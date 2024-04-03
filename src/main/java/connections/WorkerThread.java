package connections;

import http.*;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;


public class WorkerThread extends Thread {
    private Socket requestSocket;
    private String BASE_DIR = "src/main/resources/htdocs";


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
            switch (method) {
                case GET:
                    // Find file
                    /*TODO
                     * try/catch path creation
                     * check if user is trying to access parent folder
                     *  */

                    final String URI = parsedRequest.getRESOURCE_URI();

                    // resource path validation
                    Path requestedPath = Paths.get(BASE_DIR, URI).normalize();
                    Path baseDirPath = Paths.get(BASE_DIR).normalize();

                    if (!requestedPath.startsWith(baseDirPath)) {
                        response = Helper.generateHttpHeaders(StatusCode.FORBIDDEN, StatusCode.FORBIDDEN.MESSAGE.length());
                        output.write(response.getBytes());
                        break;
                    }

                    File resourceFile = new File(BASE_DIR, URI);

                    // Special case: Root file
                    if (URI.isEmpty()) {
                        resourceFile = new File(BASE_DIR, "index.html");
                    }

                    // Handle case: file not found
                    if (!resourceFile.exists()) {
                        response = Helper.generateSimpleResponse(StatusCode.NOT_FOUD_404.CODE, StatusCode.NOT_FOUD_404.MESSAGE);
                        output.write(response.getBytes());
                        break;
                    } else {
                        FileManager fileManager = new FileManager();
                        // Handle case: directory
                        if (resourceFile.isDirectory()) {
                            System.out.println("dir");
                        }
                        // Handle case: simple file
                        else {
                            fileManager.putFileInOutputStream(output, resourceFile);
                        }
                    }

                    break;

                default:
                    response = Helper.generateSimpleResponse(StatusCode.NOT_IMPLEMENTED.CODE, StatusCode.NOT_IMPLEMENTED.MESSAGE);
                    output.write(response.getBytes());
            }

            output.flush();

        } catch (IOException err) {
            try {
                final String res = Helper.generateSimpleResponse(StatusCode.INTERNAL_SERVER_ERROR_500.CODE, StatusCode.INTERNAL_SERVER_ERROR_500.MESSAGE);
                output.write(res.getBytes());
                System.err.println(err.getMessage());
            } catch (IOException err2) {
                System.err.println(err2.getMessage());
            }
        } catch (ParsingException err) {
            System.err.println(err.getMessage());
            final String res = Helper.generateSimpleResponse(StatusCode.INTERNAL_SERVER_ERROR_500.CODE, StatusCode.INTERNAL_SERVER_ERROR_500.MESSAGE);
            try {
                output.write(res.getBytes());
            } catch (IOException e) {
                System.err.println("Error sending client's response");
                System.err.println(e.getMessage());
            }
        } catch (Exception exc) {
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
