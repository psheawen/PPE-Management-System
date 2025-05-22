package mycom.services;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import mycom.config.SystemConfig;
import mycom.models.Hospital;
import mycom.utils.FileHandler;
import mycom.utils.Logger;
import mycom.utils.ErrorHandler;
import mycom.utils.exceptions.HospitalException;
import java.util.Comparator;


// IMPORTANT !!! -> should hospital.txt be reset if inventory is reset
// exception handling for contact format rmb to do (no need lar cough)

public class HospitalManagement {
    private FileHandler hospitalHandler = new FileHandler(SystemConfig.hospitalFilePath);
    private static List<Hospital> allHospitals = new ArrayList<>();
    // private User activeUser;

    // public static void main(String[] args) {
    //     User user = new User();
    //     HospitalManagement n = new HospitalManagement(user);
    //     n.addHospital("Hospital Wii", "012-3456789", "Jalan Wii, 123");
        // n.deleteSupplier("#SUP00001");
        // List<Hospital> matchedResults = n.getAllHospitals();
        // for (Hospital user2 : matchedResults) {
        //     System.out.println(user2);
        //     System.out.println();
        // }
    // }
    
    public HospitalManagement() {
        // this.activeUser = user;
        loadHospitals();
    }

    public void loadHospitals() {
        allHospitals = new ArrayList<>();
        if (hospitalHandler.checkFileExistence()) {
            ArrayList<LinkedHashMap<String, String>> hospitalsMap = hospitalHandler.readFile();
            for (LinkedHashMap<String,String> hospitalMap : hospitalsMap) {
                Hospital hospital = new Hospital(hospitalMap.get("hospitalCode"), hospitalMap.get("hospitalName"), hospitalMap.get("hospitalContact"), hospitalMap.get("hospitalAddress"));
                allHospitals.add(hospital);
            }
        }
    }

    public static List<Hospital> getAllHospitals() {
        return allHospitals;
    }

    public static Hospital getHospitalByCode(String hospitalCode) {
        try {
            if (!allHospitals.stream().anyMatch(hospital -> hospital.getCode().equals(hospitalCode))) {
                throw new HospitalException(String.format("Invalid Hospital Code: Hospital code '%s' is not found", hospitalCode));
            }
            for (Hospital hospital : allHospitals) {
                if (hospital.getCode().equals(hospitalCode)) {
                    return hospital;
                }
            }
            return null;
        } catch (HospitalException e) {
            System.out.println("Error finding hospital -> " + e.getMessage());
            Logger.errorLog("Error finding hospital -> " + e.getMessage());
            return null;
        }
    }

