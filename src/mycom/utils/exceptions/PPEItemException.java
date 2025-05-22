package mycom.utils.exceptions;

public class PPEItemException extends Exception {
    public PPEItemException(String errorMsg) {
        super("PPE Item Handling Error: " + errorMsg);
    }
}
