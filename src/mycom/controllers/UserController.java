package mycom.controllers;

import java.util.LinkedHashMap;
import java.util.List;

import mycom.models.User;
import mycom.services.UserManagement;
import mycom.utils.Logger;

// userId, userName, userPwd, userType
public class UserController {
    private UserManagement userServices;
    private TransactionController transactionController;
    private PPEController ppeController;
    private SupplierController supplierController;
    private HospitalController hospitalController;
    private User activeUser;

    public UserController() {
        this.activeUser = null;
        this.userServices = null;
        this.transactionController = null;
        this.ppeController = null;
        this.supplierController = null;
        this.hospitalController = null;
    }

    public User getActiveUser() {
        return this.activeUser;
    }

    public UserManagement getUserServices() {
        return this.userServices;
    }

    public TransactionController getTransactionController() {
        return this.transactionController;
    }

    public PPEController getPPEController() {
        return this.ppeController;
    }

    public SupplierController getSupplierController() {
        return this.supplierController;
    }

    public HospitalController getHospitalController() {
        return this.hospitalController;
    }

    // Search User
    public List<User> searchUser(String keywords) {
        return userServices.searchUser(keywords);
    }

    // Login & Logout
    public boolean userLogin(String userId, String pwd) {
        List<User> allUsers = UserManagement.getAllUsers();
        for (User user : allUsers) {
            if (user.getId().equals(userId) && user.getPwd().equals(pwd)) {
                User loginUser = user;
                this.activeUser = loginUser;
                this.userServices = new UserManagement(this.activeUser);
                this.transactionController = new TransactionController(this.activeUser);
                this.ppeController = new PPEController(this.activeUser);
                this.supplierController = new SupplierController(this.activeUser);
                this.hospitalController = new HospitalController(this.activeUser);
                Logger.log(String.format("User {%s - %s - %s} logged in", loginUser.type, userId, loginUser.getName()));
                return true;
            }
        }
        if (UserManagement.getSuperAdmin().getId().equals(userId) && UserManagement.getSuperAdmin().getPwd().equals(pwd)) {
            this.activeUser = UserManagement.getSuperAdmin();
            this.userServices = new UserManagement(this.activeUser);
            this.transactionController = new TransactionController(this.activeUser);
            this.ppeController = new PPEController(this.activeUser);
            this.supplierController = new SupplierController(this.activeUser);
            this.hospitalController = new HospitalController(this.activeUser);
            Logger.log(String.format("User {%s - %s - %s} logged in", this.activeUser.type, userId, this.activeUser.getName()));
            return true;
        }
        Logger.errorLog(String.format("User with ID '%s' failed to logged in\n\tErrorMsg: %s", userId, "Incorrect Login Credentials"));
        return false;
    }

    // update active user upon modifications
    public boolean updateActiveUser(User updatedActiveUser) {
        this.activeUser = updatedActiveUser;
        this.userServices = new UserManagement(this.activeUser);
        this.transactionController = new TransactionController(this.activeUser);
        this.ppeController = new PPEController(this.activeUser);
        this.supplierController = new SupplierController(this.activeUser);
        this.hospitalController = new HospitalController(this.activeUser);
        return true;
    }

    // used User as argument
    public boolean userLogout(User logoutUser) {
        Logger.log(String.format("User {%s - %s - %s} logged out", logoutUser.type, logoutUser.getId(), logoutUser.getName()));
        this.activeUser = null;
        this.userServices = null;
        this.transactionController = null;
        this.ppeController = null;
        this.supplierController = null;
        this.hospitalController = null;
        logoutUser.invalidateUser();
        return true;
    }

    // CRUD
    public LinkedHashMap<String, String> createUser(String name, String pwd, String type) {
        LinkedHashMap<String, String> msg = userServices.createUser(name, pwd, type);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            User newUser = UserManagement.getUserById(msg.get("newUserId"));
            Logger.log(String.format("User {%s - %s - %s} created a new '%s' account, User {%s - %s}", activeUser.type, activeUser.getId(), activeUser.getName(), newUser.type, newUser.getId(), newUser.getName()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to create a new user account\n\tErrorMsg: %s", activeUser.type, activeUser.getId(), activeUser.getName(), msg.get("msg")));
        }
        return msg;
    }

    public LinkedHashMap<String, String> modifyUserName(String newName, String userId) {
        User user = UserManagement.getUserById(userId);
        LinkedHashMap<String, String> msg = userServices.modifyUserName(newName, userId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified name for User {%s - %s - %s}", activeUser.type, activeUser.getId(), activeUser.getName(), user.type, user.getId(), user.getName()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify name for User {%s - %s - %s}\n\tErrorMsg: %s", activeUser.type, activeUser.getId(), activeUser.getName(), user.type, user.getId(), user.getName(), msg.get("msg")));
        }
        return msg;
    }

    public LinkedHashMap<String, String> modifyPwd(String newPwd, String userId) {
        User user = UserManagement.getUserById(userId);
        LinkedHashMap<String, String> msg = userServices.modifyUserPwd(newPwd, userId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified password for User {%s - %s - %s}", activeUser.type, activeUser.getId(), activeUser.getName(), user.type, user.getId(), user.getName()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify password for User {%s - %s - %s}\n\tErrorMsg: %s", activeUser.getId(), activeUser.getName(), user.type, user.getId(), user.getName(), user.type, msg.get("msg")));
        }
        return msg;
    }

    public LinkedHashMap<String, String> modifyType(String newType, String userId) {
        User user = UserManagement.getUserById(userId);
        LinkedHashMap<String, String> msg = userServices.modifyUserType(newType.toLowerCase(), userId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified role for User {%s - %s - %s}", activeUser.type, activeUser.getId(), activeUser.getName(), user.type, user.getId(), user.getName()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify role for User {%s - %s - %s}\n\tErrorMsg: %s", activeUser.getId(), activeUser.getName(), user.type, user.getId(), user.getName(), user.type, msg.get("msg")));
        }
        return msg;
    }

    public LinkedHashMap<String, String> deleteUser(String userId) {
        LinkedHashMap<String, String> msg = userServices.deleteUser(userId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} removed User {%s - %s - %s}", activeUser.type, activeUser.getId(), activeUser.getName(), msg.get("deleteUserType"), msg.get("deleteUserId"), msg.get("deleteUserName")));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to remove User {%s - %s - %s}\n\tErrorMsg: %s", activeUser.type, activeUser.getId(), activeUser.getName(), "?", userId, "?", msg.get("msg")));
        }
        return msg;
    }

    // Modify Admin Name and Password
    public LinkedHashMap<String, String> modifyAdminName(String newName) {
        LinkedHashMap<String, String> msg = userServices.modifyAdminName(newName);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("Super admin {%s - %s - %s} modified name", activeUser.type, activeUser.getId(), activeUser.getName()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify name\n\tErrorMsg: %s", activeUser.type, activeUser.getId(), activeUser.getName(), msg.get("msg")));
        }
        return msg;
    }

    public LinkedHashMap<String, String> modifyAdminPwd(String newPwd) {
        LinkedHashMap<String, String> msg = userServices.modifyAdminPwd(newPwd);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("Super admin {%s - %s - %s} modified password", activeUser.type, activeUser.getId(), activeUser.getName()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify password\n\tErrorMsg: %s", activeUser.type, activeUser.getId(), activeUser.getName(), msg.get("msg")));
        }
        return msg;
    }
}
