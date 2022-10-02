package dtos;

import entities.Phone;

public class PhoneDTO {
    private int number;
    private String description;

    public PhoneDTO() {
    }

    // For mocking up a DTO
    public PhoneDTO(int number, String description) {
        this.number = number;
        this.description = description;
    }

    // For converting an entity into a DTO
    public PhoneDTO(Phone phone) {
        this.number = phone.getNumber();
        this.description = phone.getDescription();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
