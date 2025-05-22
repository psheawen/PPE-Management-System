package mycom.controllers;

import java.util.LinkedHashMap;
import java.util.List;

import mycom.models.Supplier;
import mycom.models.User;
import mycom.services.SupplierManagement;
import mycom.utils.Logger;

public class SupplierController {
    // supplierCode, supplierName, supplierContact, supplierAddress
    private SupplierManagement supplierServices;
    private User activeUser;

    public SupplierController(User user) {
        this.activeUser = user;
        this.supplierServices = new SupplierManagement();
    }

    public User getActiveUser() {
        return this.activeUser;
    }

    public LinkedHashMap<String, String> addSupplier(String supplierName, String supplierContact, String supplierAddress) {
        LinkedHashMap<String, String> msg = supplierServices.addSupplier(supplierName, supplierContact, supplierAddress);
        if (msg.get("success") != null) {
            Supplier supplier = SupplierManagement.getSupplierByCode(msg.get("newSupplierCode"));
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} added new Supplier {%s - %s}", activeUser.type, activeUser.getId(), activeUser.getName(), supplier.getCode(), supplierName));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to add new Supplier {%s, %s, %s}\n\tErrorMsg: %s", activeUser.type, activeUser.getId(), activeUser.getName(), supplierName, supplierContact, supplierAddress, msg.get("msg")));
        }
        return msg;
    }

    public LinkedHashMap<String, String> modifySupplierName(String newName, String supplierCode) {
        LinkedHashMap<String, String> msg = supplierServices.modifySupplierName(newName, supplierCode);
        if (msg.get("success") != null) {
            Supplier supplier = SupplierManagement.getSupplierByCode(supplierCode);
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified name for Supplier {%s - %s}", activeUser.type, activeUser.getId(), activeUser.getName(), supplier.getCode(), supplier.getName()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify name for Supplier {%s - %s}\n\tErrorMsg: %s", activeUser.type, activeUser.getId(), activeUser.getName(), supplierCode, "?", msg.get("msg")));
        }
        return msg;
    }

    public LinkedHashMap<String, String> modifySupplierContact(String newContact, String supplierCode) {
        LinkedHashMap<String, String> msg = supplierServices.modifySupplierContact(newContact, supplierCode);
        if (msg.get("success") != null) {
            Supplier supplier = SupplierManagement.getSupplierByCode(supplierCode);
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified contact for Supplier {%s - %s}", activeUser.type, activeUser.getId(), activeUser.getName(), supplier.getCode(), supplier.getName()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify contact for Supplier {%s - %s}\n\tErrorMsg: %s", activeUser.type, activeUser.getId(), activeUser.getName(), supplierCode, "?", msg.get("msg")));
        }
        return msg;
    }

    public LinkedHashMap<String, String> modifySupplierAddress(String newAddress, String supplierCode) {
        LinkedHashMap<String, String> msg = supplierServices.modifySupplierAddress(newAddress, supplierCode);
        if (msg.get("success") != null) {
            Supplier supplier = SupplierManagement.getSupplierByCode(supplierCode);
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified address for Supplier {%s - %s}", activeUser.type, activeUser.getId(), activeUser.getName(), supplier.getCode(), supplier.getName()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify address for Supplier {%s - %s}\n\tErrorMsg: %s", activeUser.type, activeUser.getId(), activeUser.getName(), supplierCode, "?", msg.get("msg")));
        }
        return msg;
    }

    public LinkedHashMap<String, String> deleteSupplier(String supplierCode) {
        LinkedHashMap<String, String> msg = supplierServices.deleteSupplier(supplierCode);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} removed Supplier {%s - %s}", activeUser.type, activeUser.getId(), activeUser.getName(), msg.get("deleteSupplierCode"), msg.get("deleteSupplierName")));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to remove Supplier {%s - %s}\n\tErrorMsg: %s", activeUser.type, activeUser.getId(), activeUser.getName(), supplierCode, "?", msg.get("msg")));
        }
        return msg;
    }

    // Search Supplier
    public List<Supplier> searchSupplier(String keywords) {
        return supplierServices.searchSupplier(keywords);
    }
}
