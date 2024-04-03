package core;

import http.*;

import java.io.*;
import java.net.Socket;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;
import java.util.logging.Level;


public class WorkerThread extends Thread {
    private final Socket requestSocket;
    private final static Logger LOGGER = Logger.getLogger(WorkerThread.class.getName());

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

            LOGGER.info("Incoming HTTP request");
            LOGGER.info("Method: %s\t Host: %s \t URI: %s".formatted(parsedRequest.getMETHOD(), parsedRequest.getHOST(), parsedRequest.getRESOURCE_URI()));

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
                            if (URI.endsWith(".py")) {
                                fileManager.runScriptAndPutInOutputStream(output, fsNode);
                                break;
                            }
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

            } catch (IOException | AssertionError err2) {
                LOGGER.log(Level.WARNING, "Error sending client response", err2);
            }
        } catch (ParsingException err) {
            LOGGER.log(Level.WARNING, "Error parsing incoming HTTP request", err);
            final String res = Helper.generateHttpHeaders(StatusCode.INTERNAL_SERVER_ERROR_500, 0);
            try {
                output.write(res.getBytes());
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error sending client response", e);
            }
        } catch (InvalidPathException ipx) {
            LOGGER.log(Level.WARNING, "Invalid file path detected", ipx);
            final String res = Helper.generateHttpHeaders(StatusCode.BAD_REQUEST, 0);
            try {
                assert output != null;
                output.write(res.getBytes());
            } catch (IOException | AssertionError ioax) {
                LOGGER.log(Level.WARNING, "Error sending client response", ioax);
            }
        } catch (PythonExecutionException pex) {
            LOGGER.log(Level.WARNING, "Error executing script file", pex );
            try {
                final String html = "<p><em>Script execution failed. please retry or contact admin.</em></p><hr/>";
                final String res = Helper.generateSimpleResponse(StatusCode.INTERNAL_SERVER_ERROR_500.CODE, html);
                output.write(res.getBytes());
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Error sending client response", ex);
            }
        }
        catch (Exception exc) {
            LOGGER.log(Level.WARNING, "An unexpected error occured during HTTP request handling", exc);
        } finally {
            if (this.requestSocket != null) {
                try {
                    this.requestSocket.close();
                } catch (IOException err) {
                    LOGGER.log(Level.WARNING, "Error closing incoming request's socket", err);
                }
            }

            try {
                if (input != null) input.close();
            } catch (IOException err) {
                LOGGER.log(Level.WARNING, "Error closing input stream", err);
            }

            try {
                if (output != null) output.close();
            } catch (IOException err) {
                LOGGER.log(Level.WARNING, "Error closing output stream", err);
            }
        }
    }
}
