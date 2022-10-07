package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.PersonDTO;
import errorhandling.EntityFoundException;
import errorhandling.EntityNotFoundException;
import dtos.ResponseDTO;
import errorhandling.InternalErrorException;
import facades.PersonFacade;
import utils.EMF_Creator;
import utils.FacadeUtility;
import utils.StatusCode;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

@Path("person")
public class PersonResource {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final PersonFacade FACADE = PersonFacade.getInstance(EMF);
    private static final FacadeUtility UTIL = FacadeUtility.getInstance(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getPersons() {
        try {
            Set<PersonDTO> pdtos = FACADE.getPersons();
            return Response.ok().entity(GSON.toJson(pdtos)).build();
        }
        catch (EntityNotFoundException e) {
            ResponseDTO response = new ResponseDTO(StatusCode.NOT_FOUND, e.getMessage());
            return Response.status(StatusCode.NOT_FOUND).entity(GSON.toJson(response)).build();
        }
    }

    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getNumberOfPersons() {
        try {
            int personCount = FACADE.getPersons().size();
            return Response.ok().entity(personCount).build();
        }
        catch (EntityNotFoundException e) {
            return Response.ok().entity(0).build();
        }
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response createPerson(String jsonInput) {
        PersonDTO pdto = GSON.fromJson(jsonInput, PersonDTO.class);
        try {
            FACADE.createPerson(pdto);
            ResponseDTO response = new ResponseDTO(StatusCode.OK, "Success!");
            return Response.ok().entity(GSON.toJson(response)).build();
        }
        catch (EntityNotFoundException e) {
            ResponseDTO response = new ResponseDTO(StatusCode.NOT_FOUND, e.getMessage());
            return Response.status(StatusCode.NOT_FOUND).entity(GSON.toJson(response)).build();
        }
        catch (EntityFoundException e) {
            ResponseDTO response = new ResponseDTO(StatusCode.FOUND, e.getMessage());
            return Response.status(StatusCode.FOUND).entity(GSON.toJson(response)).build();
        }
        catch (InternalErrorException e) {
            ResponseDTO response = new ResponseDTO(StatusCode.INTERNAL_ERROR, e.getMessage());
            return Response.status(StatusCode.INTERNAL_ERROR).entity(GSON.toJson(response)).build();
        }
    }

    @PUT
    //@Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response updatePerson(/*@PathParam("id") int id,*/ String jsonInput) {
        PersonDTO personDTO = GSON.fromJson(jsonInput, PersonDTO.class);//personDTO.setId(id);
        try {
            FACADE.updatePerson(personDTO);
            ResponseDTO response = new ResponseDTO(StatusCode.OK, "Changes saved!");
            return Response.ok().entity(GSON.toJson(response)).build();
        }
        catch (EntityNotFoundException e) {
            ResponseDTO response = new ResponseDTO(StatusCode.NOT_FOUND, e.getMessage());
            return Response.status(StatusCode.NOT_FOUND).entity(GSON.toJson(response)).build();
        }
        catch (EntityFoundException e) {
            ResponseDTO response = new ResponseDTO(StatusCode.FOUND, e.getMessage());
            return Response.ok().entity(GSON.toJson(response)).build();
        }
        catch (InternalErrorException e) {
            ResponseDTO response = new ResponseDTO(StatusCode.INTERNAL_ERROR, e.getMessage());
            return Response.status(StatusCode.INTERNAL_ERROR).entity(GSON.toJson(response)).build();
        }
    }

    @DELETE
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response deletePerson(@PathParam("id") int id) {
        try {
            FACADE.deletePerson(id);
            ResponseDTO response = new ResponseDTO(StatusCode.OK, "Deleted!");
            return Response.ok().entity(GSON.toJson(response)).build();
        }
        catch (EntityNotFoundException e) {
            ResponseDTO response = new ResponseDTO(StatusCode.NOT_FOUND, e.getMessage());
            return Response.status(StatusCode.NOT_FOUND).entity(GSON.toJson(response)).build();
        }
        catch (InternalErrorException e) {
            ResponseDTO response = new ResponseDTO(StatusCode.INTERNAL_ERROR, e.getMessage());
            return Response.status(StatusCode.INTERNAL_ERROR).entity(GSON.toJson(response)).build();
        }
    }

    @GET
    @Path("id/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getPersonById(@PathParam("id") int id) {
        try {
            PersonDTO personDTO = new PersonDTO(UTIL.personExists(id));
            return Response.ok().entity(GSON.toJson(personDTO)).build();
        }
        catch (EntityNotFoundException e) {
            ResponseDTO response = new ResponseDTO(StatusCode.NOT_FOUND, e.getMessage());
            return Response.status(StatusCode.NOT_FOUND).entity(GSON.toJson(response)).build();
        }
    }

    @GET
    @Path("number/{number}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getPersonByNumber(@PathParam("number") int number) {
        try {
            PersonDTO personDTO = FACADE.getPersonByNumber(number);
            return Response.ok().entity(GSON.toJson(personDTO)).build();
        }
        catch (EntityNotFoundException e) {
            ResponseDTO response = new ResponseDTO(StatusCode.NOT_FOUND, e.getMessage());
            return Response.status(StatusCode.NOT_FOUND).entity(GSON.toJson(response)).build();
        }
    }

    @DELETE
    @Path("{id}/address")
    @Produces({MediaType.APPLICATION_JSON})
    public Response removeAddressFromPerson(@PathParam("id") int id) {
        try {
            FACADE.removeAddressFromPerson(id);
            ResponseDTO response = new ResponseDTO(StatusCode.OK, "Address removed!");
            return Response.ok().entity(GSON.toJson(response)).build();
        }
        catch (EntityNotFoundException e) {
            ResponseDTO response = new ResponseDTO(StatusCode.NOT_FOUND, e.getMessage());
            return Response.status(StatusCode.NOT_FOUND).entity(GSON.toJson(response)).build();
        }
        catch (InternalErrorException e) {
            ResponseDTO response = new ResponseDTO(StatusCode.INTERNAL_ERROR, e.getMessage());
            return Response.status(StatusCode.INTERNAL_ERROR).entity(GSON.toJson(response)).build();
        }
    }

    @GET
    @Path("has-hobby/{name}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getPersonByHobbyName(@PathParam("name") String name) {
        try {
            Set<PersonDTO> personDTOs = FACADE.getPersonsByHobbyName(name);
            return Response.ok().entity(GSON.toJson(personDTOs)).build();
        }
        catch (EntityNotFoundException e) {
            ResponseDTO response = new ResponseDTO(StatusCode.NOT_FOUND, e.getMessage());
            return Response.status(StatusCode.NOT_FOUND).entity(GSON.toJson(response)).build();
        }
        catch (InternalErrorException e) {
            ResponseDTO response = new ResponseDTO(StatusCode.INTERNAL_ERROR, e.getMessage());
            return Response.status(StatusCode.INTERNAL_ERROR).entity(GSON.toJson(response)).build();
        }
    }

    @GET
    @Path("has-hobby/{name}/count")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getNumberOfPersonsByHobbyName(@PathParam("name") String name) {
        try {
            int personCount = FACADE.getPersonsByHobbyName(name).size();
            return Response.ok().entity(personCount).build();
        }
        catch (EntityNotFoundException e) {
            return Response.ok().entity(0).build();
        }
        catch (InternalErrorException e) {
            ResponseDTO response = new ResponseDTO(StatusCode.INTERNAL_ERROR, e.getMessage());
            return Response.status(StatusCode.INTERNAL_ERROR).entity(GSON.toJson(response)).build();
        }
    }
}
