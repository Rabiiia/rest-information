package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.PhoneDTO;
import errorhandling.EntityNotFoundException;
import dtos.ResponseDTO;
import errorhandling.InternalErrorException;
import facades.PhoneFacade;
import utils.EMF_Creator;
import utils.StatusCode;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

@Path("phone")
public class PhoneResource {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final PhoneFacade FACADE = PhoneFacade.getInstance(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getPhones() {
        Set<PhoneDTO> phoneDTOs = FACADE.getPhones();
        return Response.ok().entity(GSON.toJson(phoneDTOs)).build();
    }

    @DELETE
    @Path("{number}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response removePhone(@PathParam("number") int number) {
        try {
            FACADE.removePhone(number);
            ResponseDTO response = new ResponseDTO(StatusCode.OK, "Phone removed!");
            return Response.ok().entity(GSON.toJson(response)).build();
        } catch (EntityNotFoundException e) {
            ResponseDTO response = new ResponseDTO(StatusCode.NOT_FOUND, e.getMessage());
            return Response.status(StatusCode.NOT_FOUND).entity(GSON.toJson(response)).build();
        } catch (InternalErrorException e) {
            ResponseDTO response = new ResponseDTO(StatusCode.NOT_FOUND, e.getMessage());
            return Response.status(StatusCode.INTERNAL_ERROR).entity(GSON.toJson(response)).build();
        }
    }
}
