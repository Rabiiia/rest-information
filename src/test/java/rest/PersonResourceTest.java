package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.AddressDTO;
import dtos.HobbyDTO;
import dtos.PersonDTO;
import dtos.PhoneDTO;
import errorhandling.EntityNotFoundException;
import facades.PersonFacade;
import facades.Populator;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.glassfish.grizzly.http.util.HttpStatus;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.parsing.Parser;
import java.net.URI;
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
    private static PersonDTO p1, p2, p3, p4;

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
        //Populate
        Populator.populateForTest();
        PersonFacade facade = PersonFacade.getInstance(emf);
        try {
            p1 = facade.getPersonByNumber(11111111);
            p2 = facade.getPersonByNumber(22222222);
            p3 = facade.getPersonByNumber(33333333);
            p4 = facade.getPersonByNumber(44444444);
        } catch (EntityNotFoundException e) {
            System.out.println(e);
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
        PersonDTO pdto = new PersonDTO("Bertram", "Jensen", "bertramjensen@mail.dk", new AddressDTO("New Street", 2800));
        pdto.addPhone(new PhoneDTO(87654321, "Home"));
        pdto.addPhone(new PhoneDTO(76543210, "Mobile"));

        // Send request to REST API
        Response response = given()
                .header("Content-type", ContentType.JSON)
                .body(GSON.toJson(pdto))
                .when()
                .post("/person");
        // Validate response
        response.then()
                .statusCode(
                        anyOf(
                                is(204), // No content when there's no exception
                                is(200)  // Content with ExceptionDTO when no id is found
                        )
                );
        // Print response
        System.out.println(response.getStatusCode());
        System.out.println(response.getHeaders());
        System.out.println(response.getBody().asString());
    }

    @Test
    public void testUpdatePerson() {
        // Mock up a PersonDTO
        PersonDTO pdto = new PersonDTO("Bertram", "Jensen", "bertramjensen@mail.dk", new AddressDTO("New Street", 2800));
        pdto.addPhone(new PhoneDTO(77654321, "Home"));
        pdto.addPhone(new PhoneDTO(66543210, "Mobile"));
        pdto.setId(p1.getId());// <-- Set id to an existing person id

        // Send request to REST API
        Response response = given()
                .header("Content-type", ContentType.JSON)
                .body(GSON.toJson(pdto))
                .when()
                .put("/person/" + p1.getId());
        // Validate response
        response.then()
                .statusCode(
                        anyOf(
                                is(204), // No content when there's no exception
                                is(200), // Content with ExceptionDTO when no id is found
                                is(404), // When you're trying to update a person that does not exist
                                is(405)  // When you're trying to persist phones that already exist
                        )
                );
        // Print response
        System.out.println(response.getStatusCode());
        System.out.println(response.getHeaders());
        System.out.println(response.getBody().asString());
    }

    @Test
    public void testRemoveAddressFromPerson() {
        // Send request to REST API
        Response response = given()
                .pathParam("id", 1)
                .delete("/person/{id}/address");
        // Validate response
        response.then()
                .statusCode(anyOf(
                        is(204), // No content when there's no exception
                        is(200)  // Content with ExceptionDTO when no id is found
                ));
        // Print response
        System.out.println(response.getStatusCode());
        System.out.println(response.getHeaders());
        System.out.println(response.getBody().asString());
    }

    @Test
    public void testGetPersonsByHobbyName() {
        String name = "Musik";

        // Send request to REST API
        Response response = given()
                .contentType(ContentType.JSON)
                //.pathParam("id", p1.getId()).when()
                .get("/person/has-hobby/{name}", name);
        // Validate response
        response.then()
                .assertThat()
                .statusCode(anyOf(
                        is(HttpStatus.OK_200.getStatusCode()),
                        is(HttpStatus.NOT_FOUND_404.getStatusCode())
                ));
        // Print response
        System.out.println(response.getStatusCode());
        System.out.println(response.getHeaders());
        System.out.println(response.getBody().asString());
    }

    @Test
    public void testGetNumberOfPersonsByHobbyName() {
        String name = "Musik";

        // Send request to REST API
        Response response = given()
                .contentType(ContentType.JSON)
                //.pathParam("id", p1.getId()).when()
                .get("/person/has-hobby/{name}/count", name);
        // Validate response
        response.then()
                .assertThat()
                .statusCode(anyOf(
                        is(HttpStatus.OK_200.getStatusCode()),
                        is(HttpStatus.NOT_FOUND_404.getStatusCode())
                ));
        // Print response
        System.out.println(response.getStatusCode());
        System.out.println(response.getHeaders());
        System.out.println(response.getBody().asString());
    }
}
