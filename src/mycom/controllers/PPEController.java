package mycom.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import mycom.models.Hospital;

import mycom.models.PPEItem;
import mycom.models.Supplier;
import mycom.models.User;
import mycom.services.HospitalManagement;
import mycom.services.PPEManagement;
import mycom.services.ResetSystem;
import mycom.services.SupplierManagement;
import mycom.utils.DateTime;
import mycom.utils.Logger;

public class PPEController {
    private PPEManagement ppeServices;
    private SupplierManagement supplierServices;
    private User activeUser;

    public PPEController(User user) {
        this.activeUser = user;
        this.ppeServices = new PPEManagement();
        this.supplierServices = new SupplierManagement();
    }

    public List<PPEItem> sortedPPEItemsByCode() {
        return PPEManagement.sortItemsByCode();
    }

    public LinkedHashMap<String, String> addPPEItems(String itemCode, String itemName, String supplierCode, int stockQuantity) {
        if (supplierCode != null && !supplierCode.equals("")) {
            List<Supplier> suppliers = supplierServices.searchSupplier(supplierCode);
            if (suppliers.isEmpty() || suppliers.size() > 1) {
                System.out.println("Error adding new PPE item -> Invalid Supplier Code: " + supplierCode);
                Logger.errorLog("Error adding new PPE item -> Invalid Supplier Code: " + supplierCode);
                LinkedHashMap<String, String> msg = new LinkedHashMap<>();
                msg.put("success", null);
                msg.put("msg", "Error adding new PPE item -> Invalid Supplier Code: " + supplierCode);
                return msg;
            }
        }
        LinkedHashMap<String, String> msg = ppeServices.addPPEItems(itemCode, itemName, supplierCode, stockQuantity);
        if (msg.get("success") != null) {
            PPEItem item = PPEManagement.getPPEItemByCode(msg.get("newItemCode"));
            System.out.println(msg.get("msg"));
            System.out.println(item);
            Logger.log(String.format("User {%s - %s - %s} added new PPE Item {%s - %s}", activeUser.type, activeUser.getId(), activeUser.getName(), item.getCode(), itemName));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to add new PPE Item {%s - %s}\n\tErrorMsg: %s", activeUser.type, activeUser.getId(), activeUser.getName(), itemCode, itemName, msg.get("msg")));
        }
        return msg;
    }

