package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.AddressDTO;
import dtos.PersonDTO;
import dtos.PhoneDTO;
import entities.Address;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import io.restassured.http.ContentType;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

//Uncomment the line below, to temporarily disable this test
@Disabled
public class PersonResourceTest {
    private static final String SERVER_URL = "http://localhost/api";
    private static final int SERVER_PORT = 7777;
    private static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static HttpServer httpServer;
    private static EntityManagerFactory emf;
    private static Address a1, a2;
    private static Person p1, p2, p3, p4;
    private static Phone n1, n2, n3, n4;
    private static Hobby h1, h2, h3, h4;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //System.in.read();

        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {
        // Populate the database
        EntityManager em = emf.createEntityManager();
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

    @Test
    public void testServerIsUp() {
        System.out.println("Testing is server UP");
        given().when().get("/api").then().statusCode(200);
    }

    @Test
    public void testCreatePerson() {
        // Mock up a PersonDTO
        PersonDTO pdto = new PersonDTO("Bertram", "Jensen", "bertramjensen@mail.dk");
        pdto.addPhone(new PhoneDTO(87654321, "Home"));
        pdto.addPhone(new PhoneDTO(76543210, "Mobile"));
        pdto.setAddress(new AddressDTO("New Street", 2800));

        // Send request to REST API
        given()
                .header("Content-type", ContentType.JSON)
                .body(GSON.toJson(pdto))
                .when()
                .post("/person")
                // Validate response
                .then()
                .statusCode(
                        anyOf(
                                is(204), // No content when there's no exception
                                is(200)  // Content with ExceptionDTO when no id is found
                        )
                );
    }

    @Test
    public void testUpdatePerson() {
        // Mock up a PersonDTO
        PersonDTO pdto = new PersonDTO("Bertram", "Jensen", "bertramjensen@mail.dk");
        pdto.addPhone(new PhoneDTO(87654321, "Home"));
        pdto.addPhone(new PhoneDTO(76543210, "Mobile"));
        pdto.setAddress(new AddressDTO("New Street", 2800));
        pdto.setId(p1.getId());// <-- Set id to an existing person id

        // Send request to REST API
        given()
                .header("Content-type", ContentType.JSON)
                .body(GSON.toJson(pdto))
                .when()
                .put("/person/" + p1.getId())
                // Validate response
                .then()
                .statusCode(
                        anyOf(
                                is(204), // No content when there's no exception
                                is(200)  // Content with ExceptionDTO when no id is found
                        )
                );
    }

    @Test
    public void testRemoveAddressFromPerson() {
        // Send request to REST API
        given()
                .pathParam("id", 1)
                .delete("/person/{id}/address")
                // Validate response
                .then()
                .statusCode(
                        anyOf(
                                is(204), // No content when there's no exception
                                is(200)  // Content with ExceptionDTO when no id is found
                        )
                );
    }

}
