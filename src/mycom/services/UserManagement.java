package mycom.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import mycom.config.SystemConfig;
// Internal Library =D
import mycom.models.User;
import mycom.utils.DateTime;
import mycom.utils.ErrorHandler;
import mycom.utils.FileHandler;
import mycom.utils.Logger;
import mycom.utils.exceptions.UserException;
import java.util.Comparator;

public class UserManagement {
    private static final FileHandler handler = new FileHandler(SystemConfig.userFilePath);
    private static final FileHandler permissionHandler = new FileHandler(SystemConfig.rbcFilePath);
    private static final FileHandler adminHandler = new FileHandler(SystemConfig.superAdminFilePath);
    private static User superAdmin;
    // private static List<User> admins = new ArrayList<>();
    private static List<User> managers = new ArrayList<>();
    private static List<User> staffs = new ArrayList<>();
    private static List<User> allUsers = new ArrayList<>();

    private static ArrayList<LinkedHashMap<String, String>> permissions = new ArrayList<>();
    private User activeUser;
    private boolean CRUDManager = false, CRUDStaff = false, InitializeInventory = false, ResetInventory = false, ResetSystem = false;

    public static void main(String[] args) {
        String pwd = UserManagement.generateTemporaryPassword();
        System.out.println(pwd);
    }

    public UserManagement(User user) {
        this.activeUser = user;
        loadSuperAdmin();
        loadUsers();
        loadUserPermissions();
    }

    public static void loadSuperAdmin() {
        // adminId, adminName, adminPwd, adminType
        FileHandler adminhandler = new FileHandler(SystemConfig.superAdminFilePath);
        LinkedHashMap<String, String> superAdminMap = adminhandler.readFile().get(0);
        superAdmin = new User(superAdminMap.get("userId"), superAdminMap.get("userName"),
                superAdminMap.get("userPwd"), superAdminMap.get("userType"), "");
    }

    public static void loadUsers() {
        // userId, userName, userPwd, userType
        allUsers = new ArrayList<>();
        // admins = new ArrayList<>();
        managers = new ArrayList<>();
        staffs = new ArrayList<>();
        if (handler.checkFileExistence()) {
            ArrayList<LinkedHashMap<String, String>> storedUsers = handler.readFile();
            for (LinkedHashMap<String, String> userMap : storedUsers) {
                User user = new User(userMap.get("userId"), userMap.get("userName"), userMap.get("userPwd"),
                        userMap.get("userType"), userMap.get("joinedDateTime"));
                allUsers.add(user);
                if (userMap.get("userType").equals("staff")) {
                    staffs.add(user);
                } else if (userMap.get("userType").equals("manager")) {
                    managers.add(user);
                }
                // else if (userMap.get("userType").equals("admin")) {
                // admins.add(user);
                // }
            }
        }
    }

    public void loadUserPermissions() {
        permissions = permissionHandler.readFile();
        for (LinkedHashMap<String, String> permission : permissions) {
            if (permission.get("userType").equalsIgnoreCase(this.activeUser.type)) {
                // if (permission.get("CRUDadmin").equalsIgnoreCase("allow")) {
                // CRUDAdmin = true;
                // }
                if (permission.get("CRUDmanager").equalsIgnoreCase("allow")) {
                    CRUDManager = true;
                }
                if (permission.get("CRUDstaff").equalsIgnoreCase("allow")) {
                    CRUDStaff = true;
                }
                if (permission.get("InitializeInventory").equalsIgnoreCase("allow")) {
                    InitializeInventory = true;
                }
                if (permission.get("ResetInventory").equalsIgnoreCase("allow")) {
                    ResetInventory = true;
                }
                if (permission.get("ResetSystem").equalsIgnoreCase("allow")) {
                    ResetSystem = true;
                }
                // if (permission.get("DeleteInventory").equalsIgnoreCase("allow")) {
                //     DeleteInventory = true;
                // }
            }
        }
    }

