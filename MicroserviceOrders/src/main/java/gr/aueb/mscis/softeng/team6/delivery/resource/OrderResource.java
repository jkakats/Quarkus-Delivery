package gr.aueb.mscis.softeng.team6.delivery.resource;

import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;

import gr.aueb.mscis.softeng.team6.delivery.persistence.OrderRepository;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ClientDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.OrderDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.OrderReviewDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ProductDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.mapper.OrderMapper;
import gr.aueb.mscis.softeng.team6.delivery.service.ClientService;
import gr.aueb.mscis.softeng.team6.delivery.service.OrderService;
import gr.aueb.mscis.softeng.team6.delivery.service.ProductService;
import gr.aueb.mscis.softeng.team6.delivery.service.ReviewService;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.enums.Explode;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Order resource class.
 *
 * @since 1.0.0
 */
@RequestScoped
@Path("/orders")
public class OrderResource {
  @Inject protected OrderRepository repository;
  @Inject protected OrderMapper mapper;
  @Inject protected OrderService orderService;
  @Inject protected ReviewService reviewService;
  @Inject protected JsonWebToken jwt;

  @RestClient
  ClientService clientService;

  @RestClient
  ProductService productService;

  /** Get all the orders. */
  @GET
  @Transactional
  @RolesAllowed({"admin"})
  public Response list() {
    var orders = repository.streamAll().map(mapper::serialize).toList();
    return Response.ok(orders).build();
  }

  /**
   * Get a single order.
   *
   * @param uuid the order's UUID
   */
  @GET
  @Transactional
  @Path("{uuid}")
  @RolesAllowed({"admin", "manager", "client"})
  public Response read(@PathParam("uuid") UUID uuid) throws NoSuchElementException {
    var order = repository.findByIdOptional(uuid).orElseThrow();
    JwtUtil.checkManager(jwt, order.getStore_id());
    JwtUtil.checkClient(jwt, order.getClient_uuid());
    ClientDto clientdto = clientService.getClient(order.getClient_uuid());
    ProductDto productdto = productService.getProduct(order.getProducts().iterator().next().getProduct_id());
    return Response.ok(mapper.serialize(order)).build();
  }

