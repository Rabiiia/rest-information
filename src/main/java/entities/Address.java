package entities;

import dtos.AddressDTO;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "address")
@NamedQuery(name = "Address.deleteAllRows", query = "DELETE from Address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id", nullable = false)
    private Integer id;

    @Size(max = 45)
    @NotNull
    @Column(name = "street", nullable = false, length = 45)
    private String street;

    @NotNull
    @Column(name = "zipcode", nullable = false)
    private Integer zipcode;

    @OneToMany(mappedBy = "address")
    private Set<Person> persons = new LinkedHashSet<>();

    public Address() {
    }

    // For mocking up an entity
    public Address(String street, Integer zipcode) {
        this.street = street;
        this.zipcode = zipcode;
    }

    // For converting a DTO into an entity
    public Address(AddressDTO adto) {
        this.street = adto.getStreet();
        this.zipcode = adto.getZipcode();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getZipcode() {
        return zipcode;
    }

    public void setZipcode(Integer zipcode) {
        this.zipcode = zipcode;
    }

    public Set<Person> getPersons() {
        return persons;
    }

    public void setPersons(Set<Person> persons) {
        this.persons = persons;
    }

}
