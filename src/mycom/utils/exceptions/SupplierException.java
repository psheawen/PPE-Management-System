package mycom.utils.exceptions;

public class SupplierException extends Exception {
    public SupplierException(String errorMsg) {
        super("Supplier Handling Error: " + errorMsg);
    }
}
