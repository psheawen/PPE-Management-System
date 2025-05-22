package mycom.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import mycom.config.SystemConfig;
import mycom.models.PPEItem;
import mycom.models.Supplier;
import mycom.utils.FileHandler;
import mycom.utils.Logger;
import mycom.utils.ErrorHandler;
import mycom.utils.exceptions.SupplierException;
import java.util.Comparator;


// IMPORTANT !!! -> should supplier.txt be reset if inventory is reset
// exception handling for contact format rmb to do (no need lar cough)

public class SupplierManagement {
    private FileHandler supplierHandler = new FileHandler(Supplier.filePath);
    private static List<Supplier> allSuppliers = new ArrayList<>();

    public SupplierManagement() {
        loadSuppliers();
    }

    public void loadSuppliers() {
        allSuppliers = new ArrayList<>();
        if (supplierHandler.checkFileExistence()) {
            ArrayList<LinkedHashMap<String, String>> suppliersMap = supplierHandler.readFile();
            for (LinkedHashMap<String, String> supplierMap : suppliersMap) {
                Supplier supplier = new Supplier(supplierMap.get("supplierCode"), supplierMap.get("supplierName"),
                        supplierMap.get("supplierContact"), supplierMap.get("supplierAddress"));
                allSuppliers.add(supplier);
            }
        }
    }

    public static List<Supplier> getAllSuppliers() {
        return allSuppliers;
    }

    public static Supplier getSupplierByCode(String supplierCode) {
        try {
            if (!allSuppliers.stream().anyMatch(supplier -> supplier.getCode().equals(supplierCode))) {
                throw new SupplierException(String.format("Invalid Supplier Code: Supplier code '%s' is not found", supplierCode));
            }
            for (Supplier supplier : allSuppliers) {
                if (supplier.getCode().equals(supplierCode)) {
                    return supplier;
                }
            }
            return null;
        } catch (SupplierException e) {
            System.out.println("Error finding supplier -> " + e.getMessage());
            Logger.errorLog("Error finding supplier -> " + e.getMessage());
            return null;
        }
    }

