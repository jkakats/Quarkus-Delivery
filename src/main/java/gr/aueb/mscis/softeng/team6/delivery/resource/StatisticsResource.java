package gr.aueb.mscis.softeng.team6.delivery.resource;

import gr.aueb.mscis.softeng.team6.delivery.domain.Area;
import gr.aueb.mscis.softeng.team6.delivery.domain.Store;
import gr.aueb.mscis.softeng.team6.delivery.serialization.mapper.ClientMapper;
import gr.aueb.mscis.softeng.team6.delivery.service.StatisticsService;
import java.io.Serializable;
import java.time.LocalDateTime;
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

/**
 * Statistics resource class.
 *
 * @since 1.0.0
 */
@RequestScoped
@Path("/stats/{store}")
public class StatisticsResource {
  @Inject protected StatisticsService service;
  @Inject protected ClientMapper clientMapper;
  @Inject protected JsonWebToken jwt;

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
    JwtUtil.checkManager(jwt, id);
    var clients = service.findFrequentClients(new Store().setId(id), start, end, max);
    var result = new Result<>(clients.stream().map(clientMapper::serialize).toList());
    return Response.ok(result).build();
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
  public Response delivery(
      @PathParam("store") Long id, @QueryParam("zip_code") @NotNull Integer zipCode) {
    JwtUtil.checkManager(jwt, id);
    var average =
        service.getAverageDeliveryTime(new Store().setId(id), new Area().setZipCode(zipCode));
    return Response.ok(new Result<>(average)).build();
  }

  /**
   * View the store's rush hours for a given week.
   *
   * @param id the store's ID.
   * @param week the week to check.
   * @param limit the minimum number of orders.
   */
  @GET
  @Transactional
  @Path("rush")
  @RolesAllowed({"admin", "manager"})
  @Operation(description = "Rush hours")
  public Response rush(
      @PathParam("store") Long id,
      @QueryParam("week") @NotNull LocalDateTime week,
      @QueryParam("limit") @DefaultValue("5") Integer limit) {
    JwtUtil.checkManager(jwt, id);
    week = StatisticsService.truncateToWeek(week);
    var hours = service.getRushHours(new Store().setId(id), week, limit);
    return Response.ok(new Result<>(hours)).build();
  }

  private record Result<T>(T result) implements Serializable {}
}
