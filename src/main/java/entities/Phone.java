package entities;

import dtos.PhoneDTO;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "phone")
@NamedQuery(name = "Phone.deleteAllRows", query = "DELETE from Phone")
public class Phone {
    @Id
    @Column(name = "number", nullable = false)
    private Integer number;

    @Size(max = 45)
    @Column(name = "description", length = 45)
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    public Phone() {
    }

    // For mocking up an entity
    public Phone(int number, String description, Person person) {
        this.number = number;
        this.description = description;
        this.person = person;
    }

    // For converting a DTO into an entity
    public Phone(PhoneDTO phdto) {
        this.number = phdto.getNumber();
        this.description = phdto.getDescription();
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer id) {
        this.number = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
