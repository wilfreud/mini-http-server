package connections;

import java.io.*;
import java.net.Socket;
import java.io.File;

public class WorkerThread extends Thread{

    Socket requestSocket;
    public WorkerThread(Socket socket) {
        this.requestSocket = socket;
    }

    @Override
    public void run(){
        System.out.println("Incoming request...");
        InputStream input = null;
        OutputStream output = null;

        try{
            input = this.requestSocket.getInputStream();
            output = this.requestSocket.getOutputStream();

            final String CRLF = "\r\n"; // 13, 10

            // Define a default response
            String html = "<h1>Wazzuppp!!!?!</h1>";
            String response =
                    "HTTP/1.1 200 OK" + CRLF +
                            "Content-Length: " + html.getBytes().length + CRLF +
                            CRLF +
                            html +
                            CRLF + CRLF;


            output.write(response.getBytes());
            output.flush();

            RequestHelper.printInputStream(input);

        }catch(IOException err){
            System.err.println("Error handling incoming request");
            System.err.println(err.getMessage());
        }finally {
            if(this.requestSocket != null && !this.requestSocket.isClosed()){
                try{
                    this.requestSocket.close();
                }catch(IOException err){
                    System.err.println("Error closing incoming request socket::");
                    System.err.println(err.getMessage());
                }
            }

            try{
                if(input != null ) input.close();
            }catch(IOException err) {
                System.err.println("Error closing input stream");
                System.err.println(err.getMessage());
            }

            try{
                if(output != null) output.close();
            }catch(IOException err){
                System.err.println("Error closing output stream");
                System.err.println(err.getMessage());
            }
        }
    }
}
