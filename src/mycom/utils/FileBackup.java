/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mycom.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import mycom.config.SystemConfig;

/**
 *
 * @author sheaw
 */
public class FileBackup {
    private static String backupDir = SystemConfig.backupDir;
    private static FileHandler logHandler = new FileHandler(SystemConfig.logFilePath);
    private static FileHandler errorLogHandler = new FileHandler(SystemConfig.errorLogFilePath);
    private static FileHandler superAdminHandler = new FileHandler(SystemConfig.superAdminFilePath);
    private static FileHandler userHandler = new FileHandler(SystemConfig.userFilePath);
    private static FileHandler rbcHandler = new FileHandler(SystemConfig.rbcFilePath);
    private static FileHandler ppeItemHandler = new FileHandler(SystemConfig.ppeItemFilePath);
    private static FileHandler hospitalHandler = new FileHandler(SystemConfig.hospitalFilePath);
    private static FileHandler supplierHandler = new FileHandler(SystemConfig.supplierFilePath);
    private static FileHandler transactionHandler = new FileHandler(SystemConfig.transactionFilePath);
    private static FileHandler lowStockHandler = new FileHandler(SystemConfig.lowStockFilePath);
    
    public static boolean systemBackup() {
        LocalDate currentDate = DateTime.getDate(DateTime.currentDateTime());
        LocalTime currentTime = DateTime.getTime(DateTime.currentDateTime());
        String currentDateStr = DateTime.toDateString(currentDate);
        String currentTimeStr = DateTime.toTimeString(currentTime);
        String formattedDate = currentDateStr + "_[" + currentTimeStr.replace(":", "-").replace(" ", "-") + "]";
        String backUpFolderName = formattedDate + "_backup/";
        String backUpFileDir = backupDir + backUpFolderName;
        
        //users directory
        String userBackUpFileDir = backUpFileDir + "users/";
        
        // entity directory
        String entityBackUpFileDir = backUpFileDir + "entity/";
        
        // inventory directory
        String inventoryBackUpFileDir = backUpFileDir + "inventory/";
        
        // log directory
        String logBackUpFileDir = backUpFileDir + "log/";
        
        try {
            // create backup directory
            Files.createDirectories(Paths.get(backUpFileDir));
            Files.createDirectories(Paths.get(userBackUpFileDir));
            Files.createDirectories(Paths.get(entityBackUpFileDir));
            Files.createDirectories(Paths.get(inventoryBackUpFileDir));
            Files.createDirectories(Paths.get(logBackUpFileDir));
            System.out.println("Backup directory '" + backUpFileDir + "' is created!");
            Logger.log("Backup directory '" + backUpFileDir + "' is created!");
        } catch (Exception e) {
            System.out.println("Failed to create backup directory: " + e.getMessage());
            Logger.errorLog("Failed to create backup directory: " + e.getMessage());
            return false;
        }
        
        backupFile(userHandler, userBackUpFileDir + "users.txt");
        backupFile(rbcHandler, userBackUpFileDir + "rbc.txt");
        backupFile(ppeItemHandler, inventoryBackUpFileDir + "ppe.txt");
        backupFile(lowStockHandler, inventoryBackUpFileDir + "low_stocks.txt");
        backupFile(supplierHandler, entityBackUpFileDir + "suppliers.txt");
        backupFile(hospitalHandler, entityBackUpFileDir + "hospitals.txt");
        backupFile(transactionHandler, inventoryBackUpFileDir + "transactions.txt");
        backupFile(logHandler, logBackUpFileDir + "log.txt");
        backupFile(errorLogHandler, logBackUpFileDir + "error_log.txt");
        return true;
    }
    
    private static void backupFile(FileHandler handler, String backupFilePath) {
        if (handler.checkFileExistence()) {
            Path source = Paths.get(handler.getFilePath());
            Path backup = Paths.get(backupFilePath);
            try {
                Files.copy(source, backup, StandardCopyOption.REPLACE_EXISTING);
                Logger.log("'" + source.getFileName() + "' is backed up!");
            } catch (Exception e) {
                Logger.errorLog("'" + source.getFileName() + "' failed to back up: " + e.getMessage());
            }
        }
    }
}
