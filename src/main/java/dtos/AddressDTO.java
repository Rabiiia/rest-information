package dtos;

public class AddressDTO {
    private String street;
    private int zipcode;

    public AddressDTO() {
    }

    public AddressDTO(String street, int zipcode) {
        this.street = street;
        this.zipcode = zipcode;
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
