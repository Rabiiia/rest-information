package dtos;

import entities.Address;

public class AddressDTO {
    private String street;
    private int zipcode;

    public AddressDTO() {
    }

    // For mocking up a DTO
    public AddressDTO(String street, int zipcode) {
        this.street = street;
        this.zipcode = zipcode;
    }

    // For converting an entity into a DTO
    public AddressDTO(Address address) {
        this.street = address.getStreet();
        this.zipcode = address.getZipcode();
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getZipcode() {
        return zipcode;
    }

    public void setZipcode(int zipcode) {
        this.zipcode = zipcode;
    }

}
