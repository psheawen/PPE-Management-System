package mycom.utils.exceptions;

public class HospitalException extends Exception {
    public HospitalException(String errorMsg) {
        super("Hospital Handling Error: " + errorMsg);
    }
}
