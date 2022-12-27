package gr.aueb.mscis.softeng.team6.delivery.resource;

import java.util.NoSuchElementException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

/** Response mapper for {@link NoSuchElementException}. */
@Provider
public class NoSuchElementExceptionMapper implements ExceptionMapper<NoSuchElementException> {
  @Override
  @APIResponse(responseCode = "404", description = "Not found")
  public Response toResponse(NoSuchElementException exception) {
    return Response.status(Response.Status.NOT_FOUND).build();
  }
}
