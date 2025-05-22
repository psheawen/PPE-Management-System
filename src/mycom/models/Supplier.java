package mycom.models;

import java.util.LinkedHashMap;
import java.util.Objects;

import mycom.config.SystemConfig;


public class Supplier extends Item {
    // supplierCode, supplierName, supplierContact, supplierAddress
    public static final String filePath = SystemConfig.supplierFilePath;
    private String contact, address;
    private LinkedHashMap<String, String> supplierDetails = new LinkedHashMap<>();

    public Supplier() {
        super("", "");
        this.contact = "";
        this.address = "";
    }

    public Supplier(String code, String name, String contact, String address) {
        super(code, name);
        this.contact = contact;
        this.address = address;
        supplierDetails.put("supplierCode", getCode());
        supplierDetails.put("supplierName", getName());
        supplierDetails.put("supplierContact", this.contact);
        supplierDetails.put("supplierAddress", this.address);
    }

    public void invalidateSupplier() {
        this.code = null;
        this.name = null;
        this.contact = null;
        this.address = null;
        if (supplierDetails != null) {
            supplierDetails.clear();
        }
    }

    @Override
    public String toString() {
        // return String.format("Supplier Code: %s%nSupplier Name: %s%nSupplier Contact: %s%nSupplier Address: %s", this.code, this.name, this.contact, this.address);
        return String.format("Supplier {%s - %s}", this.code, this.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Supplier)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        
        Supplier supplierObj = (Supplier) obj;
        return (Objects.equals(this.code, supplierObj.code) && Objects.equals(this.name, supplierObj.name)
        && Objects.equals(this.contact, supplierObj.contact) && Objects.equals(this.address, supplierObj.address));
    }

    public String getContact() {
        return this.contact;
    }

    public String getAddress() {
        return this.address;
    }

    // mutator method for supplier name
    public void modifyName(String newName) {
        this.name = newName;
        supplierDetails.put("supplierName", this.name);
    }

    // mutator method for supplier contact
    public void modifyContact(String newContact) {
        this.contact = newContact;
        supplierDetails.put("supplierContact", this.contact);
    }

    // mutator method for supplier address
    public void modifyAddress(String newAddress) {
        this.address = newAddress;
        supplierDetails.put("supplierAddress", this.address);
    }

    public LinkedHashMap<String, String> getSupplierMap() {
        return this.supplierDetails;
    }
}
