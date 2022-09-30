package facades;

import entities.Address;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonFacadeTest {
    private static EntityManagerFactory EMF;
    private static PersonFacade FACADE;

    private Address a1, a2;
    private Person p1, p2, p3, p4;
    private Phone n1, n2, n3, n4;
    private Hobby h1, h2, h3, h4;

    public PersonFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        EMF = EMF_Creator.createEntityManagerFactoryForTest();
        FACADE = PersonFacade.getInstance(EMF);
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
            n1 = new Phone(12345678, p1);
            n2 = new Phone(23456789, p2);
            n3 = new Phone(34567890, p3);
            n4 = new Phone(45678901, p4);
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
    }

    @Test
    public void testEditPerson() throws Exception {

    }

}