package gr.aueb.mscis.softeng.team6.delivery.resource;


import gr.aueb.mscis.softeng.team6.delivery.domain.Client;
import gr.aueb.mscis.softeng.team6.delivery.persistence.ClientRepository;
import gr.aueb.mscis.softeng.team6.delivery.serialization.mapper.ClientMapper;
import gr.aueb.mscis.softeng.team6.delivery.service.OrderService;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@RequestScoped
@Path("/stats/")
public class ClientStatisticsResource {

  @Inject protected ClientMapper clientMapper;
  @Inject protected ClientRepository clientRepository;
  @RestClient
  OrderService orderResource;

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
  @Path ("{store}/clients")
  @Operation(description = "Frequent clients")
  public Response clients(
    @PathParam("store") Long id,
    @QueryParam("start") @NotNull LocalDateTime start,
    @QueryParam("end") @NotNull LocalDateTime end,
    @QueryParam("max") @DefaultValue("10") Integer max) {
    List<UUID> clientUUIDs = orderResource.getTopClientsOfAStoreForAPeriod(id, start, end, max);
    List<Client> clients = new ArrayList<>();
    for (UUID uuid : clientUUIDs) {
      clients.add (clientRepository.findById(uuid));
    }
    var result = clients.stream().map(clientMapper::serialize).toList();
    return Response.ok(new Result<>(result)).build();
  }


  /**
   * Generic result wrapper.
   *
   * @since 1.0.0
   */
  protected record Result<T>(T result) implements Serializable {}

}
