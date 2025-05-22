package mycom.models;

public interface PPETransaction {
    boolean addStock(int newStockQuantity);
    boolean subtractStock(int newStockQuantity);
}
