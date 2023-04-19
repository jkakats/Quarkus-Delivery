package gr.aueb.mscis.softeng.team6.delivery.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@ApplicationScoped
@Path("/orders")
@RegisterRestClient(configKey = "client-api")
public interface OrderResource {

  //Get the clients of a store who made most orders between start (time) and end (time)
  @GET
  @Path("/{store}/topClientIDs")
  @Transactional
  List<UUID> getTopClientsOfAStoreForAPeriod (@PathParam("store") Long storeID,
                                              @QueryParam("start") @NotNull LocalDateTime start,
                                              @QueryParam("end") @NotNull LocalDateTime end,
                                              @QueryParam("max") @DefaultValue("10") Integer max);


}
