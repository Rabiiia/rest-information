package errorhandling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.ResponseDTO;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class InternalErrorExceptionMapper  implements ExceptionMapper<InternalErrorException> {
    static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    static final int CODE = 500;

    @Context
    ServletContext context;

    @Override
    public Response toResponse(InternalErrorException e) {
        Logger.getLogger(InternalErrorExceptionMapper.class.getName()).log(Level.SEVERE, null, e);
        ResponseDTO err = new ResponseDTO(CODE, e.getMessage());

        return Response.status(CODE)
                .entity(GSON.toJson(err))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