    public LinkedHashMap<String, String> addSupplier(String supplierName, String supplierContact,
            String supplierAddress) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            String supplierCode = generateSupplierCode();
            ErrorHandler.supplierErrorHandler(Arrays.asList("supplierCode", "supplierName", "supplierContact", "supplierAdress"),
                    supplierCode, supplierName, supplierContact, supplierAddress);
            Supplier newSupplier = new Supplier(supplierCode, supplierName, supplierContact, supplierAddress);
            allSuppliers.add(newSupplier);
            if (supplierHandler.checkFileExistence()) {
                appendNewSupplier(newSupplier);
            } else {
                writeSuppliers();
            }
            loadSuppliers();
            msgMap.put("success", "true");
            msgMap.put("msg", String.format("Supplier '%s' is created and stored!", supplierName));
            msgMap.put("newSupplierCode", supplierCode);
            // msgMap.put("newSupplierName", supplierName);
            return msgMap;
        } catch (SupplierException e) {
            System.out.println("Error creating user -> " + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Supplier '%s' is not created -> ", supplierName) + e.getMessage());
            return msgMap;
        }
    }

    public LinkedHashMap<String, String> modifySupplierName(String newName, String supplierCode) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            ErrorHandler.supplierErrorHandler(Arrays.asList("supplierName"), newName);
        } catch (SupplierException e) {
            System.out.println("Error modifying supplier name -> " + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Supplier '%s''s name is not modified -> ", supplierCode) + e.getMessage());
            return msgMap;
        }
        Supplier modifySupplier = new Supplier();
        for (Supplier supplier : allSuppliers) {
            if (supplier.getCode().equals(supplierCode)) {
                modifySupplier = supplier;
                break;
            }
        }
        modifySupplier.modifyName(newName);
        writeSuppliers();
        loadSuppliers();
        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Supplier '%s' name is modified!", supplierCode));
        return msgMap;
    }

    public LinkedHashMap<String, String> modifySupplierContact(String newContact, String supplierCode) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            ErrorHandler.supplierErrorHandler(Arrays.asList("supplierContact"), newContact);
        } catch (SupplierException e) {
            System.out.println("Error modifying supplier contact -> " + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Supplier '%s''s contact is not modified -> ", supplierCode) + e.getMessage());
            return msgMap;
        }
        Supplier modifySupplier = new Supplier();
        for (Supplier supplier : allSuppliers) {
            if (supplier.getCode().equals(supplierCode)) {
                modifySupplier = supplier;
                break;
            }
        }
        modifySupplier.modifyContact(newContact);
        writeSuppliers();
        loadSuppliers();
        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Supplier '%s' contact is modified!", supplierCode));
        return msgMap;
    }

    public LinkedHashMap<String, String> modifySupplierAddress(String newAddress, String supplierCode) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            ErrorHandler.supplierErrorHandler(Arrays.asList("supplierAddress"), newAddress);
        } catch (SupplierException e) {
            System.out.println("Error modifying supplier address -> " + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Supplier '%s''s address is not modified -> ", supplierCode) + e.getMessage());
            return msgMap;
        }
        Supplier modifySupplier = new Supplier();
        for (Supplier supplier : allSuppliers) {
            if (supplier.getCode().equals(supplierCode)) {
                modifySupplier = supplier;
                break;
            }
        }
        modifySupplier.modifyAddress(newAddress);
        writeSuppliers();
        loadSuppliers();
        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Supplier '%s' contact is modified!", supplierCode));
        return msgMap;
    }

    public LinkedHashMap<String, String> deleteSupplier(String supplierCode) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        if (allSuppliers.stream().anyMatch(supplier -> supplier.getCode().equals(supplierCode))) {
            String deleteSupplierName = getSupplierByCode(supplierCode).getName();
            allSuppliers.removeIf(supplier -> supplier.getCode().equals(supplierCode));
            writeSuppliers();
            loadSuppliers();
            // Update ppe.txt (remove supplier)
            ArrayList<LinkedHashMap<String, String>> updatedItems = new ArrayList<>();
            List<PPEItem> allItems = PPEManagement.getAllItems();
            for (PPEItem item: allItems) {
                if (item.getSupplier().equals(supplierCode)) {
                    item.setSupplier("");
                    System.out.println(item.getSupplier());
                }
                updatedItems.add(item.getItemMap());
            }
            FileHandler ppeHandler = new FileHandler(SystemConfig.ppeItemFilePath);
            ppeHandler.writeFile(updatedItems);
            msgMap.put("success", "true");
            msgMap.put("msg", String.format("Supplier '%s' is deleted!", supplierCode));
            msgMap.put("deleteSupplierCode", supplierCode);
            msgMap.put("deleteSupplierName", deleteSupplierName);
        } else {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Supplier '%s' is not found, cannot be deleted", supplierCode));
        }
        
        return msgMap;
    }

    public List<Supplier> searchSupplier(String keywords) {
        List<Supplier> matchedSuppliers = new ArrayList<>();
        matchedSuppliers = new ArrayList<>();
        for (Supplier supplier : allSuppliers) {
            if (supplier.getCode().toLowerCase().contains(keywords.toLowerCase().trim())
                    || supplier.getName().toLowerCase().contains(keywords.toLowerCase().trim())) {
                matchedSuppliers.add(supplier);
            }
        }
        return matchedSuppliers;
    }
    
        public static List<Supplier> sortSuppliersByCode() {
        List<Supplier> sortedSuppliers = allSuppliers;
        sortedSuppliers.sort(Comparator.comparing(Supplier::getCode));
        return sortedSuppliers;
    }
    
    public static List<Supplier> sortGivenSupplierList(List<Supplier> list) {
        List<Supplier> sortedSuppliers = list;
        sortedSuppliers.sort(Comparator.comparing(Supplier::getCode));
        return sortedSuppliers;
    }

    public boolean writeSuppliers() {
        ArrayList<LinkedHashMap<String, String>> suppliers = new ArrayList<>();
        for (Supplier supplier : allSuppliers) {
            suppliers.add(supplier.getSupplierMap());
        }
        supplierHandler.writeFile(suppliers);
        return true;
    }

    public boolean appendNewSupplier(Supplier supplier) {
        try {
            ArrayList<LinkedHashMap<String, String>> suppliers = new ArrayList<>();
            suppliers.add(supplier.getSupplierMap());
            if (!supplierHandler.checkFileExistence() && !supplierHandler.createFile()) {
                System.out.printf("File 'supplier.txt' failed to be created, supplier '%s' cannot be appended%n",
                        supplier.getName());
                return false;
            }
            supplierHandler.appendFile(suppliers);
            System.out.printf("Supplier '%s' is appended to supplier.txt!%n", supplier.getName());
            return true;
        } catch (Exception e) {
            System.out.println("Error appending new user: " + e.getMessage());
            Logger.errorLog("Error appending new user (System Error) -> " + e.getMessage());
            return false;
        }
    }

    public String generateSupplierCode() {
        loadSuppliers();
        if (allSuppliers.isEmpty()) {
            return "#SUP" + String.format("%05d", (allSuppliers.size() + 1));
        } else {
            String lastSupplierCode = allSuppliers.get(allSuppliers.size() - 1).getCode();
            String lastNumberString = lastSupplierCode.replace("#SUP", "");
            return "#SUP" + String.format("%05d", (Integer.parseInt(lastNumberString) + 1));
        }
    }
}
