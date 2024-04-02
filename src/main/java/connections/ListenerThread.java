package connections;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ListenerThread extends Thread {

    int port;
    ServerSocket serverSocket = null;


    public ListenerThread(int port){
        this.port = port;
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Failed to open listener socket::");
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.println("Server running on: 0.0.0.0:" + this.port);
        try {
            while (this.serverSocket.isBound() && !this.serverSocket.isClosed()) {
                Socket incomingReq = this.serverSocket.accept();

                // Start worker thread to handle the request then move on
                WorkerThread incomingReqHandler = new WorkerThread(incomingReq);
                incomingReqHandler.start();
            }
        } catch (IOException e) {
            System.err.println("Error handling incoming request::");
            System.err.println(e.getMessage());
        } finally {
            if (this.serverSocket != null) {
                try {
                    this.serverSocket.close();
                } catch (IOException err) {
                    System.err.println("Failed to close listener socket::");
                    System.err.println(err.getMessage());
                }
            }
        }
    }
}
