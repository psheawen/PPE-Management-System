package mycom.utils.exceptions;

public class UserException extends Exception {
    public UserException(String errorMsg) {
        super("User Handling Error: " + errorMsg);
    }
}
