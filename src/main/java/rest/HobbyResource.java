package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.HobbyDTO;
import dtos.PersonDTO;
import errorhandling.EntityNotFoundException;
import dtos.ResponseDTO;
import errorhandling.InternalErrorException;
import facades.HobbyFacade;
import utils.EMF_Creator;
import utils.StatusCode;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

@Path("hobby")
public class HobbyResource {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final HobbyFacade FACADE = HobbyFacade.getInstance(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getHobbies() {
        try {
            Set<HobbyDTO> hobbyDTOs = FACADE.getHobbies();
            return Response.ok().entity(GSON.toJson(hobbyDTOs)).build();
        }
        catch (EntityNotFoundException e) {
            ResponseDTO response = new ResponseDTO(StatusCode.NOT_FOUND, e.getMessage());
            return Response.status(StatusCode.NOT_FOUND).entity(GSON.toJson(response)).build();
        }
    }

    @GET
    @Path("/search/{query}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getMatches(@PathParam("query") String query) {
        Set<HobbyDTO> hobbyDTOs = FACADE.searchHobbies(query);
        return Response.ok().entity(hobbyDTOs).build();
    }

    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getNumberOfHobbies() {
        try {
            int hobbyCount = FACADE.getHobbies().size();
            return Response.ok().entity(hobbyCount).build();
        }
        catch (EntityNotFoundException e) {
            return Response.ok().entity(0).build();
        }
    }

    @GET
    @Path("{name}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getHobbyByName(@PathParam("name") String name) {
        try {
            PersonDTO.InnerHobbyDTO hobbyDTOs = FACADE.getHobbyByName(name);
            return Response.ok().entity(GSON.toJson(hobbyDTOs)).build();
        }
        catch (EntityNotFoundException e) {
            ResponseDTO response = new ResponseDTO(StatusCode.NOT_FOUND, e.getMessage());
            return Response.status(StatusCode.NOT_FOUND).entity(GSON.toJson(response)).build();
        }
    }

    @GET
    @Path("{name}/persons")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getPersonsByHobbyName(@PathParam("name") String name) {
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
    @Path("{name}/persons/count")
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
