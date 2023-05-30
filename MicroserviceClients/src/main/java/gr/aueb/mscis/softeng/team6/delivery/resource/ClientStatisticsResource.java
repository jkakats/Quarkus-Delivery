package gr.aueb.mscis.softeng.team6.delivery.resource;


import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;

import gr.aueb.mscis.softeng.team6.delivery.domain.Client;
import gr.aueb.mscis.softeng.team6.delivery.health.ServiceState;
import gr.aueb.mscis.softeng.team6.delivery.persistence.ClientRepository;
import gr.aueb.mscis.softeng.team6.delivery.serialization.mapper.ClientMapper;
import gr.aueb.mscis.softeng.team6.delivery.service.OrderService;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@RequestScoped
@Path("/stats/{store}")
public class ClientStatisticsResource {

  @Inject protected ClientMapper clientMapper;
  @Inject protected ClientRepository clientRepository;
  @Inject
  @RestClient
  OrderService orderResource;

  @Inject
  ServiceState serviceState;

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
  @Path ("/clients")
  @Counted(name = "countFrequentClients", description = "Count how many times clients has invoked")
  @Timed(name = "timeFrequentClients", description = "How long it takes to invoke clients")
  @CircuitBreaker(
    requestVolumeThreshold = 4,
    failureRatio = 0.5,
    delay = 4000,
    successThreshold = 2)
  @Fallback()
  @Retry(maxRetries = 2)
  @Operation(description = "The manager of a store provides the store's id, a number of clients who wish to be found, " +
    "the start date and the end date of a period. With these parameters operation return above specified number of customers" +
    "who ordered most times from this store in above defined period.", summary = "Frequent clients of a store for a certain" +
    "period")
  public Response clients (
    @PathParam("store") Long id,
    @QueryParam("start") @NotNull LocalDateTime start,
    @QueryParam("end") @NotNull LocalDateTime end,
    @QueryParam("max") @DefaultValue("10") Integer max) {
    if(!serviceState.isHealthyState()) {
      try {
        TimeUnit.MILLISECONDS.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    List<UUID> clientUUIDs = orderResource.getTopClientsOfAStoreForAPeriod(id, start, end, max);
    List<Client> clients = new ArrayList<>();
    for (UUID uuid : clientUUIDs) {
      var c = clientRepository.findByIdOptional(uuid).orElseThrow();
      clients.add (c);
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