  /**
   * Create a new order.
   *
   * @param dto a order DTO
   */
  @POST
  @Transactional
  @RolesAllowed({"admin", "client"})
  @APIResponses({
    @APIResponse(responseCode = "201", description = "Created"),
    @APIResponse(responseCode = "400", description = "Validation failed")
  })
  public Response create(@Context UriInfo uriInfo, @Valid OrderDto dto)
      throws PersistenceException {
    JwtUtil.checkClient(jwt, dto.client_uuid());
    var order = mapper.deserialize(dto);
    // NOTE: persistAndFlush doesn't work here
    order = repository.getEntityManager().merge(order);
    repository.flush();
    Boolean correctClient = clientService.getClientCheck();
    Boolean correctProducts = productService.getProductCheck();
    if(correctClient && correctProducts) {
      var uri = uriInfo.getRequestUriBuilder().path("{uuid}").build(order.getUuid());
      return Response.created(uri).build();
    }
    IllegalArgumentException exc= new IllegalArgumentException();
    return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorMessage(exc)).build();

  }

  /**
   * Update an existing order.
   *
   * @param uuid the order's UUID
   * @param dto the updated order DTO
   */
  @PUT
  @Transactional
  @Path("{uuid}")
  @RolesAllowed({"admin", "manager"})
  @APIResponses({
    @APIResponse(responseCode = "200", description = "Updated"),
    @APIResponse(responseCode = "400", description = "Validation failed")
  })
  public Response update(@PathParam("uuid") UUID uuid, @Valid OrderDto dto)
      throws NoSuchElementException, PersistenceException {
    var order = repository.findByIdOptional(uuid, PESSIMISTIC_WRITE).orElseThrow();
    JwtUtil.checkManager(jwt, order.getStore_id());
    mapper.update(order, dto);
    repository.persistAndFlush(order);
    return Response.ok(mapper.serialize(order)).build();
  }

  /**
   * Delete the given order.
   *
   * @param uuid the order's UUID
   */
  @DELETE
  @Transactional
  @Path("{uuid}")
  @RolesAllowed({"admin", "manager"})
  @APIResponse(responseCode = "204", description = "Deleted")
  public Response delete(@PathParam("uuid") UUID uuid) throws NoSuchElementException {
    var order = repository.findByIdOptional(uuid).orElseThrow();
    JwtUtil.checkManager(jwt, order.getStore_id());
    repository.delete(order);
    return Response.noContent().build();
  }

  /**
   * Confirm the given order.
   *
   * @param uuid the order's UUID
   * @param estimatedWait the estimated waiting time
   */
  @POST
  @Transactional
  @Path("{uuid}/confirm")
  @RolesAllowed({"admin", "manager"})
  @APIResponse(responseCode = "202", description = "Accepted")
  public Response confirm(
      @PathParam("uuid") UUID uuid, @FormParam("estimated_wait") @NotNull Long estimatedWait)
      throws NoSuchElementException {
    var order = repository.findByIdOptional(uuid, PESSIMISTIC_WRITE).orElseThrow();
    JwtUtil.checkManager(jwt, order.getStore_id());
    var response = Response.accepted();
    orderService.setMessageProvider(
        (client, id, cost, wait) -> response.entity(new Confirmation(id, cost, wait)));
    orderService.confirmOrder(order, estimatedWait);
    return response.build();
  }

  /**
   * Mark the given order as delivered.
   *
   * @param uuid the order's UUID
   */
  @POST
  @Transactional
  @Path("{uuid}/deliver")
  @RolesAllowed({"admin", "manager"})
  @APIResponse(responseCode = "202", description = "Accepted")
  public Response deliver(@PathParam("uuid") UUID uuid) throws NoSuchElementException {
    var order = repository.findByIdOptional(uuid, PESSIMISTIC_WRITE).orElseThrow();
    JwtUtil.checkManager(jwt, order.getStore_id());
    orderService.deliverOrder(order);
    return Response.accepted().build();
  }

  /**
   * Review the given order.
   *
   * @param uuid the UUID of the order
   * @param rating the rating of the review
   * @param comment an optional comment
   * @param productRatings the ratings of each product
   */
  @POST
  @Transactional
  @Path("{uuid}/review")
  @RolesAllowed({"admin", "client"})
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "Validation failed")
  })
  @Parameter(name = "product_ratings", explode = Explode.TRUE)
  public Response review(
      @PathParam("uuid") UUID uuid,
      @FormParam("rating") @NotNull @Min(0) @Max(5) Short rating,
      @FormParam("comment") String comment,
      @FormParam("product_ratings") @NotEmpty List<@Min(0) @Max(5) Short> productRatings)
      throws NotFoundException, PersistenceException {
    var order = repository.findByIdOptional(uuid, PESSIMISTIC_WRITE).orElseThrow();
    JwtUtil.checkClient(jwt, order.getClient_uuid());
    try {
      var review = reviewService.reviewOrder(order, rating, comment, productRatings);
      return Response.ok(new OrderReviewDto(review.getRating(), review.getComment())).build();
    } catch (IllegalArgumentException exc) {
      return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorMessage(exc)).build();
    }
  }

  @GET
  @Transactional
  @Path("store/{id}")
  @RolesAllowed({"admin", "manager"})
  public Response StoreOrders(@PathParam("id") Long id) throws NoSuchElementException {
    JwtUtil.checkManager(jwt, id);
    Object[] params = {id};
    var orders = repository.stream("store_id", params).map(mapper::serialize).toList();
    return Response.ok(orders).build();
  }

  /**
   * Order confirmation class.
   *
   * @since 1.0.0
   */
  @Schema
  protected record Confirmation(UUID uuid, BigDecimal cost, Long estimatedWait)
      implements Serializable {}
}
