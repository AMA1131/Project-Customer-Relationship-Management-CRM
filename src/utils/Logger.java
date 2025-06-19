package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDateTime;

public class Logger {
    private static final String logFile = "data/crm.log";

    public static void log(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write(LocalDateTime.now() + " - " + message);
            writer.newLine();
        } catch (Exception e) {
            System.err.println("Error while writing in crm.log");
        }
    }
}
