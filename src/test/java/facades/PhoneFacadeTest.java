package facades;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.PersonDTO;
import dtos.PhoneDTO;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;
import utils.FacadeUtility;

import javax.persistence.EntityManagerFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
class PhoneFacadeTest {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactoryForTest();
    private static final PhoneFacade FACADE = PhoneFacade.getInstance(EMF);
    private static final FacadeUtility UTIL = FacadeUtility.getInstance(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static PersonDTO p1, p2, p3, p4;
    private static PersonDTO.InnerHobbyDTO h1, h2, h3, h4, h5;

    public PhoneFacadeTest() {
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
        /*try {
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
        }*/
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void getPhones() {
        Set<PhoneDTO> phoneDTOs = FACADE.getPhones();
        assertTrue(phoneDTOs.size() > 0);
        System.out.println(GSON.toJson(phoneDTOs));
    }
}
