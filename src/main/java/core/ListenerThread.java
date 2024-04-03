package core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class ListenerThread extends Thread {

    private final static Logger LOGGER = Logger.getLogger(ListenerThread.class.getName());

    int port;
    private ServerSocket serverSocket = null;


    public ListenerThread(int port) {
        this.port = port;
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            LOGGER.severe("Failed to open listener socket");
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    @Override
    public void run() {
        LOGGER.info("Server running on http://localhost:" + this.port);
        try {
            while (this.serverSocket.isBound() && !this.serverSocket.isClosed()) {
                Socket incomingReq = this.serverSocket.accept();

                // Start worker thread to handle the request then move on
                WorkerThread incomingReqHandler = new WorkerThread(incomingReq);
                incomingReqHandler.start();
            }
        } catch (IOException e) {
            LOGGER.warning("Error handling incoming request");
            System.err.println(e.getMessage());
        } finally {
            if (this.serverSocket != null) {
                try {
                    this.serverSocket.close();
                    LOGGER.info("Successfully closed server socket");
                } catch (IOException err) {
                    LOGGER.warning("Error closing server socket");
                    System.err.println(err.getMessage());
                }
            }
        }
    }
}
