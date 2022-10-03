package errorhandling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class EntityNotFoundExceptionMapper implements ExceptionMapper<EntityNotFoundException> {
    static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    static final int CODE = 404;

    @Context
    ServletContext context;

    @Override
    public Response toResponse(EntityNotFoundException e) {
        Logger.getLogger(EntityNotFoundExceptionMapper.class.getName()).log(Level.SEVERE, null, e);
        ExceptionDTO err = new ExceptionDTO(CODE, e.getMessage());

        return Response.status(CODE)
                .entity(GSON.toJson(err))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