    public LinkedHashMap<String, String> addHospital(String hospitalName, String hospitalContact, String hospitalAddress) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            String hospitalCode = generateHospitalCode();
            ErrorHandler.hospitalErrorHandler(Arrays.asList("hospitalCode", "hospitalName", "hospitalContact", "hospitalAdress"), hospitalCode, hospitalName, hospitalContact, hospitalAddress);
            Hospital newHospital = new Hospital(hospitalCode, hospitalName, hospitalContact, hospitalAddress);
            allHospitals.add(newHospital);
            if (hospitalHandler.checkFileExistence()) {
                appendNewHospital(newHospital);
            } else {
                writeHospitals();
            }
            loadHospitals();
            // return String.format("User '%s' is created and stored!", name);
            msgMap.put("success", "true");
            msgMap.put("msg", String.format("Hospital '%s' is created and stored!", hospitalName));
            msgMap.put("newHospitalCode", hospitalCode);
            // msgMap.put("newHospitalName", hospitalName);
            return msgMap;
        } catch (HospitalException e) {
            System.out.println("Error creating user -> " + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Hospital '%s' is not created -> ", hospitalName) + e.getMessage());
            return msgMap;
        }
    }

    public LinkedHashMap<String, String> modifyHospitalName(String newName, String hospitalCode) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            ErrorHandler.hospitalErrorHandler(Arrays.asList("hospitalName"), newName);
        } catch (HospitalException e) {
            System.out.println("Error modifying hospital name -> " + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Hospital '%s''s name is not modified -> ", hospitalCode) + e.getMessage());
            return msgMap;
        }
        Hospital modifyHospital = new Hospital();
        for (Hospital hospital : allHospitals) {
            if (hospital.getCode().equals(hospitalCode)) {
                modifyHospital = hospital;
                break;
            }
        }
        modifyHospital.modifyName(newName);
        writeHospitals();
        loadHospitals();
        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Hospital '%s' name is modified!", hospitalCode));
        return msgMap;
    }

    public LinkedHashMap<String, String> modifyHospitalContact(String newContact, String hospitalCode) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            ErrorHandler.hospitalErrorHandler(Arrays.asList("hospitalContact"), newContact);
        } catch (HospitalException e) {
            System.out.println("Error modifying hospital contact -> " + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Hospital '%s''s contact is not modified -> ", hospitalCode) + e.getMessage());
            return msgMap;
        }
        Hospital modifyHospital = new Hospital();
        for (Hospital hospital : allHospitals) {
            if (hospital.getCode().equals(hospitalCode)) {
                modifyHospital = hospital;
                break;
            }
        }
        modifyHospital.modifyContact(newContact);
        writeHospitals();
        loadHospitals();
        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Hospital '%s' contact is modified!", hospitalCode));
        return msgMap;
    }

    public LinkedHashMap<String, String> modifyHospitalAddress(String newAddress, String hospitalCode) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            ErrorHandler.hospitalErrorHandler(Arrays.asList("hospitalAddress"), newAddress);
        } catch (HospitalException e) {
            System.out.println("Error modifying hospital address -> " + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Hospital '%s''s address is not modified -> ", hospitalCode) + e.getMessage());
            return msgMap;
        }
        Hospital modifyHospital = new Hospital();
        for (Hospital hospital : allHospitals) {
            if (hospital.getCode().equals(hospitalCode)) {
                modifyHospital = hospital;
                break;
            }
        }
        modifyHospital.modifyAddress(newAddress);
        writeHospitals();
        loadHospitals();
        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Hospital '%s' contact is modified!", hospitalCode));
        return msgMap;
    }

    public LinkedHashMap<String, String> deleteHospital(String hospitalCode) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        if (allHospitals.stream().anyMatch(hospital -> hospital.getCode().equals(hospitalCode))) {
            String deleteHospitalName = getHospitalByCode(hospitalCode).getName();
            allHospitals.removeIf(hospital -> hospital.getCode().equals(hospitalCode));
            writeHospitals();
            loadHospitals();
            msgMap.put("success", "true");
            msgMap.put("msg", String.format("Hospital '%s' is deleted!", hospitalCode));
            msgMap.put("deleteHospitalCode", hospitalCode);
            msgMap.put("deleteHospitalName", deleteHospitalName);
        } else {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Hospital '%s' is not found, cannot be deleted", hospitalCode));
        }
        return msgMap;
    }

    public List<Hospital> searchHospital(String keywords) {
        List<Hospital> matchedHospitals = new ArrayList<>();
        matchedHospitals = new ArrayList<>();
        for (Hospital hospital : allHospitals) {
            if (hospital.getCode().toLowerCase().contains(keywords.toLowerCase().trim()) || hospital.getName().toLowerCase().contains(keywords.toLowerCase().trim())) {
                matchedHospitals.add(hospital);
            }
        }
        return matchedHospitals;
    }
    
    public static List<Hospital> sortHospitalsByCode() {
        List<Hospital> sortedHospitals = allHospitals;
        sortedHospitals.sort(Comparator.comparing(Hospital::getCode));
        return sortedHospitals;
    }
    
    public static List<Hospital> sortGivenSupplierList(List<Hospital> list) {
        List<Hospital> sortedHospitals = list;
        sortedHospitals.sort(Comparator.comparing(Hospital::getCode));
        return sortedHospitals;
    }

    public boolean writeHospitals() {
        ArrayList<LinkedHashMap<String, String>> hospitals = new ArrayList<>();
        for (Hospital hospital : allHospitals) {
            hospitals.add(hospital.getHospitalMap());
        }
        hospitalHandler.writeFile(hospitals);
        return true;
    }

    public boolean appendNewHospital(Hospital hospital) {
        try {
            ArrayList<LinkedHashMap<String, String>> hospitals = new ArrayList<>();
            hospitals.add(hospital.getHospitalMap());
            if (!hospitalHandler.checkFileExistence() && !hospitalHandler.createFile()) {
                System.out.printf("File 'hospital.txt' failed to be created, hospital '%s' cannot be appended%n",
                        hospital.getName());
                return false;
            }
            hospitalHandler.appendFile(hospitals);
            System.out.printf("Hospital '%s' is appended to hospital.txt!%n", hospital.getName());
            return true;
        } catch (Exception e) {
            System.out.println("Error appending new user (System Error) -> " + e.getMessage());
            Logger.errorLog("Error appending new user (System Error) -> " + e.getMessage());
            return false;
        }
    }

    public String generateHospitalCode() {
        if (allHospitals.isEmpty()) {
            return "#HOSP" + String.format("%05d", (allHospitals.size() + 1));
        } else {
            String lastHospitalCode= "";
            for (Hospital hospital : allHospitals) {
                lastHospitalCode = hospital.getCode();
            }
            String lastNumberString = lastHospitalCode.replace("#HOSP", "");
            return "#HOSP" + String.format("%05d", Integer.parseInt(lastNumberString) + 1);
        }
    }
}