package gr.aueb.mscis.softeng.team6.delivery.resource;


import gr.aueb.mscis.softeng.team6.delivery.domain.Client;
import gr.aueb.mscis.softeng.team6.delivery.serialization.mapper.ClientMapper;
import gr.aueb.mscis.softeng.team6.delivery.service.OrderResource;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
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
@Path("/stats/{store}")
public class ClientStatisticsResource {

  @Inject protected ClientMapper clientMapper;
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
  @RolesAllowed({"admin", "manager"})
  @Operation(description = "Frequent clients")
  public Response clients(
    @PathParam("store") Long id,
    @QueryParam("start") @NotNull LocalDateTime start,
    @QueryParam("end") @NotNull LocalDateTime end,
    @QueryParam("max") @DefaultValue("10") Integer max) {
    List<UUID> clientUUIDs = orderResource.getTopClientsOfAStoreForAPeriod(id, start, end, max);
    //Εχεις παρει απο το Microservice με τα orders μια λιστα με τα uuids των πιο frequent πελατων
    //Πρεπει να βρεις τους clients με αυτα τα UUIDs στην βαση σου με καποιο query
    List<Client> clients = new ArrayList<>(); // Προφανως δεν χρειαζεται να ειναι arraylist απλα το εβαλα για να γινεται initialized προσωρινα
    //Και τελος να τους μετατρεψεις σε ClientDto για να τους στειλεις ως response (ετοιμο στην επομενη γραμμη)
    var result = clients.stream().map(clientMapper::serialize).toList();
    return Response.ok(new Result<>(result)).build();
    //Για να τεσταρεις αμα ειναι οκ θα πρεπει να φτιαξεις mock με μια συγκεκριμενη συμπεριφορα οπως σελ 10 των διαφανειων για rest client
  }


  /**
   * Generic result wrapper.
   *
   * @since 1.0.0
   */
  protected record Result<T>(T result) implements Serializable {}

}
