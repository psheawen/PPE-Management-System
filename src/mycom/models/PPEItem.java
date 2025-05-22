package mycom.models;

import java.util.LinkedHashMap;
import java.util.Objects;

import mycom.config.SystemConfig;

public class PPEItem extends Item implements PPETransaction {
    // itemCode, itemName, supplierCode, stockQuantity
    public static final String filePath = SystemConfig.ppeItemFilePath;
    private String supplier, lastRestockDateTime;
    private int quantity;
    private LinkedHashMap<String, String> details = new LinkedHashMap<>();

    public PPEItem() {
        super("", "");
        this.supplier = "";
    }

    public PPEItem(String code, String itemName, String lastRestockDateTime) {
            super(code, itemName);
            this.supplier = "";
            this.quantity = 100;
            this.lastRestockDateTime = lastRestockDateTime;
            details.put("itemCode", getCode());
            details.put("itemName", getName());
            details.put("supplierCode", this.supplier);
            details.put("stockQuantity", String.valueOf(this.quantity));
            details.put("lastRestockDateTime", this.lastRestockDateTime);
    }

    public void invalidatePPEItem() {
        this.code = null;
        this.name = null;
        this.supplier = null;
        this.quantity = 0;
        this.lastRestockDateTime = null;
        if (details != null) {
            details.clear();
        }
    }

    @Override
    public String toString() {
        // return String.format("Item Code: %s%nItem Name: %s%nSupplier Code: %s%nStock Quantity: %d", this.code, this.name, this.supplier, this.quantity);
        return String.format("PPE Item {%s - %s}", this.code, this.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PPEItem)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        
        PPEItem itemObj = (PPEItem) obj;
        return (Objects.equals(this.code, itemObj.code) && Objects.equals(this.name, itemObj.name)
        && Objects.equals(this.supplier, itemObj.supplier) && Objects.equals(this.quantity, itemObj.quantity));
    }

    public String getSupplier() {
        return this.supplier;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public String getLastRestockDateTime() {
        return this.lastRestockDateTime;
    }

    public LinkedHashMap<String, String> getItemMap() {
        return this.details;
    }


    public boolean setStockQuantity(int stockQuantity) {
        if (stockQuantity >= 0) {
            this.quantity = stockQuantity;
            details.put("stockQuantity", String.valueOf(stockQuantity));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addStock(int newStockQuantity) {
        if (newStockQuantity > 0) {
            this.quantity += newStockQuantity;
            details.put("stockQuantity", String.valueOf(this.quantity));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean subtractStock(int newStockQuantity) {
        if (newStockQuantity > 0 && this.quantity >= newStockQuantity) {
            this.quantity -= newStockQuantity;
            details.put("stockQuantity", String.valueOf(this.quantity));
            return true;
        } else {
            return false;
        }
    }

    public boolean modifyItemName(String newName) {
        if (newName != null && !newName.equals("")) { // put the condition to check null first to avoid NullPointerException
            this.name = newName;
            details.put("itemName", newName);
            return true;
        } else {
            return false;
        }
    }

    public boolean setSupplier(String supplierCode) {
        if (supplierCode != null) {
            this.supplier = supplierCode;
            details.put("supplierCode", supplierCode);
            return true;
        } else {
            return false;
        }
    }

    public boolean updateLastRestockDateTime(String lastRestockDateTime) {
        this.lastRestockDateTime = lastRestockDateTime;
        return true;
    }
}
