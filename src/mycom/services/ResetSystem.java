package mycom.services;

import java.util.LinkedHashMap;

import mycom.config.SystemConfig;
import mycom.models.User;
import mycom.utils.FileBackup;
import mycom.utils.FileHandler;

public class ResetSystem {
    private static FileHandler userHandler = new FileHandler(SystemConfig.userFilePath);
    private static FileHandler permissionHandler = new FileHandler(SystemConfig.rbcFilePath);
    private static FileHandler ppehandler = new FileHandler(SystemConfig.ppeItemFilePath);
    private static FileHandler lowRecordHandler = new FileHandler(SystemConfig.lowStockFilePath);
    private static FileHandler supplierHandler = new FileHandler(SystemConfig.supplierFilePath);
    private static FileHandler hospitalHandler = new FileHandler(SystemConfig.hospitalFilePath);
    private static FileHandler transactionHandler = new FileHandler(SystemConfig.transactionFilePath);
    private static FileHandler logHandler = new FileHandler(SystemConfig.logFilePath);
    private static FileHandler errorLogHandler = new FileHandler(SystemConfig.errorLogFilePath);

    public static LinkedHashMap<String, String> resetSystem(User user) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        UserManagement userServices = new UserManagement(user);
        if (userServices.getResetSystem() || user.equals(UserManagement.getSuperAdmin())) {
            if (FileBackup.systemBackup()) {
                if (userHandler.checkFileExistence()) {
                    userHandler.deleteFile();
                }
                if (permissionHandler.checkFileExistence()) {
                    permissionHandler.writeFile(SystemConfig.defaultPermissions);
                }
                if (ppehandler.checkFileExistence()) {
                    ppehandler.deleteFile();
                }
                if (lowRecordHandler.checkFileExistence()) {
                    lowRecordHandler.deleteFile();
                }
                if (supplierHandler.checkFileExistence()) {
                    supplierHandler.deleteFile();
                }
                if (hospitalHandler.checkFileExistence()) {
                    hospitalHandler.deleteFile();
                }
                if (transactionHandler.checkFileExistence()) {
                    transactionHandler.deleteFile();
                }
                if (logHandler.checkFileExistence()) {
                    logHandler.deleteFile();
                }
                if (errorLogHandler.checkFileExistence()) {
                    errorLogHandler.deleteFile();
                }
                msgMap.put("success", "true");
                msgMap.put("msg", String.format("User {%s - %s - %s} reset the system", user.type, user.getId(), user.getName()));
            } else {
                msgMap.put("success", null);
                msgMap.put("msg", String.format("User {%s - %s - %s} failed to reset the system", user.type, user.getId(), user.getName()));
            }
        } else {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("User {%s - %s - %s} failed to reset the system: System cannot be backed up", user.type, user.getId(), user.getName()));
        }
        return msgMap;      
    }
}
