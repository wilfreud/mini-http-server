package connections;

import java.net.Socket;

public class WorkerThread extends Thread{

    Socket requestSocket = null;
    public WorkerThread(Socket socket) {
        this.requestSocket = socket;
    }

    @Override
    public void run(){
        System.out.println("Incoming request");
    }
}
