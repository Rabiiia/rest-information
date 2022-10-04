package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.PersonDTO;
import errorhandling.EntityFoundException;
import errorhandling.EntityNotFoundException;
import errorhandling.ExceptionDTO;
import errorhandling.InternalErrorException;
import facades.PersonFacade;
import utils.EMF_Creator;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/person")
public class PersonResource {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final PersonFacade FACADE = PersonFacade.getInstance(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response createPerson(String jsonInput) {
        PersonDTO pdto = GSON.fromJson(jsonInput, PersonDTO.class);
        try {
            FACADE.createPerson(pdto);
            return Response.noContent().build();
        }
        catch (InternalErrorException e) {
            ExceptionDTO edto = new ExceptionDTO(500, e.getMessage());
            return Response.ok().entity(GSON.toJson(edto)).build();
        }
    }

    @GET
    @Path("{number}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getPersonByNumber(@PathParam("number") int number) {
        try {
            PersonDTO pdto = FACADE.getPersonByNumber(number);
            return Response.ok().entity(GSON.toJson(pdto)).build();
        }
        catch (EntityNotFoundException e) {
            ExceptionDTO edto = new ExceptionDTO(404, e.getMessage());
            return Response.ok().entity(GSON.toJson(edto)).build();
        }
        catch (InternalErrorException e) {
            ExceptionDTO edto = new ExceptionDTO(500, e.getMessage());
            return Response.ok().entity(GSON.toJson(edto)).build();
        }
    }

    @PUT
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response updatePerson(@PathParam("id") int id, String jsonInput) {
        PersonDTO personDTO = GSON.fromJson(jsonInput, PersonDTO.class);
        personDTO.setId(id);
        try {
            FACADE.updatePerson(personDTO);
            return Response.noContent().build();
        }
        catch (EntityNotFoundException e) {
            ExceptionDTO edto = new ExceptionDTO(404, e.getMessage());
            return Response.ok().entity(GSON.toJson(edto)).build();
        }
        catch (EntityFoundException e) {
            ExceptionDTO edto = new ExceptionDTO(405, e.getMessage());
            return Response.ok().entity(GSON.toJson(edto)).build();
        }
        catch (InternalErrorException e) {
            ExceptionDTO edto = new ExceptionDTO(500, e.getMessage());
            return Response.ok().entity(GSON.toJson(edto)).build();
        }
    }

    @DELETE
    @Path("{id}/address")
    public Response removeAddressFromPerson(@PathParam("id") int id) {
        try {
            FACADE.removeAddressFromPerson(id);
            return Response.noContent().build();
        }
        catch (EntityNotFoundException e) {
            ExceptionDTO edto = new ExceptionDTO(404, e.getMessage());
            return Response.ok().entity(GSON.toJson(edto)).build();
        }
        catch (InternalErrorException e) {
            ExceptionDTO edto = new ExceptionDTO(500, e.getMessage());
            return Response.ok().entity(GSON.toJson(edto)).build();
        }
    }

}
