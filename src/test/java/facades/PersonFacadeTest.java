package facades;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.AddressDTO;
import dtos.PersonDTO;
import dtos.PhoneDTO;
import entities.Address;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import errorhandling.EntityFoundException;
import errorhandling.EntityNotFoundException;
import errorhandling.InternalErrorException;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PersonFacadeTest {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactoryForTest();
    private static final PersonFacade FACADE = PersonFacade.getInstance(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Address a1, a2;
    private static Person p1, p2, p3, p4;
    private static Phone n1, n2, n3, n4;
    private static Hobby h1, h2, h3, h4;

    public PersonFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        // Populate the database
        EntityManager em = EMF.createEntityManager();
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
    public void testCreatePerson() {
        // Mock up a PersonDTO
        PersonDTO pdto = new PersonDTO("Bertram", "Jensen", "bertramjensen@mail.dk");
        pdto.addPhone(new PhoneDTO(87654321, "Home"));
        pdto.addPhone(new PhoneDTO(76543210, "Mobile"));
        pdto.setAddress(new AddressDTO("Nørgaardsvej 30", 2800));
        // Try to persist person
        try {
            FACADE.createPerson(pdto);
            System.out.println("Successfully created a new person!");
        }
        catch (InternalErrorException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testGetPersonById() {
        try {
            PersonDTO pdto = FACADE.getPersonById(1);
            System.out.println(GSON.toJson(pdto));
        }
        catch (EntityNotFoundException | InternalErrorException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testGetPersonByNumber() {
        try {
            PersonDTO pdto = FACADE.getPersonByNumber(12345678);
            System.out.println(GSON.toJson(pdto));
        }
        catch (EntityNotFoundException | InternalErrorException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testUpdatePerson() {
        // Mock up updates as a person DTO (one line)
        PersonDTO pdto = new PersonDTO("UpdatedFirstName", "UpdatedLastName", "UpdatedEmail", new LinkedHashSet<>(), new AddressDTO("New Street", 2800));
        // Set the DTOs id to an existing person's id
        pdto.setId(p1.getId());
        // Try to merge the updates into the existing person
        try {
            FACADE.updatePerson(pdto);
            System.out.println("Successfully updated an existing person!");
        }
        catch (EntityNotFoundException | EntityFoundException | InternalErrorException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testRemoveAddressFromPerson() {
        try {
            FACADE.removeAddressFromPerson(7);
            assertNull(FACADE.getPersonById(7).getAddress());
            System.out.println("Successfully removed a person's address!");
        }
        catch (EntityNotFoundException | InternalErrorException e) {
            System.out.println(e.getMessage());
        }
    }

}
