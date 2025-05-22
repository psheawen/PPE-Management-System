package mycom.services;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import mycom.config.SystemConfig;
import mycom.models.Hospital;
// Internal Library =D
import mycom.models.PPEItem;
import mycom.models.Supplier;
import mycom.models.User;
import mycom.utils.DateTime;
import mycom.utils.ErrorHandler;
import mycom.utils.FileHandler;
import mycom.utils.Logger;
import mycom.utils.exceptions.PPEItemException;
import mycom.utils.exceptions.UserException;

public class PPEManagement {
    private static FileHandler ppehandler = new FileHandler(SystemConfig.ppeItemFilePath);
    private static FileHandler supplierHandler = new FileHandler(SystemConfig.supplierFilePath);
    private static FileHandler transactionHandler = new FileHandler(SystemConfig.transactionFilePath);
    private static FileHandler lowStockHandler = new FileHandler(SystemConfig.lowStockFilePath);
    private static List<PPEItem> allItems = new ArrayList<>();
    private static List<Supplier> allSuppliers = new ArrayList<>();
    private static ArrayList<LinkedHashMap<String, String>> lowStockItems = new ArrayList<>();

    public PPEManagement() {
        loadPPEItems();
        loadSuppliers();
        updateLowStockItems();
    }

    public void loadPPEItems() {
        allItems = new ArrayList<>();
        if (ppehandler.checkFileExistence()) {
            ArrayList<LinkedHashMap<String, String>> itemsMap = ppehandler.readFile();
            for (LinkedHashMap<String, String> itemMap : itemsMap) {
                PPEItem item = new PPEItem(itemMap.get("itemCode"), itemMap.get("itemName"), itemMap.get("lastRestockDateTime"));
                item.setSupplier(itemMap.get("supplierCode"));
                item.setStockQuantity(Integer.parseInt(itemMap.get("stockQuantity")));
                allItems.add(item);
            }
        }
    }

    public void loadSuppliers() {
        allSuppliers = new ArrayList<>();
        SupplierManagement supplierServices = new SupplierManagement();
        allSuppliers = supplierServices.getAllSuppliers();
    }

    public static void updateLowStockItems() {
        List<PPEItem> items = new ArrayList<>();
        for (PPEItem ppeItem : allItems) {
            if (ppeItem.getQuantity() < SystemConfig.thresholdQuantity) {
                items.add(ppeItem);
            }
        }
        if (lowStockHandler.checkFileExistence()) {
            ArrayList<LinkedHashMap<String, String>> storedLowStockItems = lowStockHandler.readFile();
            ArrayList<LinkedHashMap<String, String>> newLowStockItems = new ArrayList<>();
            // Remove Back to Normal Items - Add Still Low Stock Items
            for (LinkedHashMap<String, String> itemMap: storedLowStockItems) {
                if (items.stream().anyMatch(item -> item.getCode().equals(itemMap.get("itemCode")))) {
                    newLowStockItems.add(itemMap);
                }
            }
            // Add New Low Stock Items
            for (PPEItem ppeItem: items) {
                if (!storedLowStockItems.stream().anyMatch(itemMap -> itemMap.get("itemCode").equals(ppeItem.getCode()))) {
                    LinkedHashMap<String, String> itemLowStockDetail = new LinkedHashMap<>();
                    itemLowStockDetail.put("itemCode", ppeItem.getCode());
                    itemLowStockDetail.put("resolutionStatus", "waiting");
                    itemLowStockDetail.put("staffInvolved", "-");
                    newLowStockItems.add(itemLowStockDetail);
                }
            }
            lowStockHandler.writeFile(newLowStockItems);
        } else {
            if (items.size() > 0) {
                for (PPEItem ppeItem : items) {
                    LinkedHashMap<String, String> itemLowStockDetail = new LinkedHashMap<>();
                    if (ppeItem.getQuantity() < SystemConfig.thresholdQuantity) {
                        itemLowStockDetail.put("itemCode", ppeItem.getCode());
                        itemLowStockDetail.put("resolutionStatus", "waiting");
                        itemLowStockDetail.put("staffInvolved", "-");
                        lowStockItems.add(itemLowStockDetail);
                    }
                }
                lowStockHandler.writeFile(lowStockItems);
            }
        }
        if (lowStockHandler.checkFileExistence()) {
            loadLowStockItems();
        }
    }

