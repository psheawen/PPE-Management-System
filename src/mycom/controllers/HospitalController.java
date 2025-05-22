package mycom.controllers;

import java.util.LinkedHashMap;
import java.util.List;

import mycom.models.Hospital;
import mycom.models.User;
import mycom.services.HospitalManagement;
import mycom.utils.Logger;

public class HospitalController {
    // hospitalCode, hospitalName, hospitalContact, hospitalAddress
    private HospitalManagement hospitalServices;
    private User activeUser;

    public HospitalController(User user) {
        this.activeUser = user;
        this.hospitalServices = new HospitalManagement();
    }

    public User getActiveUser() {
        return this.activeUser;
    }

    public LinkedHashMap<String, String> addHospital(String hospitalName, String hospitalContact, String hospitalAddress) {
        LinkedHashMap<String, String> msg = hospitalServices.addHospital(hospitalName, hospitalContact, hospitalAddress);
        if (msg.get("success") != null) {
            Hospital hospital = HospitalManagement.getHospitalByCode(msg.get("newHospitalCode"));
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} added new Hospital {%s - %s}", activeUser.type, activeUser.getId(), activeUser.getName(), hospital.getCode(), hospitalName));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to add new Hospital {%s, %s, %s}\n\tErrorMsg: %s", activeUser.type, activeUser.getId(), activeUser.getName(), hospitalName, hospitalContact, hospitalAddress, msg.get("msg")));
        }
        return msg;
    }

    public LinkedHashMap<String, String> modifyHospitalName(String newName, String hospitalCode) {
        LinkedHashMap<String, String> msg = hospitalServices.modifyHospitalName(newName, hospitalCode);
        if (msg.get("success") != null) {
            Hospital hospital = HospitalManagement.getHospitalByCode(hospitalCode);
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified name for Hospital {%s - %s}", activeUser.type, activeUser.getId(), activeUser.getName(), hospital.getCode(), hospital.getName()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify name for Supplier {%s - %s}\n\tErrorMsg: %s", activeUser.type, activeUser.getId(), activeUser.getName(), hospitalCode, "?", msg.get("msg")));
        }
        return msg;
    }

    public LinkedHashMap<String, String> modifyHospitalContact(String newContact, String hospitalCode) {
        LinkedHashMap<String, String> msg = hospitalServices.modifyHospitalContact(newContact, hospitalCode);
        if (msg.get("success") != null) {
            Hospital hospital = HospitalManagement.getHospitalByCode(hospitalCode);
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified contact for Hospital {%s - %s}", activeUser.type, activeUser.getId(), activeUser.getName(), hospital.getCode(), hospital.getName()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify contact for Hospital {%s - %s}\n\tErrorMsg: %s", activeUser.type, activeUser.getId(), activeUser.getName(), hospitalCode, "?", msg.get("msg")));
        }
        return msg;
    }

    public LinkedHashMap<String, String> modifyHospitalAddress(String newAddress, String hospitalCode) {
        LinkedHashMap<String, String> msg = hospitalServices.modifyHospitalAddress(newAddress, hospitalCode);
        if (msg.get("success") != null) {
            Hospital hospital = HospitalManagement.getHospitalByCode(hospitalCode);
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified address for Hospital {%s - %s}", activeUser.type, activeUser.getId(), activeUser.getName(), hospital.getCode(), hospital.getName()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify address for Hospital {%s - %s}\n\tErrorMsg: %s", activeUser.type, activeUser.getId(), activeUser.getName(), hospitalCode, "?", msg.get("msg")));
        }
        return msg;
    }

    public LinkedHashMap<String, String> deleteHospital(String hospitalCode) {
        LinkedHashMap<String, String> msg = hospitalServices.deleteHospital(hospitalCode);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} removed Hospital {%s - %s}", activeUser.type, activeUser.getId(), activeUser.getName(), msg.get("deleteHospitalCode"), msg.get("deleteHospitalName")));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to remove Hospital {%s - %s}\n\tErrorMsg: %s", activeUser.type, activeUser.getId(), activeUser.getName(), hospitalCode, "?", msg.get("msg")));
        }
        return msg;
    }

    // Search Hospitals
    public List<Hospital> searchHospital(String keywords) {
        return hospitalServices.searchHospital(keywords);
    }
}
