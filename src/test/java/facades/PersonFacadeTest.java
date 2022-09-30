package facades;

import dtos.AddressDTO;
import dtos.PersonDTO;
import dtos.PhoneDTO;
import entities.Address;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonFacadeTest {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactoryForTest();
    private static final PersonFacade FACADE = PersonFacade.getInstance(EMF);

    private Address a1, a2;
    private Person p1, p2, p3, p4;
    private Phone n1, n2, n3, n4;
    private Hobby h1, h2, h3, h4;

    public PersonFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
        // Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    @BeforeEach
    void setUp() {
        EntityManager em = EMF.createEntityManager();
        // Populate the test database
        try {
            em.getTransaction().begin();
            // Delete all if any rows
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            // Create and persist addresses
            a1 = new Address("Falkonner Alle", 2800);
            a2 = new Address("Nørregaard", 3500);
            em.persist(a1);
            em.persist(a2);
            // Create and persist persons (using the above addresses)
            p1 = new Person("Mathias","LastName1", "Email1", a1);
            p2 = new Person("Martin", "LastName2","Email2", a1);
            p3 = new Person("Rabia", "LastName3","Email3", a2);
            p4 = new Person("Zack", "LastName4","Email4", a2);
            em.persist(p1);
            em.persist(p2);
            em.persist(p3);
            em.persist(p4);
            // Create and persist phones (using the above persons)
            n1 = new Phone(12345678, "Mobile", p1);
            n2 = new Phone(23456789, "Mobile", p2);
            n3 = new Phone(34567890, "Mobile", p3);
            n4 = new Phone(45678901, "Mobile", p4);
            em.persist(n1);
            em.persist(n2);
            em.persist(n3);
            em.persist(n4);
            // Create and persist hobbies
            h1 = new Hobby("", "Musik", "", "");
            h2 = new Hobby("", "Fuglekiggeri", "", "");
            h3 = new Hobby("", "Søvn", "", "");
            h4 = new Hobby("", "Squash", "", "");
            em.persist(h1);
            em.persist(h2);
            em.persist(h3);
            em.persist(h4);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void testCreatePerson() throws Exception {
        // Get DTOs from form
        AddressDTO address = new AddressDTO("Nybrovej", 2800);
        Set<PhoneDTO> phones = new LinkedHashSet<>();
        phones.add(new PhoneDTO(87654321, "Mobile"));
        phones.add(new PhoneDTO(76543210, "Home"));
        PersonDTO pdto = new PersonDTO("Bertram", "Jensen", "bertramjensen@hotmail.dk", address, phones);
        // Construct entity from DTOs
        Person person = new Person(pdto);

        //Person person = new Person("Bertram", "Jensen", "bertramjensen@hotmail.dk");
        //Address address = new Address("Nybrovej", 2800);
        //Phone phone1 = new Phone(87654321, "Mobile");
        //Phone phone2 = new Phone(76543210, "Mobile");
        //person.setAddress(address);
        //person.getPhones().add(phone1);
        //person.getPhones().add(phone2);

        FACADE.createPerson(person);
    }

    @Test
    public void testEditPerson() throws Exception {

    }

}