    public static void loadLowStockItems() {
        lowStockItems = new ArrayList<>();
        ArrayList<LinkedHashMap<String, String>> storedLowStockItems = lowStockHandler.readFile();
        for (LinkedHashMap<String, String> itemMap: storedLowStockItems) {
            lowStockItems.add(itemMap);
        }
    }

    public LinkedHashMap<String, String> resetInventory(User user) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        UserManagement userServices = new UserManagement(user);
        if (userServices.getResetInventory() || user.equals(UserManagement.getSuperAdmin())) {
            if (ppehandler.checkFileExistence() && ppehandler.deleteFile()) {
                initializeInventory();
                if (transactionHandler.checkFileExistence() && transactionHandler.deleteFile()) {
                    if (lowStockHandler.checkFileExistence() && lowStockHandler.deleteFile()) {
                        msgMap.put("success", "true");
                        msgMap.put("msg", "PPE inventory is reset -> Transaction records and low stock item records are deleted");
                        loadPPEItems();
                        loadSuppliers();
                        updateLowStockItems();
                        loadLowStockItems();
                    } else {
                        msgMap.put("success", "true");
                        msgMap.put("msg", "PPE inventory is reset -> Transaction records are deleted\n\tBut low stock item records are not deleted or does not exists");
                    }
                        
                } else {
                    msgMap.put("success", "true");
                    msgMap.put("msg", "PPE inventory is reset -> But transaction records are not deleted or does not exists");
                }
            } else {
                msgMap.put("success", null);
                msgMap.put("msg", "PPE inventory is not reset, , 'ppe.txt' does not exists");
            }
        } else {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("User {%s - %s - %s} failed to reset the PPE inventory", user.type, user.getId(), user.getName()));
        }
        return msgMap;
    }

    public LinkedHashMap<String, String> initializeInventory() {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        if (lowStockHandler.checkFileExistence()) {
            lowStockHandler.deleteFile();
            Logger.log("Low stock items records 'low_stocks.txt' is deleted");
        }
        if (!ppehandler.checkFileExistence() && supplierHandler.checkFileExistence()) {
            if (ppehandler.createFile()) {
                LinkedHashMap<String, String> msg = setDefaultPPEItems();
                if (msg.get("success") != null) {
                    writeInventory();
                }
                return msg;
            } else {
                msgMap.put("success", null);
                msgMap.put("msg", "PPE inventory is not initialized, failed to create 'ppe.txt'");
            }
        } else if (!ppehandler.checkFileExistence()) {
            msgMap.put("success", null);
            msgMap.put("msg",
                    "PPE inventory is not initialized, 'supplier.txt' needs to be created to initialize inventory");
        } else {
            msgMap.put("success", null);
            msgMap.put("msg", "PPE inventory is not initialized, 'ppe.txt' already exists");
        }
        return msgMap;
    }

    // public LinkedHashMap<String, String> deleteInventory(User user) {
    //     LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
    //     UserManagement userServices = new UserManagement(user);
    //     if (userServices.getDeleteInventory() || user.equals(UserManagement.getSuperAdmin())) {
    //         if (ppehandler.checkFileExistence() && ppehandler.deleteFile()) {
    //             if (transactionHandler.checkFileExistence() && transactionHandler.deleteFile()) {
    //                 msgMap.put("success", "true");
    //                 msgMap.put("msg", "PPE inventory is deleted\nTransaction records are deleted");
    //             } else if (!transactionHandler.checkFileExistence()) {
    //                 msgMap.put("success", "true");
    //                 msgMap.put("msg", "PPE inventory is deleted\nNo transaction records");
    //             } else {
    //                 msgMap.put("success", null);
    //                 msgMap.put("msg", "PPE inventory is deleted\nBut transaction records are not deleted");
    //             }
    //         } else {
    //             msgMap.put("success", null);
    //             msgMap.put("msg", "PPE inventory is not deleted, , 'ppe.txt' does not exists");
    //         }
    //     } else {
    //         msgMap.put("success", null);
    //         msgMap.put("msg", String.format("User {%s - %s -%s} has no permission to delete the PPE inventory", user.type, user.getId(), user.getName()));
    //     }
    //     return msgMap;
    // }

    public LinkedHashMap<String, String> setDefaultPPEItems() {
        allItems = new ArrayList<>(); // clear items array list
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        if (supplierHandler.checkFileExistence()) {
            String datetime = DateTime.formattedLocalDateTime(DateTime.currentDateTime());
            for (LinkedHashMap<String, String> itemMap: SystemConfig.defaultPPEItems) {
                allItems.add(new PPEItem(itemMap.get("itemCode"), itemMap.get("itemName"), datetime));
            }
            msgMap.put("success", "true");
            msgMap.put("msg", "PPE Items are initialized");
        } else {
            msgMap.put("success", null);
            msgMap.put("msg",
                    "PPE Items are not initialized, 'supplier.txt' needs to be created to initialize inventory");
        }
        return msgMap;
    }

    public boolean writeInventory() {
        ArrayList<LinkedHashMap<String, String>> items = new ArrayList<>();
        for (PPEItem item : allItems) {
            items.add(item.getItemMap());
        }
        ppehandler.writeFile(items);
        return true;
    }

    public static List<PPEItem> getAllItems() {
        return allItems;
    }

    public static ArrayList<LinkedHashMap<String, String>> getLowStockItems() {
        updateLowStockItems();
        loadLowStockItems();
        return lowStockItems;
    }

    public static PPEItem getPPEItemByCode(String itemCode) {
        try {
            if (!allItems.stream().anyMatch(item -> item.getCode().equals(itemCode))) {
                throw new PPEItemException(
                        String.format("Invalid PPE item Code: PPE item code '%s' is not found", itemCode));
            }
            for (PPEItem ppeItem : allItems) {
                if (ppeItem.getCode().equals(itemCode)) {
                    return ppeItem;
                }
            }
            return null;
        } catch (PPEItemException e) {
            System.out.println("Error finding PPE item->" + e.getMessage());
            return null;
        }
    }

    public static List<PPEItem> sortItemsByCode() {
        List<PPEItem> sortedItems = allItems;
        sortedItems.sort(Comparator.comparing(PPEItem::getCode));
        return sortedItems;
    }
    
    public static List<PPEItem> sortGivenItemList(List<PPEItem> list) {
        List<PPEItem> sortedItems = list;
        sortedItems.sort(Comparator.comparing(PPEItem::getCode));
        return sortedItems;
    }

    public LinkedHashMap<String, String> addPPEItems(String itemCode, String itemName, String supplierCode,
            int stockQuantity) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            ErrorHandler.ppeErrorHandler(Arrays.asList("itemCode", "itemName"), itemCode, itemName);
            if (stockQuantity != 0) {
                ErrorHandler.ppeErrorHandler(Arrays.asList("stockQuantity"), stockQuantity);
            }
            if (!supplierCode.equals("") && supplierCode != null) {
                ErrorHandler.ppeErrorHandler(Arrays.asList("supplierCode"), supplierCode);
            }
        } catch (PPEItemException e) {
            System.out.println("Error adding new PPE item -> " + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", String.format("PPE Item '%s' is not created", itemName) + e.getMessage());
            return msgMap;
        }
        String datetime = DateTime.formattedLocalDateTime(DateTime.currentDateTime());
        PPEItem newItem = new PPEItem(itemCode, itemName, datetime);
        if (!supplierCode.equals("") && supplierCode != null) {
            newItem.setSupplier(supplierCode);
        }
        if (stockQuantity != 0) {
            newItem.setStockQuantity(stockQuantity);
        }
        if (appendNewPPEItem(newItem)) {
            loadPPEItems();
            msgMap.put("success", "true");
            msgMap.put("msg", String.format("PPE Item '%s' is created and added!", itemName));
            msgMap.put("newItemCode", itemCode);
        }
        return msgMap;
    }

    public LinkedHashMap<String, String> modifyItemName(String newName, String itemCode) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            ErrorHandler.ppeErrorHandler(Arrays.asList("itemName"), newName);
        } catch (PPEItemException e) {
            System.out.println("Error modifying PPE item name -> " + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", String.format("PPE item '%s''s name is not modified -> ", itemCode) + e.getMessage());
            return msgMap;
        }
        if (allItems.stream().anyMatch(item -> item.getCode().equals(itemCode))) {
            PPEItem modifyItem = new PPEItem();
            for (PPEItem ppeItem : allItems) {
                if (ppeItem.getCode().equals(itemCode)) {
                    modifyItem = ppeItem;
                    break;
                }
            }
            modifyItem.modifyItemName(newName);
            writeInventory();
            loadPPEItems();
            msgMap.put("success", "true");
            msgMap.put("msg", String.format("PPE item '%s' name is modified!", itemCode));
        } else {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("PPE item '%s' is not found, name cannot modified:",
                    itemCode));
        }
        return msgMap;
    }

    public LinkedHashMap<String, String> setExactPPEItemQuantity(String itemCode, int quantity) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        PPEItem item = getPPEItemByCode(itemCode);
        if (item == null) {
            msgMap.put("success", null);
            msgMap.put("msg",
                    String.format(
                            "Stock quantity of PPE item {%s - ?} failed to be set to %d boxes -> Invalid Item Code",
                            itemCode, quantity));
        }
        if (item.setStockQuantity(quantity)) {
            writeInventory();
            loadPPEItems();
            loadSuppliers();
            updateLowStockItems();
            loadLowStockItems();
            msgMap.put("success", "true");
            msgMap.put("msg", String.format("Stock quantity of PPE item {%s - %s} is set to %d boxes", item.getCode(),
                    item.getName(), quantity));
        } else {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Stock quantity of PPE item {%s - %s} failed to be set to %d boxes",
                    item.getCode(), item.getName(), quantity));
        }
        return msgMap;
    }

    public LinkedHashMap<String, String> removePPEItems(String itemCode) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        if (allItems.stream().anyMatch(item -> item.getCode().equals(itemCode))) {
            PPEItem deleteItem = getPPEItemByCode(itemCode);
            allItems.removeIf(item -> item.getCode().equals(itemCode));
            writeInventory();
            loadPPEItems();
            loadSuppliers();
            updateLowStockItems();
            msgMap.put("success", "true");
            msgMap.put("msg", String.format("PPE item '%s' is removed!", itemCode));
            msgMap.put("deleteItemCode", itemCode);
            msgMap.put("deleteItemName", deleteItem.getName());
        } else {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("PPE item '%s' is not found, cannot be removed", itemCode));
        }
        return msgMap;
    }

    public LinkedHashMap<String, String> assignSupplier(String itemCode, String supplierCode) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        PPEItem item = new PPEItem();
        try {
            ErrorHandler.ppeErrorHandler(Arrays.asList("checkItemCodeExistence"), itemCode);
            // if (!allItems.stream().anyMatch(ppeItem ->
            // ppeItem.getCode().equals(itemCode))) {
            // throw new PPEItemException("Invalid PPE item code: PPE item not found");
            // }
            for (PPEItem ppeItem : allItems) {
                if (ppeItem.getCode().equals(itemCode)) {
                    item = ppeItem;
                    break;
                }
            }
            ErrorHandler.ppeErrorHandler(Arrays.asList("supplierCode", "PPEItem"), supplierCode, item);
        } catch (PPEItemException e) {
            System.out.println("Error assigning supplier to PPE item\n" + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg",
                    String.format("PPE Item '%s' supplier code is not updated\n", item.getName()) + e.getMessage());
            return msgMap;
        }
        for (PPEItem ppeItem : allItems) {
            if (ppeItem.equals(item)) {
                if (ppeItem.setSupplier(supplierCode)) {
                    break;
                }
            }
        }
        writeInventory();
        loadPPEItems();
        msgMap.put("success", "true");
        msgMap.put("msg", String.format("PPE Item '%s' supplier code is updated!", item.getName()));
        return msgMap;
    }

    // used PPEItem as argument
    public LinkedHashMap<String, String> receiveStock(PPEItem item, int increaseQuantity) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            ErrorHandler.ppeErrorHandler(
                    Arrays.asList("stockQuantityReceive", "newQuantity", "PPEItem", "itemSupplier"), item.getQuantity(),
                    increaseQuantity, item, item);
        } catch (PPEItemException e) {
            System.out.println("Error receiving PPE item stock -> " + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg",
                    String.format("PPE Item '%s' new stock is not received -> ", item.getName()) + e.getMessage());
            return msgMap;
        }
        for (PPEItem ppeItem : allItems) {
            if (ppeItem.equals(item)) {
                if (ppeItem.addStock(increaseQuantity)) {
                    String currentDatetime = DateTime.currentDateTime().toString();
                    ppeItem.updateLastRestockDateTime(currentDatetime);
                    break;
                }
            }
        }
        Supplier supplier = new Supplier();
        for (Supplier sup : allSuppliers) {
            if (sup.getCode().equals(item.getSupplier())) {
                supplier = sup;
            }
        }
        writeInventory();
        updateLowStockItems();
        recordTransactions("receive", increaseQuantity, item, supplier);
        msgMap.put("success", "true");
        msgMap.put("msg",
                String.format("PPE Item '%s' new stock is received from Supplier '%s'! (+ %d boxes)", item.getName(),
                        supplier.getName(), increaseQuantity));
        return msgMap;
    }

    // used PPEItem as argument
    public LinkedHashMap<String, String> distributeStock(PPEItem item, int decreaseQuantity, Hospital hospital) {
        // IMPORTANT !!! -> might need to save transactions to transaction.txt hereee
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            ErrorHandler.ppeErrorHandler(
                    Arrays.asList("stockQuantityDistribute", "newQuantity", "PPEItem", "Hospital", "itemSupplier"),
                    item.getQuantity(), decreaseQuantity, item, hospital, item);
            if (item.getQuantity() < decreaseQuantity) {
                throw new PPEItemException("Not enough stock to distribute");
            }
        } catch (PPEItemException e) {
            System.out.println("Error distributing PPE item stock -> " + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", String.format("PPE Item '%s' is not distributed -> ", item.getName()) + e.getMessage());
            return msgMap;
        }
        for (PPEItem ppeItem : allItems) {
            if (ppeItem.equals(item)) {
                if (ppeItem.subtractStock(decreaseQuantity)) {
                    break;
                }
            }
        }
        writeInventory();
        recordTransactions("distribute", decreaseQuantity, item, hospital);
        updateLowStockItems();
        msgMap.put("success", "true");
        msgMap.put("msg",
                String.format("PPE Item '%s' is distributed to Hospital '%s'! (- %d boxes)", item.getName(),
                        hospital.getName(), decreaseQuantity));
        return msgMap;
    }

    public void recordTransactions(String type, int quantity, PPEItem item, Object entity) {
        // item code, supplier code/Hospital code, quantity received/quantity
        // distributed, and date-time.
        try {
            ArrayList<LinkedHashMap<String, String>> transactionDetails = new ArrayList<>();
            LinkedHashMap<String, String> transaction = new LinkedHashMap<>();
            if (quantity < 0) {
                throw new PPEItemException(
                        "Invalid stock quantity: Stock quantity distributed or received cannot be negative or zero");
            }
            if (type.equalsIgnoreCase("distribute")) {
                if (!(entity instanceof Hospital)) {
                    throw new PPEItemException("Invalid entity: Stock can only be distributed to Hospital");
                }
                String itemCode = item.getCode();
                String hospitalCode = ((Hospital) entity).getCode();
                String quantityDistributed = "-" + String.valueOf(quantity);
                // DateTime dateTime = new DateTime();
                String currentFormattedDateTime = DateTime.formattedLocalDateTime(DateTime.currentDateTime());
                transaction.put("itemCode", itemCode);
                transaction.put("supplierCode/hospitalCode", hospitalCode);
                transaction.put("quantityReceived/quantityDistributed", quantityDistributed);
                transaction.put("date-time", currentFormattedDateTime);
                transactionDetails.add(transaction);
            } else if (type.equalsIgnoreCase("receive")) {
                if (!(entity instanceof Supplier)) {
                    throw new PPEItemException("Invalid entity: Stock can only be received from Supplier");
                }
                String itemCode = item.getCode();
                String supplierCode = ((Supplier) entity).getCode();
                String quantityReceived = "+" + String.valueOf(quantity);
                // DateTime dateTime = new DateTime();
                String currentFormattedDateTime = DateTime.formattedLocalDateTime(DateTime.currentDateTime());
                transaction.put("itemCode", itemCode);
                transaction.put("supplierCode/hospitalCode", supplierCode);
                transaction.put("quantityReceived/quantityDistributed", quantityReceived);
                transaction.put("date-time", currentFormattedDateTime);
                transactionDetails.add(transaction);
            } else {
                throw new PPEItemException("Invalid transaction type: can only be 'receive' or 'distribute'");
            }
            if (!transactionHandler.checkFileExistence() && transactionHandler.createFile()) {
                transactionHandler.writeFile(transactionDetails);
            } else if (transactionHandler.checkFileExistence()) {
                transactionHandler.appendFile(transactionDetails);
            }
        } catch (PPEItemException e) {
            System.out.println("Error recording stock transactions -> " + e.getMessage());
            Logger.errorLog("Error recording stock transactions -> " + e.getMessage());
        }
    }

    public ArrayList<LinkedHashMap<String, String>> totalStockReceived(String itemCode, Date startDate, Date endDate) {
        ArrayList<LinkedHashMap<String, String>> stockReceived = new ArrayList<>();
        if (transactionHandler.checkFileExistence()) {
            try {
                ErrorHandler.ppeErrorHandler(Arrays.asList("checkItemCodeExistence"), itemCode);
                LinkedHashMap<String, String> stockReceivedByEachSupplier = new LinkedHashMap<>();
                ArrayList<LinkedHashMap<String, String>> transactions = transactionHandler.readFile();
                int total = 0;
                for (LinkedHashMap<String, String> transaction : transactions) {
                    if (transaction.get("itemCode").equals(itemCode)) {
                        if (transaction.get("quantityReceived/quantityDistributed").contains("+")) {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                Date transactionDate = sdf.parse(transaction.get("date-time"));
                                if ((transactionDate.compareTo(startDate) >= 0) && (transactionDate.compareTo(endDate) <= 0)) {
                                    int quantityReceived = Integer
                                            .parseInt(transaction.get("quantityReceived/quantityDistributed").split("\\+")[1]);
                                    if (stockReceived.stream().anyMatch(item -> item.get("supplierCode")
                                            .equals(transaction.get("supplierCode/hospitalCode")))) {
                                        int stockIndex = 0;
                                        for (int i = 0; i < stockReceived.size(); i++) {
                                            if (stockReceived.get(i).get("supplierCode")
                                                    .equals(transaction.get("supplierCode/hospitalCode"))) {
                                                stockIndex = i;
                                                break;
                                            }
                                        }
                                        total = Integer.parseInt(stockReceived.get(stockIndex).get("totalStockReceived"));
                                        total += quantityReceived;
                                        stockReceived.get(stockIndex).put("totalStockReceived", String.valueOf(total));
                                    } else {
                                        stockReceivedByEachSupplier = new LinkedHashMap<>();
                                        total = quantityReceived;
                                        stockReceivedByEachSupplier.put("supplierCode",
                                                transaction.get("supplierCode/hospitalCode"));
                                        stockReceivedByEachSupplier.put("totalStockReceived", String.valueOf(total));
                                        stockReceived.add(stockReceivedByEachSupplier);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (PPEItemException e) {
                System.out.println("Error getting total stock received -> " + e.getMessage());
                Logger.errorLog("Error getting total stock received -> " + e.getMessage());
            }
        }
        return stockReceived;
    }

    public ArrayList<LinkedHashMap<String, String>> totalStockDistributed(String itemCode, Date startDate, Date endDate) {
        ArrayList<LinkedHashMap<String, String>> stockDistributed = new ArrayList<>();
        if (transactionHandler.checkFileExistence()) {
            try {
                ErrorHandler.ppeErrorHandler(Arrays.asList("checkItemCodeExistence"), itemCode);
                LinkedHashMap<String, String> stockDistributedToEachHospital = new LinkedHashMap<>();
                ArrayList<LinkedHashMap<String, String>> transactions = transactionHandler.readFile();
                int total = 0;
                for (LinkedHashMap<String, String> transaction : transactions) {
                    if (transaction.get("itemCode").equals(itemCode)) {
                        if (transaction.get("quantityReceived/quantityDistributed").contains("-")) {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                Date transactionDate = sdf.parse(transaction.get("date-time"));
                                if ((transactionDate.compareTo(startDate) >= 0) && (transactionDate.compareTo(endDate) <= 0)) {
                                    int quantityDistributed = Integer
                                            .parseInt(transaction.get("quantityReceived/quantityDistributed").split("\\-")[1]);
                                    if (stockDistributed.stream().anyMatch(item -> item.get("hospitalCode")
                                            .equals(transaction.get("supplierCode/hospitalCode")))) {
                                        int stockIndex = 0;
                                        for (int i = 0; i < stockDistributed.size(); i++) {
                                            if (stockDistributed.get(i).get("hospitalCode")
                                                    .equals(transaction.get("supplierCode/hospitalCode"))) {
                                                stockIndex = i;
                                                break;
                                            }
                                        }
                                        total = Integer.parseInt(stockDistributed.get(stockIndex).get("totalStockDistributed"));
                                        total += quantityDistributed;
                                        stockDistributed.get(stockIndex).put("totalStockDistributed", String.valueOf(total));
                                    } else {
                                        stockDistributedToEachHospital = new LinkedHashMap<>();
                                        total = quantityDistributed;
                                        stockDistributedToEachHospital.put("hospitalCode",
                                                transaction.get("supplierCode/hospitalCode"));
                                        stockDistributedToEachHospital.put("totalStockDistributed", String.valueOf(total));
                                        stockDistributed.add(stockDistributedToEachHospital);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (PPEItemException e) {
                System.out.println("Error getting total stock distributed -> " + e.getMessage());
                Logger.errorLog("Error getting total stock distributed -> " + e.getMessage());
            }
        }
        return stockDistributed;
    }
    
    

    public List<PPEItem> searchItem(String keywords) {
        List<PPEItem> matchedItems = new ArrayList<>();
        matchedItems = new ArrayList<>();
        for (PPEItem item : allItems) {
            if (item.getCode().toLowerCase().contains(keywords.toLowerCase().trim())
                    || item.getName().toLowerCase().contains(keywords.toLowerCase().trim())) {
                matchedItems.add(item);
            }
        }
        return matchedItems;
    }

    public boolean appendNewPPEItem(PPEItem item) {
        try {
            ArrayList<LinkedHashMap<String, String>> items = new ArrayList<>();
            items.add(item.getItemMap());
            if (!ppehandler.checkFileExistence()) {
                System.out.printf("File 'ppe.txt' does not exists, PPE item '%s' cannot be appended%n",
                        item.getName());
                Logger.errorLog(String.format(
                        "Error appending new PPE item (System Error): File 'ppe.txt' does not exists, PPE item '%s' cannot be appended%n",
                        item.getName()));
                return false;
            }
            ppehandler.appendFile(items);
            System.out.printf("PPE item '%s' is appended to ppe.txt!%n", item.getName());
            return true;
        } catch (Exception e) {
            System.out.println("Error appending new PPE item (System Error) -> " + e.getMessage());
            Logger.errorLog("Error appending new PPE item (System Error) -> " + e.getMessage());
            return false;
        }
    }

    public static ArrayList<LinkedHashMap<String, String>> getTransactionRecords() {
        FileHandler handler = new FileHandler(SystemConfig.transactionFilePath);
        ArrayList<LinkedHashMap<String, String>> transactionRecords = new ArrayList<>();
//        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        if (handler.checkFileExistence()) {
            ArrayList<LinkedHashMap<String, String>> transactions = handler.readFile();
            for (LinkedHashMap<String, String> transaction: transactions) {
                LinkedHashMap<String, String> map = new LinkedHashMap<>();
                if (transaction.get("quantityReceived/quantityDistributed").contains("+")) {
                    map.put("transactionType", "received");
                    map.put("quantity", transaction.get("quantityReceived/quantityDistributed").split("\\+")[1]);
                } else if (transaction.get("quantityReceived/quantityDistributed").contains("-")) {
                    
                    map.put("transactionType", "distributed");
                    map.put("quantity", transaction.get("quantityReceived/quantityDistributed").split("\\-")[1]);
                }
                LocalDateTime datetime = DateTime.toDateTimeObject(transaction.get("date-time"));
                LocalDate date = DateTime.getDate(datetime);
                String dateString = DateTime.toDateString(date);
                map.put("itemCode", transaction.get("itemCode"));
                map.put("date", dateString);
                map.put("entityInvolved", transaction.get("supplierCode/hospitalCode"));
                transactionRecords.add(map);
            }
        } else {
            transactionRecords = null;
        }
        return transactionRecords;
    }

    // used PPE item as argument
    public LinkedHashMap<String, String> updateLowStockItemStatus(PPEItem item, String status, String staffId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            User staff = UserManagement.getUserById(staffId);
            if (item == null || staff == null) {
                throw new Exception("Item or User is null");
            }
            msgMap.put("success", null);
            msgMap.put("msg",
                    String.format("Low stock resolution status for PPE item '%s' is not updated", item.getName(), status) + "\n");
            ErrorHandler.userErrorHandler(Arrays.asList("staff"), staff);
            ErrorHandler.ppeErrorHandler(Arrays.asList("PPEItem", "lowStock"), item, item);
            for (LinkedHashMap<String, String> lowStockDetails : lowStockItems) {
                if (lowStockDetails.get("itemCode").equals(item.getCode())) {
                    lowStockDetails.put("resolutionStatus", status);
                    lowStockDetails.put("staffInvolved", staffId);
                    msgMap.put("success", "true");
                    msgMap.put("msg",
                            String.format("Low stock resolution status for PPE item '%s' is updated to '%s'",
                                    item.getName(), status));
                    lowStockHandler.writeFile(lowStockItems);
                    break;
                }
            }
        } catch (PPEItemException pe) {
            System.out.println("Error updating low stock item resolution status ->" + pe.getMessage());
            Logger.errorLog("Error updating low stock item resolution status (System Error) -> " + pe.getMessage());
        } catch (UserException ue) {
            System.out.println("Error updating low stock item resolution status ->" + ue.getMessage());
            Logger.errorLog("Error updating low stock item resolution status (System Error) -> " + ue.getMessage());
        } catch (Exception e) {
            System.out.println("Error updating low stock item resolution status ->" + e.getMessage());
            Logger.errorLog("Error updating low stock item resolution status (System Error) -> " + e.getMessage());
        }
        return msgMap;
    }
}
