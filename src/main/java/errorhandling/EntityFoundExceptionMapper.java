package errorhandling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.ResponseDTO;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EntityFoundExceptionMapper implements ExceptionMapper<EntityFoundException> {
    static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    static final int CODE = 405;

    @Context
    ServletContext context;

    @Override
    public Response toResponse(EntityFoundException e) {
        Logger.getLogger(EntityFoundExceptionMapper.class.getName()).log(Level.SEVERE, null, e);
        ResponseDTO err = new ResponseDTO(CODE, e.getMessage());

        return Response.status(CODE)
                .entity(GSON.toJson(err))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
