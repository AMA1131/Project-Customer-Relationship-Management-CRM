package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.*;

public class LogHandler {
    private static final String logFile = "data/crm.log";
    private static final Logger logger = Logger.getLogger(LogHandler.class.getName());

    static {
        try{
            LogManager.getLogManager().reset();
            logger.setLevel(Level.ALL);

            /*ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            consoleHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(consoleHandler);*/

            FileHandler fileHandler = new FileHandler("data/crm.log", true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch(IOException e){
            throw new RuntimeException("Error in logging configuration : " + e.getMessage());
        }

    }

    public static void logInfo(String message) {
        logger.info(message);
    }

    public static void logWarning(String message) {
        logger.warning(message);
    }

    public static void logError(String message) {
        logger.severe(message);
    }

    public static void logDebug(String message) {
        logger.fine(message);
    }
}
