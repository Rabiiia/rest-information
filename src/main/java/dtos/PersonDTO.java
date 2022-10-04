package dtos;

import entities.Hobby;
import entities.Person;
import entities.Phone;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class PersonDTO {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private AddressDTO address;
    private Set<PhoneDTO> phones = new LinkedHashSet<>();
    private Set<InnerHobbyDTO> hobbies = new LinkedHashSet<>();

    public PersonDTO() {
    }

    // For mocking up a DTO
    public PersonDTO(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // For mocking up a DTO
    public PersonDTO(String firstName, String lastName, String email, Set<PhoneDTO> phones, AddressDTO address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phones = phones;
        if (address != null)
            this.address = address;
    }

    // For converting an entity into a DTO
    public PersonDTO(Person person) {
        if (person.getId() != null) {
            this.id = person.getId();
        }
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.email = person.getEmail();
        if (person.getAddress() != null) {
            this.address = new AddressDTO(person.getAddress());
        }
        for (Phone phone : person.getPhones()) {
            this.phones.add(new PhoneDTO(phone));
        }
        for (Hobby hobby : person.getHobbies()) {
            this.hobbies.add(new InnerHobbyDTO(hobby));
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

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    public Set<PhoneDTO> getPhones() {
        return phones;
    }

    public void setPhones(Set<PhoneDTO> phones) {
        this.phones = phones;
    }

    public void addPhone(PhoneDTO phone) {
        this.phones.add(phone);
    }

    public void removePhone(PhoneDTO phone) {
        this.phones.remove(phone);
    }

    /**
     * A DTO for the {@link entities.Person} entity
     */
    public static class InnerHobbyDTO implements Serializable {
        private final Integer id;
        private final String category;
        private final String name;
        private final String type;
        private final String wikiLink;

        // For moocking up a DTO
        public InnerHobbyDTO(Integer id, String category, String name, String type, String wikiLink) {
            this.id = id;
            this.category = category;
            this.name = name;
            this.type = type;
            this.wikiLink = wikiLink;
        }

        // For converting an entity to a DTO
        public InnerHobbyDTO(Hobby hobby) {
            this.id = hobby.getId();
            this.category = hobby.getCategory();
            this.name = hobby.getName();
            this.type = hobby.getType();
            this.wikiLink = hobby.getWikiLink();
        }

        public Integer getId() {
            return id;
        }

        public String getCategory() {
            return category;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getWikiLink() {
            return wikiLink;
        }

    }

}
