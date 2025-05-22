package mycom.utils;

import java.util.Arrays;
import java.util.List;

import mycom.config.SystemConfig;
import mycom.models.Hospital;
import mycom.models.PPEItem;
import mycom.models.Supplier;
import mycom.models.User;
import mycom.services.HospitalManagement;
import mycom.services.PPEManagement;
import mycom.services.SupplierManagement;
import mycom.services.UserManagement;
import mycom.utils.exceptions.HospitalException;
import mycom.utils.exceptions.PPEItemException;
import mycom.utils.exceptions.SupplierException;
import mycom.utils.exceptions.UserException;

public class ErrorHandler {

    public static void userErrorHandler(List<String> details, Object... var) throws UserException {
        List<User> allUsers = UserManagement.getAllUsers();
        List<User> allStaff = UserManagement.getStaffs();
        List<User> allManagers = UserManagement.getManagers();
        try {
            if (details.size() != var.length) {
                throw new Exception(
                        "Invalid arguments passed: number of details set to check and number of data passed is conflicted");
            }
        } catch (Exception e) {
            System.out.println("Error Handling error: " + e.getMessage());
            Logger.errorLog("Error Handling error (System Error) -> " + e.getMessage());
        }
        int i = 0;
        for (Object object : var) {
            if (object instanceof String) {
                String data = (String) object;
                if (details.get(i).equalsIgnoreCase("userid")) {
                    if (data == null || data.equals("")) {
                        throw new UserException("User ID cannot be null or empty");
                    }
                } else if (details.get(i).equalsIgnoreCase("name")) {
                    if (data == null || data.equals("")) {
                        throw new UserException("name cannot be null or empty");
                    }
                } else if (details.get(i).equalsIgnoreCase("pwd")) {
                    // a mix of uppercase and lowercase letters, numbers, and symbols.
                    if (data == null || data.equals("")) {
                        throw new UserException("password cannot be null or empty");
                    } else if (data.length() < 8) {
                        throw new UserException("Password must be at least 8 characters long");
                    } else if (!(data.matches(SystemConfig.pwdPattern1) && data.matches(SystemConfig.pwdPattern2)
                            && data.matches(SystemConfig.pwdPattern3) && data.matches(SystemConfig.pwdPattern4))) {
                        throw new UserException(
                                "Password must be a mix of uppercase and lowercase letters, digits, and special characters");
                    }
                } else if (details.get(i).equalsIgnoreCase("type")) {
                    if (!Arrays.asList(SystemConfig.userTypes).contains(data)) {
                        throw new UserException("Invalid user type");
                    }
                }
            } else if (object instanceof User) {
                User data = (User) object;
                if (details.get(i).equalsIgnoreCase("staff")) {
                    if (!allStaff.stream().anyMatch(user -> user.equals(data))) {
                        throw new UserException(String.format("Staff '%s' not found", data.getName()));
                    }
                } else if (details.get(i).equalsIgnoreCase("manager")) {
                    if (!allManagers.stream().anyMatch(user -> user.equals(data))) {
                        throw new UserException(String.format("Manager '%s' not found", data.getName()));
                    }
                } else if (details.get(i).equalsIgnoreCase("user")) {
                    if (!allUsers.stream().anyMatch(user -> user.equals(data))) {
                        throw new UserException(String.format("User '%s' not found", data.getName()));
                    }
                }
            }
            i++;
        }
    }

    public static void ppeErrorHandler(List<String> details, Object... var) throws PPEItemException {
        SupplierManagement supplierServices = new SupplierManagement();
        HospitalManagement hospitalServices = new HospitalManagement();
        List<PPEItem> allItems = PPEManagement.getAllItems();
        List<Supplier> allSuppliers = supplierServices.getAllSuppliers();
        List<Hospital> allHospitals = hospitalServices.getAllHospitals();
        try {
            if (details.size() != var.length) {
                throw new Exception(
                        "Invalid arguments passed: number of details set to check and number of data passed is conflicted");
            }
        } catch (Exception e) {
            System.out.println("Error Handling error -> " + e.getMessage());
            Logger.errorLog("Error Handling error (System Error) -> " + e.getMessage());
        }
        int i = 0;
        for (Object object : var) {
            if (object instanceof String) {
                String data = (String) object;
                if (details.get(i).equalsIgnoreCase("itemCode")) {
                    if (data == null || data.equals("")) {
                        throw new PPEItemException("Item code cannot be null or empty");
                    } else if (allItems.stream().anyMatch(item -> item.getCode().equals(data))) {
                        throw new PPEItemException("Item code must be unique");
                    }
                } else if (details.get(i).equalsIgnoreCase("itemName")) {
                    if (data == null || data.equals("")) {
                        throw new PPEItemException("Name cannot be null or empty");
                    } else if (allItems.stream().anyMatch(item -> item.getName().equals(object))) {
                        throw new PPEItemException(String.format("Item '%s' already exists", data));
                    }
                } else if (details.get(i).equalsIgnoreCase("supplierCode")) {
                    if (!allSuppliers.stream().anyMatch(supplier -> supplier.getCode().equals(data))) {
                        throw new PPEItemException(String.format("Supplier code '%s' not found", data));
                    }
                } else if (details.get(i).equalsIgnoreCase("checkItemCodeExistence")) {
                    if (!allItems.stream().anyMatch(ppeItem -> ppeItem.getCode().equals(data))) {
                        throw new PPEItemException("Invalid PPE item code: PPE item not found");
                    }
                }
            } else if (object instanceof Integer) {
                int data = (int) object;
                if (details.get(i).equalsIgnoreCase("stockQuantityDistribute")) { // the original stock quantity of the item being dispathced
                    if (data < 0) {
                        throw new PPEItemException("Stock quantity cannot be negative");
                    }
                    if (data < SystemConfig.thresholdQuantity) {
                        throw new PPEItemException(String.format(
                                "Stock quantity is below threshold quantity (%d boxes), cannot distribute stocks",
                                SystemConfig.thresholdQuantity));
                    }
                }
                if (details.get(i).equalsIgnoreCase("stockQuantityReceive")) { // the original stock quantity of the item being restocked
                    if (data < 0) {
                        throw new PPEItemException("Stock quantity received cannot be negative");
                    }
                }
                if (details.get(i).equalsIgnoreCase("newQuantity")) { // received or dispatched stock quantity
                    if (data <= 0) {
                        throw new PPEItemException("Stock received or dispatched cannot be negative");
                    }
                }
            } else if (object instanceof PPEItem) {
                PPEItem data = (PPEItem) object;
                if (details.get(i).equalsIgnoreCase("PPEItem")) {
                    if (!allItems.stream().anyMatch(item -> item.equals(data))) {
                        throw new PPEItemException(String.format("PPE item '%s' not found", data.getName()));
                    }
                } else if (details.get(i).equalsIgnoreCase("itemSupplier")) {
                    if (data.getSupplier().equals("") || data.getSupplier() == null) {
                        throw new PPEItemException(
                                String.format("PPE item '%s' has not assigned with a supplier", data.getName()));
                    }
                } else if (details.get(i).equalsIgnoreCase("lowStock")) {
                    if (data.getQuantity() >= SystemConfig.thresholdQuantity) {
                        throw new PPEItemException(
                                String.format("PPE item '%s' stock level is normal", data.getName()));
                    }
                }
            } else if (object instanceof Hospital) {
                Hospital data = (Hospital) object;
                if (details.get(i).equalsIgnoreCase("Hospital")) {
                    if (!allHospitals.stream().anyMatch(item -> item.equals(data))) {
                        throw new PPEItemException(String.format("Hospital '%s' not found", data.getName()));
                    }
                }
            }
            i++;
        }
    }

