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

class PersonFacadeTest {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactoryForTest();
    private static final PersonFacade FACADE = PersonFacade.getInstance(EMF);

    private static Address a1, a2;
    private static Person p1, p2, p3, p4;
    private static Phone n1, n2, n3, n4;
    private static Hobby h1, h2, h3, h4;

    public PersonFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        EntityManager em = EMF.createEntityManager();
        // Populate the database
        try {
            // Begin new transaction
            em.getTransaction().begin();
            // Delete all rows if any exists
            em.createNamedQuery("Phone.deleteAllRows").executeUpdate();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
            // Create and persist addresses
            a1 = new Address("Falkonner Alle", 2800);
            a2 = new Address("Nørregaard", 3500);
            em.persist(a1);
            em.persist(a2);
            // Create and persist persons (using the address ids generated above)
            p1 = new Person("Mathias","LastName1", "Email1", a1);
            p2 = new Person("Martin", "LastName2","Email2", a1);
            p3 = new Person("Rabia", "LastName3","Email3", a2);
            p4 = new Person("Zack", "LastName4","Email4", a2);
            em.persist(p1);
            em.persist(p2);
            em.persist(p3);
            em.persist(p4);
            // Create and persist phones (using the person ids generated above)
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
            // Commit transaction
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterAll
    public static void tearDownClass() {
        //EntityManager em = EMF.createEntityManager();
        // Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
        /*try {
            // Begin new transaction
            em.getTransaction().begin();
            // Delete all rows if any exists
            em.createNamedQuery("Phone.deleteAllRows").executeUpdate();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
            // Commit transaction
            em.getTransaction().commit();
        } finally {
            em.close();
        }*/
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void testCreatePerson() throws Exception {
        // Mock up DTOs
        AddressDTO address = new AddressDTO("Nørgaardsvej 30", 2800);
        Set<PhoneDTO> phones = new LinkedHashSet<>();
        phones.add(new PhoneDTO(87654321, "Home"));
        phones.add(new PhoneDTO(76543210, "Mobile"));
        PersonDTO pdto = new PersonDTO("Bertram", "Jensen", "bertramjensen@mail.dk", phones, address);
        // Persist person
        FACADE.createPerson(pdto);
    }

    @Test
    public void testGetPersonById() throws Exception {
        assertEquals(1, FACADE.getPersonById(1).getId());
    }

}
