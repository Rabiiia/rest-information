package entities;

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

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Size(max = 45)
    @Column(name = "description", length = 45)
    private String description;


    public Phone() {
    }

    public Phone(int number, Person person) {
        this.number = number;
        this.person = person;
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