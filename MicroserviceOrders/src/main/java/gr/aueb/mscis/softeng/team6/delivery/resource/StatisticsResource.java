package gr.aueb.mscis.softeng.team6.delivery.resource;

import gr.aueb.mscis.softeng.team6.delivery.service.ClientService;
import gr.aueb.mscis.softeng.team6.delivery.service.StatisticsService;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
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
import org.eclipse.microprofile.jwt.JsonWebToken;
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

  @RestClient
  ClientService clientService;

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
  @Path("topClientUUIDs")
  @RolesAllowed({"admin", "manager"})
  @Operation(description = "Frequent clients")
  public Response clients(
      @PathParam("store") Long store_id,
      @QueryParam("start") @NotNull LocalDateTime start,
      @QueryParam("end") @NotNull LocalDateTime end,
      @QueryParam("max") @DefaultValue("10") Integer max) {
    JwtUtil.checkManager(jwt, store_id);
    var clients = service.findFrequentClients(store_id, start, end, max);
    var result = clients.stream().toList();
    return Response.ok(new Result<>(result)).build();
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
  @Operation(description = "Average delivery time")
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
  @Operation(description = "Rush hours")
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
}