    public LinkedHashMap<String, String> modifyPPEItemName(String itemCode, String newName) {
        LinkedHashMap<String, String> msg = ppeServices.modifyItemName(newName, itemCode);
        if (msg.get("success") != null) {
            PPEItem item = PPEManagement.getPPEItemByCode(itemCode);
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified name for PPE Item {%s - %s}", activeUser.type, activeUser.getId(), activeUser.getName(), item.getCode(), item.getName()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify name for PPE Item {%s - %s}\n\tErrorMsg: %s", activeUser.type, activeUser.getId(), activeUser.getName(), itemCode, "?", msg.get("msg")));
        }
        return msg;
    }

    public LinkedHashMap<String, String> setExactPPEItemQuantity(String itemCode, int quantity) {
        LinkedHashMap<String, String> msg = ppeServices.setExactPPEItemQuantity(itemCode, quantity);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(msg.get("msg"));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(msg.get("msg"));
        }
        return msg;
    }

    public LinkedHashMap<String, String> removePPEItem(String itemCode) {
        LinkedHashMap<String, String> msg = ppeServices.removePPEItems(itemCode);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} removed PPE Item {%s - %s}", activeUser.type, activeUser.getId(), activeUser.getName(), msg.get("deleteItemCode"), msg.get("deleteItemName")));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to remove PPE Item {%s - %s}\n\tErrorMsg: %s", activeUser.type, activeUser.getId(), activeUser.getName(), itemCode, "?", msg.get("msg")));
        }
        return msg;
    }

    public LinkedHashMap<String, String> assignSupplier(String itemCode, String supplierCode) {
        LinkedHashMap<String, String> msg = ppeServices.assignSupplier(itemCode, supplierCode);
        if (msg.get("success") != null) {
            PPEItem item = PPEManagement.getPPEItemByCode(itemCode);
            Supplier supplier = SupplierManagement.getSupplierByCode(supplierCode);
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} assigned Supplier {%s - %s} to PPE Item {%s - %s}", activeUser.type, activeUser.getId(), activeUser.getName(), supplier.getCode(), supplier.getName(), item.getCode(), item.getName()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to assign Supplier {%s - %s} to PPE Item {%s - %s}\n\tErrorMsg: %s", activeUser.type, activeUser.getId(), activeUser.getName(), supplierCode, "?", itemCode, "?", msg.get("msg")));
        }
        return msg;
    }

    // Low Stock Alert
    public ArrayList<LinkedHashMap<String, String>> lowStockAlert() {
        return PPEManagement.getLowStockItems();
    }

    // Search Items
    public List<PPEItem> searchPPEItems(String keywords) {
        return ppeServices.searchItem(keywords);
    }

    // Get All PPE Items
    public List<PPEItem> getAllPPEItems() {
        return PPEManagement.getAllItems();
    }

    // Get Total Inventory Items
    public int getTotalInventoryItems() {
        int total = 0;
        for (PPEItem item: PPEManagement.getAllItems()) {
            total += item.getQuantity();
        }
        return total;
    }

    // Update Low Stock Resolution Status
    public LinkedHashMap<String, String> updateLowStockItemStatus(String itemCode, String status, String staffId) {
        List<PPEItem> allItems = getAllPPEItems();
        PPEItem updateItem = new PPEItem();
        for (PPEItem ppeItem : allItems) {
            if (ppeItem.getCode().equals(itemCode)) {
                updateItem = ppeItem;
                break;
            }
        }
        LinkedHashMap<String, String> msg = ppeServices.updateLowStockItemStatus(updateItem, status, staffId);
        if (msg.get("success") != null) {
            PPEItem item = PPEManagement.getPPEItemByCode(itemCode);
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} updated low stock resolution status for PPE Item {%s - %s}", activeUser.type, activeUser.getId(), activeUser.getName(), item.getCode(), item.getName()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to update low stock resolution status for PPE Item {%s - %s}\n\tErrorMsg: %s", activeUser.type, activeUser.getId(), activeUser.getName(), itemCode, "?", msg.get("msg")));
        }
        return msg;
    }
    
    // Get PPE Received Transactions
    public ArrayList<LinkedHashMap<String, String>> getPPEReceivedTransactions() {
        // transactionType
        ArrayList<LinkedHashMap<String, String>> transactions = PPEManagement.getTransactionRecords();
        ArrayList<LinkedHashMap<String, String>> receivedTransactions = new ArrayList<>();
        if (transactions != null && !transactions.isEmpty()) {
            for (LinkedHashMap<String, String> transaction : transactions) {
                if (transaction.get("transactionType").equalsIgnoreCase("received")) {
                    LinkedHashMap<String, String> newTransactions = new LinkedHashMap<>();
                    newTransactions.put("date", transaction.get("date"));

                    PPEItem item = PPEManagement.getPPEItemByCode(transaction.get("itemCode"));
                    newTransactions.put("itemCode", item.getCode());
                    newTransactions.put("itemName", item.getName());
                    newTransactions.put("receivedQuantity", transaction.get("quantity"));

                    Supplier supplier = SupplierManagement.getSupplierByCode(transaction.get("entityInvolved"));
                    newTransactions.put("supplierCode", supplier.getCode());
                    newTransactions.put("supplierName", supplier.getName());
                    receivedTransactions.add(newTransactions);
                }
            }
        }
        return receivedTransactions;
    }

    // Get PPE Dispatched Transactions
    public ArrayList<LinkedHashMap<String, String>> getPPEDispatchedTransactions() {
        // transactionType
        ArrayList<LinkedHashMap<String, String>> transactions = PPEManagement.getTransactionRecords();
        ArrayList<LinkedHashMap<String, String>> distributedTransactions = new ArrayList<>();
        if (transactions != null && !transactions.isEmpty()) {
            for (LinkedHashMap<String, String> transaction : transactions) {
                if (transaction.get("transactionType").equalsIgnoreCase("distributed")) {
                    LinkedHashMap<String, String> newTransactions = new LinkedHashMap<>();
                    newTransactions.put("date", transaction.get("date"));

                    PPEItem item = PPEManagement.getPPEItemByCode(transaction.get("itemCode"));
                    newTransactions.put("itemCode", item.getCode());
                    newTransactions.put("itemName", item.getName());
                    newTransactions.put("distributedQuantity", transaction.get("quantity"));

                    Hospital hospital = HospitalManagement.getHospitalByCode(transaction.get("entityInvolved"));
                    newTransactions.put("hospitalCode", hospital.getCode());
                    newTransactions.put("hospitalName", hospital.getName());
                    distributedTransactions.add(newTransactions);
                }
            }
        }
        return distributedTransactions;
    }

    // Reset Inventory
    public LinkedHashMap<String, String> resetInventory() {
        LinkedHashMap<String, String> msg = ppeServices.resetInventory(activeUser);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} reset the inventory", activeUser.type, activeUser.getId(), activeUser.getName()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to reset inventory\n\tErrorMsg: %s", activeUser.type, activeUser.getId(), activeUser.getName(), msg.get("msg")));
        }
        return msg;
    }

    // Initialize Inventory
    public LinkedHashMap<String, String> initializeInventory() {
        LinkedHashMap<String, String> msg = ppeServices.initializeInventory();
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} initialized the inventory", activeUser.type, activeUser.getId(), activeUser.getName()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to initialize inventory\n\tErrorMsg: %s", activeUser.type, activeUser.getId(), activeUser.getName(), msg.get("msg")));
        }
        return msg;
    }

    // public boolean deleteInventory() {
    //     LinkedHashMap<String, String> msg = ppeServices.deleteInventory(activeUser);
    //     if (msg.get("success") != null) {
    //         System.out.println(msg.get("msg"));
    //         Logger.log(String.format("User {%s - %s - %s} deleted the PPE inventory", activeUser.type, activeUser.getId(), activeUser.getName()));
    //         return true;
    //     } else {
    //         System.out.println(msg.get("msg"));
    //         Logger.errorLog(String.format("User {%s - %s - %s} failed to delete the PPE inventory\n\tErrorMsg: %s", activeUser.type, activeUser.getId(), activeUser.getName(), msg.get("msg")));
    //         return false;
    //     }
    // }

    // Reset System
    public boolean resetSystem() {
        if (ResetSystem.resetSystem(activeUser).get("success") != null) {
            Logger.log(ResetSystem.resetSystem(activeUser).get("msg"));
            return true;
        } else {
            Logger.errorLog(ResetSystem.resetSystem(activeUser).get("msg") + String.format("\n\tErrorMsg: User {%s - %s -%s} has no permission to reset the system", activeUser.type, activeUser.getId(), activeUser.getName()));
            return false;
        }
    }
}
