package core;

import http.*;

import java.io.*;
import java.net.Socket;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class WorkerThread extends Thread {
    private final Socket requestSocket;


    public WorkerThread(Socket socket) {
        this.requestSocket = socket;
    }

    @Override
    public void run() {
        InputStream input = null;
        OutputStream output = null;

        try {
            // get socket streams
            input = this.requestSocket.getInputStream();
            output = this.requestSocket.getOutputStream();


            // parse incoming request for relevant information
            RequestParser parsedRequest = new RequestParser(input);

//            // Actions depending on request type
            final Method method = Method.valueOf(parsedRequest.getMETHOD());
            String response;
            final String BASE_DIR = FileManager.BASE_DIR;
            final Path BASE_DIR_PATH = FileManager.BASE_DIR_PATH;
            switch (method) {
                case GET:
                    // Find file

                    final String URI = parsedRequest.getRESOURCE_URI();

                    // resource path validation
                    Path requestedPath = Paths.get(BASE_DIR, URI).normalize();

                    if (!requestedPath.startsWith(BASE_DIR_PATH)) {
                        response = Helper.generateHttpHeaders(StatusCode.FORBIDDEN, 0);
                        output.write(response.getBytes());
                        break;
                    }

                    File fsNode = new File(BASE_DIR, URI);

                    // Handle case: file not found
                    if (!fsNode.exists()) {
                        response = Helper.generateHttpHeaders(StatusCode.NOT_FOUD_404, 0);
                        output.write(response.getBytes());
                        break;
                    } else {
                        FileManager fileManager = new FileManager();
                        // Handle case: directory
                        if (fsNode.isDirectory()) {
                            fileManager.putDirectoryContentInOutputStream(output, fsNode, URI);
                        }
                        // Handle case: simple file
                        else {
                            fileManager.putFileInOutputStream(output, fsNode);
                        }
                    }

                    break;

                default:
                    response = Helper.generateHttpHeaders(StatusCode.NOT_IMPLEMENTED, 0);
                    output.write(response.getBytes());
            }

            output.flush();

        } catch (IOException err) {
            try {
                final String res = Helper.generateHttpHeaders(StatusCode.INTERNAL_SERVER_ERROR_500, 0);
                assert output != null;
                output.write(res.getBytes());
                System.err.println(err.getMessage());
            } catch (IOException | AssertionError err2) {
                System.err.println(err2.getMessage());
            }
        } catch (ParsingException err) {
            System.err.println(err.getMessage());
            final String res = Helper.generateHttpHeaders(StatusCode.INTERNAL_SERVER_ERROR_500, 0);
            try {
                output.write(res.getBytes());
            } catch (IOException e) {
                System.err.println("Error sending client's response");
                System.err.println(e.getMessage());
            }
        } catch (InvalidPathException ipx) {
            System.err.println(ipx.getMessage());
            final String res = Helper.generateHttpHeaders(StatusCode.BAD_REQUEST,0);
            try{
                assert output != null;
                output.write(res.getBytes());
            }catch (IOException | AssertionError ioax){
                System.err.println("Error sending BAD_REQUEST response: " + ioax.getMessage());
            }
        } catch (Exception exc) {
            System.err.println("AN unexpected error occured:: ");
            exc.printStackTrace();
        } finally {
            if (this.requestSocket != null) {
                try {
                    this.requestSocket.close();
                } catch (IOException err) {
                    System.err.println("Error closing incoming request socket::");
                    System.err.println(err.getMessage());
                }
            }

            try {
                if (input != null) input.close();
            } catch (IOException err) {
                System.err.println("Error closing input stream");
                System.err.println(err.getMessage());
            }

            try {
                if (output != null) output.close();
            } catch (IOException err) {
                System.err.println("Error closing output stream");
                System.err.println(err.getMessage());
            }
        }
    }
}
