package facades;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.AddressDTO;
import dtos.HobbyDTO;
import dtos.PersonDTO;
import dtos.PhoneDTO;
import entities.Hobby;
import errorhandling.EntityFoundException;
import errorhandling.EntityNotFoundException;
import errorhandling.InternalErrorException;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;
import utils.FacadeUtility;

import javax.persistence.EntityManagerFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Disabled
class PersonFacadeTest {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactoryForTest();
    private static final PersonFacade FACADE = PersonFacade.getInstance(EMF);
    private static final FacadeUtility UTIL = FacadeUtility.getInstance(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static PersonDTO p1, p2, p3, p4;
    private static PersonDTO.InnerHobbyDTO h1, h2, h3, h4, h5;

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
        Populator.populateForTest();
        try {
            p1 = FACADE.getPersonByNumber(11111111);
            p2 = FACADE.getPersonByNumber(22222222);
            p3 = FACADE.getPersonByNumber(33333333);
            p4 = FACADE.getPersonByNumber(44444444);

            HobbyFacade hobbyFacade = HobbyFacade.getInstance(EMF);
            h1 = hobbyFacade.getHobbyByName("Musik");
            h2 = hobbyFacade.getHobbyByName("Fuglekiggeri");
            h3 = hobbyFacade.getHobbyByName("Søvn");
            h4 = hobbyFacade.getHobbyByName("Yoga");
            h5 = hobbyFacade.getHobbyByName("Squash");
        }
        catch (EntityNotFoundException e) {
            System.out.println(e);
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void testCreatePerson() {
        // Mock up a PersonDTO
        PersonDTO pdto = new PersonDTO("Bertram", "Jensen", "bertramjensen@mail.dk", new AddressDTO("Nørgaardsvej 30", 2800));
        pdto.addPhone(new PhoneDTO(88888888, "Home"));
        pdto.addPhone(new PhoneDTO(77777777, "Mobile"));
        pdto.addHobby(new PersonDTO.InnerHobbyDTO("Musik"));
        pdto.addHobby(new PersonDTO.InnerHobbyDTO("Fuglekiggeri"));
        pdto.addHobby(new PersonDTO.InnerHobbyDTO("Søvn"));
        pdto.addHobby(new PersonDTO.InnerHobbyDTO("Squash"));
        // Try to persist person
        try {
            FACADE.createPerson(pdto);
            System.out.println("Successfully created a new person!");
        }
        catch (EntityFoundException | EntityNotFoundException | InternalErrorException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testUpdatePerson() {
        // Mock up updates as a person DTO
        PersonDTO pdto = new PersonDTO("Updated First Name", "Updated Last Name", "Updated Email", new AddressDTO("New Street", 2800));
        pdto.addPhone(new PhoneDTO(11111111, "New Phone"));
        pdto.addHobby(new PersonDTO.InnerHobbyDTO("Fuglekiggeri"));
        pdto.setId(p1.getId());// <-- Set id to an existing person id
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
    public void testGetPersonByNumber() {
        try {
            PersonDTO pdto = FACADE.getPersonByNumber(12345678);
            System.out.println(GSON.toJson(pdto));
        }
        catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testRemoveAddressFromPerson() {
        AddressDTO address = p3.getAddress();
        try {
            FACADE.removeAddressFromPerson(p3.getId());
            assertNull(FACADE.getPersonByNumber(33333333).getAddress());
            System.out.println("Successfully removed a person's address!");
            FACADE.removeAddressFromPerson(p4.getId());
            assertNull(FACADE.getPersonByNumber(44444444).getAddress());
            System.out.println("Successfully removed a person's address!");
            UTIL.addressExists(address.getStreet(), address.getZipcode());
            System.out.println("The address still exists in the database...");
        }
        catch (EntityNotFoundException e) {
            System.out.println("Address removed from the database (no one lived there)!");
            System.out.println(e.getMessage());
        }
        catch (InternalErrorException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testGetPersonsByHobbyName() {
        try {
            Set<PersonDTO> personDTOs = FACADE.getPersonsByHobbyName("Fuglekiggeri");
            System.out.println(GSON.toJson(personDTOs));
        }
        catch (EntityNotFoundException | InternalErrorException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testGetNumberPersonsByHobbyName() {
        try {
            int personCount = FACADE.getPersonsByHobbyName("Fuglekiggeri").size();
            System.out.println(personCount);
        }
        catch (EntityNotFoundException | InternalErrorException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testGetPersonsByZipcode() {
        try {
            Set<PersonDTO> personDTOs = FACADE.getPersonsByZipcode(2800);
            System.out.println(GSON.toJson(personDTOs));
        }
        catch (EntityNotFoundException | InternalErrorException e) {
            System.out.println(e.getMessage());
        }
    }

}
