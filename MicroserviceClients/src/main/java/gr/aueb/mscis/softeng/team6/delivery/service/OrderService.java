package gr.aueb.mscis.softeng.team6.delivery.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@ApplicationScoped
@Path("/stats")
@RegisterRestClient(configKey = "orders-api")
public interface OrderService {

  // Get the clients of a store who made most orders between start (time) and end (time)
  @GET
  @Path("/{store}/topClientUUIDs")
  List<UUID> getTopClientsOfAStoreForAPeriod(
      @PathParam("store") Long storeID,
      @QueryParam("start") @NotNull LocalDateTime start,
      @QueryParam("end") @NotNull LocalDateTime end,
      @QueryParam("max") @DefaultValue("10") Integer max);
}
