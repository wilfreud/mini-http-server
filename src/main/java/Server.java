import connections.ListenerThread;


public class Server extends Thread {
    public static void main(String[] args) {
        try {
            System.out.println("Starting C64-server version " + Config.APP_VERSION);

            ListenerThread mainThread = new ListenerThread(Config.SERVER_PORT);
            mainThread.start();

        }catch(Exception e){
            System.err.println("Error starting server");
            System.err.println(e.getMessage());
        }
    }
}
