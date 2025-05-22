package mycom.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class SystemConfig {
    // TXT File Path
    public static final String logFilePath = "src/txt/log/log.txt";
    public static final String errorLogFilePath = "src/txt/log/error_log.txt";
    public static final String superAdminFilePath = "src/txt/users/superAdmin.txt";
    public static final String userFilePath = "src/txt/users/users.txt";
    public static final String rbcFilePath = "src/txt/users/rbc.txt";
    public static final String ppeItemFilePath = "src/txt/inventory/ppe.txt";
    public static final String hospitalFilePath = "src/txt/entity/hospitals.txt";
    public static final String supplierFilePath = "src/txt/entity/suppliers.txt";
    public static final String transactionFilePath = "src/txt/inventory/transactions.txt";
    public static final String lowStockFilePath = "src/txt/inventory/low_stocks.txt";
    public static final String backupDir = "src/txt/backups/";
    
    // Default PPE Items
    public static final ArrayList<LinkedHashMap<String, String>> defaultPPEItems = new ArrayList<>(Arrays.asList(
        new LinkedHashMap<String, String>() {{put("itemCode", "HC"); put("itemName", "Head Cover");}},
        new LinkedHashMap<String, String>() {{put("itemCode", "FS"); put("itemName", "Face Shield");}},
        new LinkedHashMap<String, String>() {{put("itemCode", "MS"); put("itemName", "Mask");}},
        new LinkedHashMap<String, String>() {{put("itemCode", "GL"); put("itemName", "Gloves");}},
        new LinkedHashMap<String, String>() {{put("itemCode", "GW"); put("itemName", "Gown");}},
        new LinkedHashMap<String, String>() {{put("itemCode", "SC"); put("itemName", "Shoe Covers");}}
    ));
    
    // Default RBAC
    public static final ArrayList<LinkedHashMap<String, String>> defaultPermissions = new ArrayList<> (Arrays.asList(
        new LinkedHashMap<String, String>() {{put("userType", "manager"); put("CRUDmanager", "deny"); put("CRUDstaff", "allow"); put("InitializeInventory", "allow"); put("ResetInventory", "deny"); put("ResetSystem", "deny");}},
        new LinkedHashMap<String, String>() {{put("userType", "staff"); put("CRUDmanager", "deny"); put("CRUDstaff", "deny"); put("InitializeInventory", "allow"); put("ResetInventory", "deny"); put("ResetSystem", "deny");}}
    ));
    
    // Default User Type
    public static String[] userTypes = { "staff", "manager" };

    // Default Escape Delimiter
    public static final String txtDelimiter = "#|";

    // Stock Threshold Quantity
    public static final int thresholdQuantity = 25;

    // Strong Password Pattern
    public static final String pwdPattern1 = ".*\\d+.*"; // one or more digits
    public static final String pwdPattern2 = ".*[a-z]+.*"; // one or more lowercase letters
    public static final String pwdPattern3 = ".*[A-Z]+.*"; // one or more uppercase letters
    public static final String pwdPattern4 = ".*\\W+.*"; // one or more special characters
}