    public static void supplierErrorHandler(List<String> details, Object... var) throws SupplierException {
        SupplierManagement supplierServices = new SupplierManagement();
        List<Supplier> allSuppliers = supplierServices.getAllSuppliers();
        try {
            if (details.size() != var.length) {
                throw new Exception(
                        "Invalid arguments passed: number of details set to check and number of data passed is conflicted");
            }
        } catch (Exception e) {
            System.out.println("Error Handling error: " + e.getMessage());
            Logger.errorLog("Error Handling error (System Error) -> " + e.getMessage());
        }
        int i = 0;
        for (Object object : var) {
            if (object instanceof String) {
                String data = (String) object;
                if (details.get(i).equalsIgnoreCase("supplierCode")) {
                    if (data == null || data.equals("")) {
                        throw new SupplierException(
                                "Supplier Code cannot be null or empty");
                    }
                } else if (details.get(i).equalsIgnoreCase("supplierName")) {
                    if (data == null || data.equals("")) {
                        throw new SupplierException("Name cannot be null or empty");
                    } else {
                        if (allSuppliers.stream().anyMatch(supplier -> supplier.getName().equalsIgnoreCase(data))) {
                            throw new SupplierException("Supplier name must be unique");
                        }
                    }
                } else if (details.get(i).equalsIgnoreCase("supplierContact")) {
                    if (data == null || data.equals("")) {
                        throw new SupplierException("Contact cannot be null or empty");
                    } // check phone format
                } else if (details.get(i).equalsIgnoreCase("supplierAddress")) {
                    if (data == null || data.equals("")) {
                        throw new SupplierException("Address cannot be null or empty");
                    }
                }
            }
            i++;
        }
    }

    public static void hospitalErrorHandler(List<String> details, Object... var) throws HospitalException {
        HospitalManagement hospitalServices = new HospitalManagement();
        List<Hospital> allHospitals = hospitalServices.getAllHospitals();
        try {
            if (details.size() != var.length) {
                throw new Exception(
                        "Invalid arguments passed: number of details set to check and number of data passed is conflicted");
            }
        } catch (Exception e) {
            System.out.println("Error Handling error: " + e.getMessage());
            Logger.errorLog("Error Handling error (System Error) -> " + e.getMessage());
        }
        int i = 0;
        for (Object object : var) {
            if (object instanceof String) {
                String data = (String) object;
                if (details.get(i).equalsIgnoreCase("hospitalCode")) {
                    if (data == null || data.equals("")) {
                        throw new HospitalException("Hospital Code cannot be null or empty");
                    }
                } else if (details.get(i).equalsIgnoreCase("hospitalName")) {
                    if (data == null || data.equals("")) {
                        throw new HospitalException("Name cannot be null or empty");
                    } else {
                        if (allHospitals.stream().anyMatch(hospital -> hospital.getName().equalsIgnoreCase(data))) {
                            throw new HospitalException("Hospital name must be unique");
                        }
                    }
                } else if (details.get(i).equalsIgnoreCase("hospitalContact")) {
                    if (data == null || data.equals("")) {
                        throw new HospitalException("Contact cannot be null or empty");
                    } // check phone format
                } else if (details.get(i).equalsIgnoreCase("hospitalAddress")) {
                    if (data == null || data.equals("")) {
                        throw new HospitalException("Address cannot be null or empty");
                    }
                }
            }
            i++;
        }
    }
}
