package mycom.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import mycom.models.Hospital;
import mycom.models.PPEItem;
import mycom.models.User;
// import com.models.Supplier;
import mycom.services.HospitalManagement;
import mycom.services.PPEManagement;
// import com.services.SupplierManagement;
import mycom.utils.Logger;

public class TransactionController {
    private PPEManagement ppeServices;
    private User activeUser;

    public TransactionController(User user) {
        this.activeUser = user;
        this.ppeServices = new PPEManagement();
    }

    public LinkedHashMap<String, String> receiveStock(String itemCode, int quantity) {
        // List<Supplier> suppliers =
        // supplierServices.searchSupplier(item.getSupplier());
        // if (suppliers.isEmpty() || suppliers.size() > 1) {
        // System.out.println("Error receiving stock -> Invalid Supplier Code: " +
        // item.getSupplier());
        // return false;
        // }
        LinkedHashMap<String, String> msg = new LinkedHashMap<>();
        msg.put("success", null);
        msg.put("msg", String.format("User {%s - %s - %s} failed to update inventory\n\tErrorMsg: %s",
                        activeUser.type, activeUser.getId(), activeUser.getName(), String.format("Invalid PPE item code '%s'", itemCode)));
        PPEItem item = PPEManagement.getPPEItemByCode(itemCode);
        if (item != null) {
            msg = ppeServices.receiveStock(item, quantity);
            System.out.println(msg.get("msg"));
            if (msg.get("success") != null) {
                Logger.log(String.format("User {%s - %s - %s} updated inventory: %s", activeUser.type, activeUser.getId(),
                        activeUser.getName(), msg.get("msg")));
            } else {
                Logger.errorLog(String.format("User {%s - %s - %s} failed to update inventory\n\tErrorMsg: %s",
                        activeUser.type, activeUser.getId(), activeUser.getName(), msg.get("msg")));
                msg.put("msg", String.format("User {%s - %s - %s} failed to update inventory\n\tErrorMsg: %s",
                        activeUser.type, activeUser.getId(), activeUser.getName(), msg.get("msg")));
            }
        } else {
            Logger.errorLog(String.format("User {%s - %s - %s} failed to update inventory\n\tErrorMsg: %s",
                        activeUser.type, activeUser.getId(), activeUser.getName(), String.format("Invalid PPE item code '%s'", itemCode)));
        }
        return msg;
    }

    public LinkedHashMap<String, String> distributeStock(String itemCode, int quantity, String hospitalCode) {
        LinkedHashMap<String, String> msg = new LinkedHashMap<>();
        msg.put("success", null);
        PPEItem item = PPEManagement.getPPEItemByCode(itemCode);
        Hospital hospitalDistribute = HospitalManagement.getHospitalByCode(hospitalCode);
        if (item != null && hospitalDistribute != null) {
            msg = ppeServices.distributeStock(item, quantity, hospitalDistribute);
            System.out.println(msg.get("msg"));
            if (msg.get("success") != null) {
                Logger.log(String.format("User {%s - %s - %s} updated inventory: %s", activeUser.type, activeUser.getId(),
                        activeUser.getName(), msg.get("msg")));
            } else {
                Logger.errorLog(String.format("User {%s - %s - %s} failed to update inventory\n\tErrorMsg: %s",
                        activeUser.type, activeUser.getId(), activeUser.getName(), msg.get("msg")));
                msg.put("msg", String.format("User {%s - %s - %s} failed to update inventory\n\tErrorMsg: %s",
                        activeUser.type, activeUser.getId(), activeUser.getName(), msg.get("msg")));
            }
        } else if (item == null) {
            Logger.errorLog(String.format("User {%s - %s - %s} failed to update inventory\n\tErrorMsg: %s",
                        activeUser.type, activeUser.getId(), activeUser.getName(), String.format("Invalid PPE item code '%s'", itemCode)));
            msg.put("msg", String.format("User {%s - %s - %s} failed to update inventory\n\tErrorMsg: %s",
                        activeUser.type, activeUser.getId(), activeUser.getName(), String.format("Invalid PPE item code '%s'", itemCode)));
        } else {
            Logger.errorLog(
                    String.format("User {%s - %s - %s} failed to update inventory\n\tErrorMsg: %s", activeUser.type, activeUser.getId(),
                            activeUser.getName(), String.format("Invalid Hospital code '%s'", hospitalCode)));
            msg.put("msg", String.format("User {%s - %s - %s} failed to update inventory\n\tErrorMsg: %s", activeUser.type, activeUser.getId(),
                            activeUser.getName(), String.format("Invalid Hospital code '%s'", hospitalCode)));
        }
        return msg;
    }

    public List<LinkedHashMap<String, String>> allTotalStockReceived(Date startDate, Date endDate) {
        ArrayList<LinkedHashMap<String, String>> allTotalStockReceived = new ArrayList<>();
        List<PPEItem> allItems = PPEManagement.getAllItems();
        for (PPEItem item: allItems) {
            LinkedHashMap<String, String> stockReceivedMap = new LinkedHashMap<>();
            String allSuppliers = "";
            int totalStockReceived = 0;
            int i = 0;
            for (LinkedHashMap<String, String> stockReceived: ppeServices.totalStockReceived(item.getCode(), startDate, endDate)) {
                if (i == 0) {
                    String supplier = stockReceived.get("supplierCode");
                    allSuppliers = supplier;
                } else {
                    allSuppliers += (", " + stockReceived.get("supplierCode"));
                }
                totalStockReceived += Integer.parseInt(stockReceived.get("totalStockReceived"));
                i++;
            }
            stockReceivedMap.put("itemCode", item.getCode());
            stockReceivedMap.put("itemName", item.getName());
            stockReceivedMap.put("supplierCode", allSuppliers);
            stockReceivedMap.put("totalStockReceived", String.valueOf(totalStockReceived));
            allTotalStockReceived.add(stockReceivedMap);
        }
        return allTotalStockReceived;
    }

    public List<LinkedHashMap<String, String>> allTotalStockDistributed(Date startDate, Date endDate) {
        List<LinkedHashMap<String, String>> allTotalStockDistributed = new ArrayList<>();
        List<PPEItem> allItems = PPEManagement.getAllItems();
        for (PPEItem item: allItems) {
            LinkedHashMap<String, String> stockDistributedMap = new LinkedHashMap<>();
            String allHospitals = "";
            int totalStockReceived = 0;
            int i = 0;
            for (LinkedHashMap<String, String> stockDistributed: ppeServices.totalStockDistributed(item.getCode(), startDate, endDate)) {
                if (i == 0) {
                    String hospital = stockDistributed.get("hospitalCode");
                    allHospitals = hospital;
                } else {
                    allHospitals += (", " + stockDistributed.get("hospitalCode"));
                }
                totalStockReceived += Integer.parseInt(stockDistributed.get("totalStockDistributed"));
                i++;
            }
            stockDistributedMap.put("itemCode", item.getCode());
            stockDistributedMap.put("itemName", item.getName());
            stockDistributedMap.put("hospitalCode", allHospitals);
            stockDistributedMap.put("totalStockDistributed", String.valueOf(totalStockReceived));
            allTotalStockDistributed.add(stockDistributedMap);
        }
        return allTotalStockDistributed;
    }
}
