package entities;

import dtos.HobbyDTO;
import dtos.PersonDTO;
import dtos.PhoneDTO;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "person")
@NamedQuery(name = "Person.deleteAllRows", query = "DELETE from Person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id", nullable = false)
    private Integer id;

    @Size(max = 45)
    @NotNull
    @Column(name = "first_name", nullable = false, length = 45)
    private String firstName;

    @Size(max = 45)
    @NotNull
    @Column(name = "last_name", nullable = false, length = 45)
    private String lastName;

    @Size(max = 45)
    @NotNull
    @Column(name = "email", nullable = false, length = 45)
    private String email;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(mappedBy = "person")
    private Set<Phone> phones = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "hobby_person",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "hobby_id"))
    private Set<Hobby> hobbies = new LinkedHashSet<>();

    public Person() {
    }

    // For mocking up an entity
    public Person(String firstName, String lastName, String email, Address address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
    }

    // For converting a DTO into an entity
    public Person(PersonDTO pdto) {
        this.id = pdto.getId();
        this.firstName = pdto.getFirstName();
        this.lastName = pdto.getLastName();
        this.email = pdto.getEmail();
        for (PhoneDTO phdto : pdto.getPhones()) {
            this.phones.add(new Phone(phdto));
        }
        if (pdto.getAddress() != null) {
            this.address = new Address(pdto.getAddress());
        }
        for (PersonDTO.InnerHobbyDTO hdto : pdto.getHobbies()) {
            this.hobbies.add(new Hobby(hdto));
        }
    }

    // For converting a DTO into an entity
    public Person(HobbyDTO.InnerPersonDTO pdto) {
        this.id = pdto.getId();
        this.firstName = pdto.getFirstName();
        this.lastName = pdto.getLastName();
        this.email = pdto.getEmail();
        for (PhoneDTO phdto : pdto.getPhones()) {
            this.phones.add(new Phone(phdto));
        }
        if (pdto.getAddress() != null) {
            this.address = new Address(pdto.getAddress());
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setAddressId(int id) {
        address.setId(id);
    }

    public void removeAddress() {
        this.address = null;
    }

    public Set<Phone> getPhones() {
        return phones;
    }

    public void setPhones(Set<Phone> phones) {
        this.phones = phones;
    }

    public void addPhone(Phone phone) {
        this.phones.add(phone);
    }

    public void removePhone(Phone phone) {
        this.phones.remove(phone);
    }

    public Set<Hobby> getHobbies() {
        return hobbies;
    }

    public void setHobbies(Set<Hobby> hobbies) {
        this.hobbies = hobbies;
    }

    public void addHobby(Hobby hobby) {
        this.hobbies.add(hobby);
    }

    public void removeHobby(Hobby hobby) {
        this.hobbies.remove(hobby);
    }

}