    public static User getUserById(String userId) {
        loadUsers();
        try {
            if (!allUsers.stream().anyMatch(user -> user.getId().equals(userId))) {
                throw new UserException(String.format("Invalid User ID: User ID '%s' is not found", userId));
            }
            for (User user : allUsers) {
                if (user.getId().equals(userId)) {
                    return user;
                }
            }
            return null;
        } catch (UserException e) {
            System.out.println("Error finding user -> " + e.getMessage());
            Logger.errorLog("Error finding user -> " + e.getMessage());
            return null;
        }
    }

    // accessor methods
    public static User getSuperAdmin() {
        loadSuperAdmin();
        return superAdmin;
    }

    public static List<User> getStaffs() {
        loadUsers();
        return staffs;
    }

    public static List<User> getManagers() {
        loadUsers();
        return managers;
    }

    // public static List<User> getAdmins() {
    // loadUsers();
    // return admins;
    // }

    public static List<User> getAllUsers() {
        loadUsers();
        return allUsers;
    }

    // public boolean getCRUDAdmin() {
    // return this.CRUDAdmin;
    // }

    public boolean getCRUDManager() {
        return this.CRUDManager;
    }

    public boolean getCRUDStaff() {
        return this.CRUDStaff;
    }

    public boolean getInitializeInventory() {
        return this.InitializeInventory;
    }

    public boolean getResetInventory() {
        return this.ResetInventory;
    }

    // public boolean getDeleteInventory() {
    //     return this.DeleteInventory;
    // }

    public boolean getResetSystem() {
        return this.ResetSystem;
    }
    
    public static ArrayList<LinkedHashMap<String, String>> getPermissionMap() {
        return permissions;
    }

