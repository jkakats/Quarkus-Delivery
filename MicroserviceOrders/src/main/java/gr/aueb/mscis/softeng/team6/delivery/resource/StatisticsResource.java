package gr.aueb.mscis.softeng.team6.delivery.resource;

import gr.aueb.mscis.softeng.team6.delivery.health.ServiceState;
import gr.aueb.mscis.softeng.team6.delivery.service.ClientService;
import gr.aueb.mscis.softeng.team6.delivery.service.StatisticsService;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.security.PermitAll;
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
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Statistics resource class.
 *
 * @since 1.0.0
 * @version 1.0.1
 */
@RequestScoped
@Path("/stats/{store}")
public class StatisticsResource {
  @Inject protected StatisticsService service;
  @Inject protected JsonWebToken jwt;

  @Inject
  @RestClient
  ClientService clientService;

  @Inject
  ServiceState serviceState;

  /**
   * List the most frequent clients of a store during a certain time period.
   *
   * @param store_id the store's ID.
   * @param start the period's start date.
   * @param end the period's end date.
   * @param max the maximum number of clients returned.
   */
  @GET
  @Transactional
  @Path("/topClientUUIDs")
  @PermitAll
  @Counted(name="countTopClientUUIDs",description = "Count how many times topClientUUIDs has been invoked")
  @Timed(name="timeTopClientUUIDs",description = "How long it takes to invoke topClientUUIDs")
  @Operation(summary = "Frequent clients")
  public Response clients(
      @PathParam("store") Long store_id,
      @QueryParam("start") @NotNull LocalDateTime start,
      @QueryParam("end") @NotNull LocalDateTime end,
      @QueryParam("max") @DefaultValue("10") Integer max) {
    //JwtUtil.checkManager(jwt, store_id);
    if(!serviceState.isHealthyState()) {
      try {
        TimeUnit.MILLISECONDS.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    var clients = service.findFrequentClients(store_id, start, end, max);
    var result = clients.stream().toList();
    return Response.ok((result)).build();
  }

  /**
   * View the average delivery time of a store for a certain area.
   *
   * @param id the store's ID.
   * @param zipCode the area's zip code.
   */
  @GET
  @Transactional
  @Path("delivery")
  @RolesAllowed({"admin", "manager"})
  @Counted(name="countDelivery",description = "Count how many times avg delivery time service has been invoked")
  @Timed(name="timeDelivery",description = "How long it takes to invoke avg delivery time service")
  @Timeout(value = 2000,unit = ChronoUnit.MILLIS)
  @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 4000, successThreshold = 2)
  @Fallback(fallbackMethod = "fallback")
  @Operation(summary = "Average delivery time", description = "View the average delivery time of a store for a certain area")
  public Response delivery( @PathParam("store") Long id, @QueryParam("zip_code") @NotNull Integer zipCode) {
    JwtUtil.checkManager(jwt, id);
    List<String> clientIds = clientService.getClientIds(zipCode);
    var average =
        service.getAverageDeliveryTime(id, clientIds);
    return Response.ok(new Result<>(average)).build();
  }

  /**
   * View the store's rush hours for a given week.
   *
   * @param store_id the store's ID.
   * @param week the week to check.
   * @param limit the minimum number of orders.
   */
  @GET
  @Transactional
  @Path("rush")
  @RolesAllowed({"admin", "manager"})
  @Counted(name="countRush",description = "Count how many times rush hours service has been invoked")
  @Timed(name="timeRush",description = "How long it takes to invoke rush hours service")
  @Operation(summary = "Rush hours", description = "View the store's rush hours for a given week")
  public Response rush(
      @PathParam("store") Long store_id,
      @QueryParam("week") @NotNull LocalDateTime week,
      @QueryParam("limit") @DefaultValue("5") Integer limit) {
    JwtUtil.checkManager(jwt, store_id);
    week = StatisticsService.truncateToWeek(week);
    var hours = service.getRushHours(store_id, week, limit);
    return Response.ok(new Result<>(hours)).build();
  }

  /**
   * Generic result wrapper.
   *
   * @since 1.0.0
   */
  protected record Result<T>(T result) implements Serializable {}

  @Counted(name="countAvgDeliveryFallback",description = "Count how many times AvgDeliveryFallback service has been invoked")
  @Timed(name="timeAvgDeliveryFallback",description = "How long it takes to invoke AvgDeliveryFallback service")
  public Response fallback(@PathParam("store") Long id, @QueryParam("zip_code") @NotNull Integer zipCode){
    System.out.println("fallback");
    return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorMessage("Remote Microservice down")).build();
  }

}
