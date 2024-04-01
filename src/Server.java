import connections.ListenerThread;


public class Server extends Thread {
    public static void main(String[] args) {
        try {
            System.out.println("Starting C64-server version " + Utils.APP_VERSION);

            ListenerThread mainThread = new ListenerThread(Utils.SERVER_PORT);
            mainThread.start();

        }catch(Exception e){
            System.err.println("Error starting server");
            e.printStackTrace();
        }
    }
}