    // CRUD - mutator methods
    public LinkedHashMap<String, String> createUser(String name, String pwd, String type) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            ErrorHandler.userErrorHandler(Arrays.asList("name", "pwd", "type"), name, pwd, type);
        } catch (UserException e) {
            System.out.println("Error creating user\n" + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", String.format("User '%s' is not created\n", name) + e.getMessage());
            return msgMap;
        }
        if (checkPermission(type)) {
                String newUserId = generateId(type);
                String currentDateTime = DateTime.formattedLocalDateTime(DateTime.currentDateTime());
                User newUser = new User(newUserId, name, pwd, type, currentDateTime);
                allUsers.add(newUser);
                if (handler.checkFileExistence()) {
                    appendNewUser(newUser);
                } else {
                    writeUsers();
                }
                loadUsers();
                msgMap.put("success", "true");
                msgMap.put("msg", String.format("User '%s' is created and stored!", name));
                msgMap.put("newUserId", newUserId);
                // msgMap.put("newUserName", name);
                return msgMap;
        } else {
            msgMap.put("success", null);
            msgMap.put("msg",
                    String.format("User '%s' is not created: you have no access to create '%s' users", name, type));
            return msgMap;
        }
    }

    public LinkedHashMap<String, String> modifyUserName(String newName, String userId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            User user = getUserById(userId);
            ErrorHandler.userErrorHandler(Arrays.asList("name", "user"), newName, user);
        } catch (UserException e) {
            System.out.println("Error modifying user name\n" + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", String.format("User '%s''s name is not modified\n", userId) + e.getMessage());
            return msgMap;
        }
        User modifyUser = new User();
        for (User user : allUsers) {
            if (user.getId().equals(userId)) {
                modifyUser = user;
                break;
            }
        }
        if (checkPermission(modifyUser.type) || this.activeUser.equals(getUserById(userId))) {
//            modifyUser.modifyName(newName);
            for (User user : allUsers) {
                if (user.getId().equals(modifyUser.getId())) {
                    user.modifyName(newName);
                    break;
                }
            }
            writeUsers();
            loadUsers();
            msgMap.put("success", "true");
            msgMap.put("msg", String.format("User '%s' name is modified!", userId));
        } else {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("User '%s' name is not modified: you have no access to modify '%s' users",
                    userId, modifyUser.type));
        }
        return msgMap;
    }

    public LinkedHashMap<String, String> modifyUserPwd(String newPwd, String userId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            User user = getUserById(userId);
            ErrorHandler.userErrorHandler(Arrays.asList("pwd", "user"), newPwd, user);
        } catch (UserException e) {
            System.out.println("Error modifying user password\n" + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", String.format("User '%s''s password is not modified\n", userId) + e.getMessage());
            return msgMap;
        }
        User modifyUser = new User();
        for (User user : allUsers) {
            if (user.getId().equals(userId)) {
                modifyUser = user;
                break;
            }
        }
        if (checkPermission(modifyUser.type) || this.activeUser.equals(getUserById(userId))) {
//            modifyUser.modifyPwd(newPwd);
            for (User user : allUsers) {
                if (user.getId().equals(modifyUser.getId())) {
                        user.modifyPwd(newPwd);
                    break;
                }
            }
            writeUsers();
            loadUsers();
            msgMap.put("success", "true");
            msgMap.put("msg", String.format("User '%s' password is modified!", userId, modifyUser.type));
        } else {
            msgMap.put("success", null);
            msgMap.put("msg",
                    String.format("User '%s' password is not modified: you have no access to modify '%s' users", userId,
                            modifyUser.type));
        }
        return msgMap;
    }

    public LinkedHashMap<String, String> modifyUserType(String newType, String userId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            User user = getUserById(userId);
            System.out.println(newType);
            ErrorHandler.userErrorHandler(Arrays.asList("type", "user"), newType, user);
        } catch (UserException e) {
            System.out.println("Error modifying user role ->" + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", String.format("User '%s''s role is not modified\n", userId) + e.getMessage());
            return msgMap;
        }
        User modifyUser = new User();
        for (User user : allUsers) {
            if (user.getId().equals(userId)) {
                modifyUser = user;
                break;
            }
        }
        if (checkPermission(modifyUser.type)) {
            modifyUser.modifyType(newType);
            writeUsers();
            loadUsers();
            msgMap.put("success", "true");
            msgMap.put("msg", String.format("User '%s' role is modified!", userId, modifyUser.type));
        } else {
            msgMap.put("success", null);
            msgMap.put("msg",
                    String.format("User '%s' role is not modified: you have no access to modify '%s' users", userId,
                            modifyUser.type));
        }
        return msgMap;
    }

    public LinkedHashMap<String, String> modifyAdminName(String newName) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            ErrorHandler.userErrorHandler(Arrays.asList("name"), newName);
            if (!this.activeUser.type.equalsIgnoreCase("super admin")) {
                throw new UserException("Access denied to modify super admin name");
            }
        } catch (UserException e) {
            System.out.println("Error modifying super admin name\n" + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Super admin '%s''s name is not modified\n", superAdmin.getId()) + e.getMessage());
            return msgMap;
        }
        superAdmin.modifyName(newName);
        ArrayList<LinkedHashMap<String, String>> users = new ArrayList<>();
        users.add(superAdmin.getUserMap());
        adminHandler.writeFile(users);
        loadSuperAdmin();
        msgMap.put("success", "true");
        msgMap.put("msg", String.format("User '%s' name is modified!", superAdmin.getId()));
        return msgMap;
    }

    public LinkedHashMap<String, String> modifyAdminPwd(String newPwd) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            ErrorHandler.userErrorHandler(Arrays.asList("pwd"), newPwd);
            if (!this.activeUser.type.equalsIgnoreCase("super admin")) {
                throw new UserException("Access denied to modify super admin name");
            }
        } catch (UserException e) {
            System.out.println("Error modifying user password\n" + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Super admin '%s' password is not modified\n", superAdmin.getId()) + e.getMessage());
            return msgMap;
        }
        superAdmin.modifyPwd(newPwd);
        ArrayList<LinkedHashMap<String, String>> users = new ArrayList<>();
        users.add(superAdmin.getUserMap());
        adminHandler.writeFile(users);
        loadSuperAdmin();
        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Super admin '%s' password is modified!", superAdmin.getId()));
        return msgMap;
    }

    public LinkedHashMap<String, String> deleteUser(String userId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        User deleteUser = new User();
        for (User user : allUsers) {
            if (user.getId().equals(userId)) {
                deleteUser = user;
                break;
            }
        }
        if (checkPermission(deleteUser.type)) {
            allUsers.removeIf(user -> user.getId().equals(userId));
            writeUsers();
            loadUsers();
            msgMap.put("success", "true");
            msgMap.put("msg", String.format("User '%s' is deleted!", userId));
            msgMap.put("deleteUserType", deleteUser.type);
            msgMap.put("deleteUserId", deleteUser.getId());
            msgMap.put("deleteUserName", deleteUser.getName());
        } else {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("User '%s' is not deleted: you have no access to delete '%s' users", userId,
                    deleteUser.type));
        }
        return msgMap;
    }

    public LinkedHashMap<String, String> modifyUserPermissions(String userType, boolean CRUDManager,
            boolean CRUDStaff, boolean InitializeInventory, boolean ResetInventory, boolean ResetSystem) {
        // userType, CRUDadmin, CRUDmanager, CRUDstaff, ResetInventory
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        if (this.activeUser.equals(superAdmin)) {
            for (LinkedHashMap<String, String> permission : permissions) {
                if (permission.get("userType").equalsIgnoreCase(userType)) {
                    // permission.put("CRUDadmin", CRUDAdmin ? "allow" : "deny");
                    permission.put("CRUDmanager", CRUDManager ? "allow" : "deny");
                    permission.put("CRUDstaff", CRUDStaff ? "allow" : "deny");
                    permission.put("InitializeInventory", InitializeInventory ? "allow" : "deny");
                    permission.put("ResetInventory", ResetInventory ? "allow" : "deny");
                    // permission.put("DeleteInventory", DeleteInventory ? "allow" : "deny");
                    permission.put("ResetSystem", ResetSystem ? "allow" : "deny");
                    break;
                }
            }
            writeUserPermissions();
            loadUserPermissions();
            msgMap.put("success", "true");
            msgMap.put("msg", "User permissions are modified!");
        } else {
            msgMap.put("success", null);
            msgMap.put("msg", "User permissions are not modified: you have no access to modify user permissions");
        }
        return msgMap;
    }

    public List<User> searchUser(String keywords) {
        List<User> matchedUsers = new ArrayList<>();
        // matchedUsers = new ArrayList<>();
        for (User user : allUsers) {
            if (user.getId().toLowerCase().contains(keywords.toLowerCase().trim())
                    || user.getName().toLowerCase().contains(keywords.toLowerCase().trim()) || user.type.contains(keywords.toLowerCase().trim())) {
                matchedUsers.add(user);
            }
        }
        return matchedUsers;
    }
    
    public List<User> searchUserFromGivenList(String keywords, List<User> userList) {
        List<User> matchedUsers = new ArrayList<>();
        for (User user : userList) {
            if (user.getId().toLowerCase().contains(keywords.toLowerCase().trim())
                    || user.getName().toLowerCase().contains(keywords.toLowerCase().trim()) || user.type.contains(keywords.toLowerCase().trim())) {
                matchedUsers.add(user);
            }
        }
        return matchedUsers;
    }
    
        public static List<User> sortUsersById() {
        List<User> sortedUsers = allUsers;
        sortedUsers.sort(Comparator.comparing(User::getId));
        return sortedUsers;
    }
    
    public static List<User> sortGivenUserList(List<User> list) {
        List<User> sortedUsers = list;
        sortedUsers.sort(Comparator.comparing(User::getId));
        return sortedUsers;
    }

    // public String generateId(String type) {
    //     if (!Arrays.asList(userTypes).contains(type)) {
    //         throw new IllegalArgumentException("Invalid user type");
    //     }
    //     String newUserId = "";
    //     boolean loop = false;
    //     do {
    //         newUserId = switch (type.toLowerCase()) {
    //             case "staff" ->
    //                 "#STF" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5);
    //             case "manager" ->
    //                 "#MNG" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5);
    //             // case "admin" ->
    //             // "#ADM" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5);
    //             default -> throw new IllegalStateException("Invalid User ID: User ID cannot be null or empty");
    //         };
    //         String userId = newUserId;
    //         if (newUserId != null && allUsers.stream().anyMatch(user -> Objects.equals(user.getId(), userId))) {
    //             loop = true;
    //         } else {
    //             loop = false;
    //         }
    //     } while (loop);
    //     return newUserId;
    // }

    public static String generateId(String type) {
        if (!Arrays.asList(SystemConfig.userTypes).contains(type)) {
            throw new IllegalArgumentException("Invalid user type");
        }
        String newUserId = "";
        boolean loop;
        do {
            switch (type.toLowerCase()) {
                case "staff":
                    newUserId = "#STF" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5);
                    break;
                case "manager":
                    newUserId = "#MNG" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5);
                    break;
                default:
                    throw new IllegalStateException("Invalid User ID: User ID cannot be null or empty");
            }

            String userId = newUserId;
            loop = newUserId != null && allUsers.stream().anyMatch(user -> Objects.equals(user.getId(), userId));

        } while (loop);
        return newUserId;
    }

    public static String generateTemporaryPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        int passwordLength = 8;
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        String pwd;
        do {
            password = new StringBuilder();
            for (int i = 0; i < passwordLength; i++) {
                password.append(characters.charAt(random.nextInt(characters.length())));
            }
            pwd = password.toString();
        } while (!(pwd.matches(SystemConfig.pwdPattern1) && pwd.matches(SystemConfig.pwdPattern2) && pwd.matches(SystemConfig.pwdPattern3) && pwd.matches(SystemConfig.pwdPattern4)));
        return password.toString();
    }

    public boolean checkPermission(String targetUserType) {
        if (this.activeUser.equals(superAdmin)) {
            return true;
        } else if ((targetUserType.equalsIgnoreCase("staff") && CRUDStaff)
                || (targetUserType.equalsIgnoreCase("manager") && CRUDManager)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean writeUsers() {
        ArrayList<LinkedHashMap<String, String>> users = new ArrayList<>();
        for (User user : allUsers) {
            users.add(user.getUserMap());
        }
        handler.writeFile(users);
        return true;
    }

    public boolean appendNewUser(User user) {
        try {
            ArrayList<LinkedHashMap<String, String>> users = new ArrayList<>();
            users.add(user.getUserMap());
            if (!handler.checkFileExistence() && !handler.createFile()) {
                System.out.printf("File 'users.txt' failed to be created, user '%s' cannot be appended%n",
                        user.getName());
                return false;
            }
            handler.appendFile(users);
            System.out.printf("User '%s' is appended to users.txt!%n", user.getName());
            return true;
        } catch (Exception e) {
            System.out.println("Error appending new user: " + e.getMessage());
            Logger.errorLog("Error appending new user (System Error) -> " + e.getMessage() + "\n");
            return false;
        }
    }

    public boolean writeUserPermissions() {
        try {
            permissionHandler.writeFile(permissions);
            return true;
        } catch (Exception e) {
            System.out.println("Error writing user permissions: " + e.getMessage());
            Logger.errorLog("Error writing user permissions (System Error) -> " + e.getMessage() + "\n");
            return false;
        }
    }
}
