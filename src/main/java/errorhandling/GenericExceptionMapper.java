/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package errorhandling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author jobe
 */

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Context
    ServletContext context;

    @Override
    public Response toResponse(Throwable e) {
        Logger.getLogger(GenericExceptionMapper.class.getName()).log(Level.SEVERE, null, e);
        Response.StatusType type = getStatusType(e);
        ExceptionDTO err;
        if (e instanceof WebApplicationException) {
            err = new ExceptionDTO(type.getStatusCode(), ((WebApplicationException) e).getMessage());
        } else {
            err = new ExceptionDTO(type.getStatusCode(), type.getReasonPhrase());
        }
        return Response.status(type.getStatusCode())
                .entity(GSON.toJson(err))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private Response.StatusType getStatusType(Throwable e) {
        if (e instanceof WebApplicationException) {
            return ((WebApplicationException) e).getResponse().getStatusInfo();
        }
        return Response.Status.INTERNAL_SERVER_ERROR;
    }

}
