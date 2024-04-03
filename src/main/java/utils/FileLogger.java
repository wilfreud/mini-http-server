package utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class FileLogger {
    private final static Logger LOGGER = Logger.getLogger(FileLogger.class.getName());

    public static void logRequest(String ip, String httpMethod, String uri) {
        try {
            // Create a file handler for logging
            FileHandler fileHandler = new FileHandler(Config.LOG_FILE_DIR, true);
            LOGGER.addHandler(fileHandler);

            // Disable logging to the console
            LOGGER.setUseParentHandlers(false);

            // custom log message format
            SimpleFormatter simpleFormatter = new SimpleFormatter() {
                private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                @Override
                public synchronized String format(LogRecord lr) {
                    return String.format("[%s] %s from %s   %s URI %s\n",
                            dateFormat.format(new Date(lr.getMillis())),
                            lr.getMessage(), ip, httpMethod, uri);
                }
            };

            // Set the custom log message format
            fileHandler.setFormatter(simpleFormatter);

            // Log the incoming HTTP request
            LOGGER.log(Level.INFO, "Incoming HTTP request", new Object[]{ip, httpMethod, uri});
            fileHandler.close();
        } catch (IOException err) {
            // Handle any errors that occur during logging
            System.err.println("Error writing to logfile: " + err.getMessage());
        }
    }
}
