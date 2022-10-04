package dtos;

import entities.Hobby;
import entities.Person;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A DTO for the {@link entities.Hobby} entity
 */
public class HobbyDTO implements Serializable {
    private final Integer id;
    private final String category;
    private final String name;
    private final String type;
    private final String wikiLink;
    private final Set<InnerPersonDTO> persons = new LinkedHashSet<>();

    // For moocking up a DTO
    public HobbyDTO(Integer id, String category, String name, String type, String wikiLink) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.type = type;
        this.wikiLink = wikiLink;
    }

    // For converting an entity to a DTO
    public HobbyDTO(Hobby hobby) {
        this.id = hobby.getId();
        this.category = hobby.getCategory();
        this.name = hobby.getName();
        this.type = hobby.getType();
        this.wikiLink = hobby.getWikiLink();
        for (Person person : hobby.getPersons()) {
            this.persons.add(new InnerPersonDTO(person));
        }
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

    public Set<InnerPersonDTO> getPersons() {
        return persons;
    }

    /**
     * A DTO for the {@link entities.Person} entity
     */
    public static class InnerPersonDTO implements Serializable {
        private final Integer id;
        private final String firstName;
        private final String lastName;
        private final String email;
        private final Set<PhoneDTO> phones = new LinkedHashSet<>();
        private final AddressDTO address;

        // For mocking up an inner DTO
        public InnerPersonDTO(Integer id, String firstName, String lastName, String email, AddressDTO address) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.address = address;
        }

        // For converting an entity into an inner DTO
        public InnerPersonDTO(Person person) {
            this.id = person.getId();
            this.firstName = person.getFirstName();
            this.lastName = person.getLastName();
            this.email = person.getEmail();
            this.address = new AddressDTO(person.getAddress());
        }

        public Integer getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public AddressDTO getAddress() {
            return address;
        }

        public Set<PhoneDTO> getPhones() {
            return phones;
        }
    }
}