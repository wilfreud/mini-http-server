import ui.MainWindow;
import utils.Config;
import core.ListenerThread;
import java.util.logging.Logger;


public class Server extends Thread {
    private final static Logger LOGGER = Logger.getLogger(Server.class.getName());

    public static void main(String[] args) {
        try {

            LOGGER.info("Starting Commodore64-server version %s ...".formatted(Config.APP_VERSION));

            ListenerThread mainThread = new ListenerThread(Config.SERVER_PORT);
            mainThread.start();
//

            // open main window
//            MainWindow mainWindow = new MainWindow(mainThread);

        }catch(Exception e){
            LOGGER.severe("Server failed to start");
            System.err.println(e.getMessage());
        }
    }

}
