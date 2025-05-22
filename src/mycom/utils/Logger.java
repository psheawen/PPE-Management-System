package mycom.utils;

import java.io.FileWriter;
import java.io.IOException;
import mycom.config.SystemConfig;

public class Logger {
    private static final String logFilePath = SystemConfig.logFilePath;
    private static final String errorLogFilePath = SystemConfig.errorLogFilePath;
    private static final FileHandler logHandler = new FileHandler(logFilePath);
    private static final FileHandler errorLogHandler = new FileHandler(errorLogFilePath);

    public static void log(String activity) {
        String timeStamp = DateTime.formattedLocalDateTime(DateTime.currentDateTime());

        try {
            if (!logHandler.checkFileExistence()) {
                System.out.println(logHandler.createFile());
            }

            try (FileWriter writer = new FileWriter(logFilePath, true)) {
                writer.write(String.format("[%s] %s%n", timeStamp, activity));
                writer.flush();
            }
        } catch (IOException e) {
            errorLog("Error writing to log file -> " + e.getMessage());
        }
    }

    public static void errorLog(String activity) {
        String timeStamp = DateTime.formattedLocalDateTime(DateTime.currentDateTime());

        try {
            if (!errorLogHandler.checkFileExistence()) {
                System.out.println(errorLogHandler.createFile());
            }

            try (FileWriter writer = new FileWriter(errorLogFilePath, true)) {
                writer.write(String.format("[%s] %s%n", timeStamp, activity));
                writer.flush();
            }
        } catch (IOException e) {
            System.out.println("Error writing to error log file -> " + e.getMessage());
        }
    }

    static void log() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
