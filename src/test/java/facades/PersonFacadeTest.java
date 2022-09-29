package facades;

import entities.Address;
import entities.Person;
import entities.Phone;
import entities.RenameMe;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class PersonFacadeTest {


    private static EntityManagerFactory emf;
    private static PersonFacade facade;

    Person p1,p2;
    Address a1,a2;

    public PersonFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = PersonFacade.getInstance(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }


    @BeforeEach
    void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            //em.createNamedQuery("Parent.deleteAllRows").executeUpdate();
            a1 = new Address("Falkonner alle", 2800);
            a2 = new Address("Nørregaard", 3500);
            em.persist(a1);
            em.persist(a2);
            p1 = new Person("TestFirstName","TestLastName", "TestEmail", a1);
            p2 = new Person("TestFirstName2", "TestLastName2","TestEmail2", a2);
            em.persist(p1);
            em.persist(p2);
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