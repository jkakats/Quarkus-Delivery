package gr.aueb.mscis.softeng.team6.delivery.resource;

import java.sql.SQLIntegrityConstraintViolationException;
import javax.persistence.PersistenceException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

/** Response mapper for {@link PersistenceException}. */
@Provider
public class PersistenceExceptionMapper implements ExceptionMapper<PersistenceException> {
  @Override
  @APIResponse(responseCode = "409", description = "Unique constraint failed")
  public Response toResponse(PersistenceException exception) {
    var cause = exception.getCause();
    if (cause.getCause() instanceof SQLIntegrityConstraintViolationException) {
      return Response.status(Response.Status.CONFLICT)
          .entity(new ErrorMessage("Unique constraint violation"))
          .build();
    } else {
      exception.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(new ErrorMessage(cause))
          .build();
    }
  }
}
