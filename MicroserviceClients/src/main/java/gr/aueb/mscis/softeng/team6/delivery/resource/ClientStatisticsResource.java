package gr.aueb.mscis.softeng.team6.delivery.resource;


import java.util.List;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.rest.client.inject.RestClient;

public class ClientStatisticsResource {

  @Inject
  @RestClient
  OrderResource orderResource;

  /**
   * List the most frequent clients of a store during a certain time period.
   *
   * @param id the store's ID.
   * @param start the period's start date.
   * @param end the period's end date.
   * @param max the maximum number of clients returned.
   */
  @GET
  @Transactional
  @Path("clients")
  @RolesAllowed({"admin", "manager"})
  @Operation(description = "Frequent clients")
  public Response clients(
    @PathParam("store") Long id,
    @QueryParam("start") @NotNull LocalDateTime start,
    @QueryParam("end") @NotNull LocalDateTime end,
    @QueryParam("max") @DefaultValue("10") Integer max) {
    Response response = orderResource.getTopClientsOfAStoreForAPeriod(id, start, end, max);
    Result<List<UUID>> result = (Result<List<UUID>>) response.getEntity();
    List<UUID> listOfClients = result.result();
    return Response.ok(new Result<List<UUID>>(listOfClients)).build();
  }


  /**
   * Generic result wrapper.
   *
   * @since 1.0.0
   */
  protected record Result<T>(T result) implements Serializable {}

}
