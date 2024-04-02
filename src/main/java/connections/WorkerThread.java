package connections;

import http.*;

import java.io.*;
import java.net.Socket;


public class WorkerThread extends Thread {

    Socket requestSocket;

    public WorkerThread(Socket socket) {
        this.requestSocket = socket;
    }

    @Override
    public void run() {
        System.out.println("Incoming request...");
        InputStream input = null;
        OutputStream output = null;

        try {
            // get socket streams
            input = this.requestSocket.getInputStream();
            output = this.requestSocket.getOutputStream();


            // parse incoming request for relevant information
            RequestParser parsedRequest = new RequestParser(input);

//
//            // Actions depending on request type
            final Method method = Method.valueOf(parsedRequest.getMETHOD());
            String response;
            switch (method) {
                case GET:
                    System.out.println("GET METHOD detected");
//                    // Define a default response
                    String html = "<h1>Wazzuppp!!!?!</h1>";
                    response = Helper.generateResponse(StatusCode.OK.CODE, html);


                    output.write(response.getBytes());
                    break;

                default:
                    System.out.println("In default...");
                    response = Helper.generateResponse(StatusCode.NOT_IMPLEMENTED.CODE, StatusCode.NOT_IMPLEMENTED.MESSAGE);
                    output.write(response.getBytes());
            }

            output.flush();

        } catch (IOException err) {
            try {
                final String res = Helper.generateResponse(StatusCode.INTERNAL_SERVER_ERROR_500.CODE, StatusCode.INTERNAL_SERVER_ERROR_500.MESSAGE);
                output.write(res.getBytes());
                err.printStackTrace();
            } catch (IOException err2) {
                err2.printStackTrace();
            }
        } catch (ParsingException err) {
            System.err.println(err.getMessage());
            final String res = Helper.generateResponse(StatusCode.INTERNAL_SERVER_ERROR_500.CODE, StatusCode.INTERNAL_SERVER_ERROR_500.MESSAGE);
            try {
                output.write(res.getBytes());
            } catch (IOException e) {
                System.err.println("Error sending client's response");
                System.err.println(e.getMessage());
            }
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
