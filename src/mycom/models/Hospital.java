package mycom.models;

import java.util.LinkedHashMap;
import java.util.Objects;

import mycom.config.SystemConfig;


public class Hospital extends Item {
    // hospitalCode, hospitalName, hospitalContact, hospitalAddress
    public static final String filePath = SystemConfig.hospitalFilePath;
    private String contact, address;
    private LinkedHashMap<String, String> hospitalDetails = new LinkedHashMap<>();

    public Hospital() {
        super("", "");
        this.contact = "";
        this.address = "";
    }

    public Hospital(String code, String name, String contact, String address) {
        super(code, name);
        this.contact = contact;
        this.address = address;
        hospitalDetails.put("hospitalCode", getCode());
        hospitalDetails.put("hospitalName", getName());
        hospitalDetails.put("hospitalContact", this.contact);
        hospitalDetails.put("hospitalAddress", this.address);
    }

    public void invalidateHospital() {
        this.code = null;
        this.name = null;
        this.contact = null;
        this.address = null;
        if (hospitalDetails != null) {
            hospitalDetails.clear();
        }
    }

    @Override
    public String toString() {
        // return String.format("Hospital Code: %s%nHospital Name: %s%nHospital Contact: %s%nHospital Address: %s", this.code, this.name, this.contact, this.address);
        return String.format("Hospital {%s - %s}", this.code, this.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Hospital)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        
        Hospital hospitalObj = (Hospital) obj;
        return (Objects.equals(this.code, hospitalObj.code) && Objects.equals(this.name, hospitalObj.name)
        && Objects.equals(this.contact, hospitalObj.contact) && Objects.equals(this.address, hospitalObj.address));
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public String getContact() {
        return this.contact;
    }

    public String getAddress() {
        return this.address;
    }

    public LinkedHashMap<String, String> getHospitalMap() {
        return this.hospitalDetails;
    }

    // mutator method for hospital name
    public void modifyName(String newName) {
        this.name = newName;
        hospitalDetails.put("hospitalName", this.name);
    }

    // mutator method for hospital contact
    public void modifyContact(String newContact) {
        this.contact = newContact;
        hospitalDetails.put("hospitalContact", this.contact);
    }

    // mutator method for hospital address
    public void modifyAddress(String newAddress) {
        this.address = newAddress;
        hospitalDetails.put("hospitalAddress", this.address);
    }
}
