package gr.aueb.mscis.softeng.team6.delivery.resource;

import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;

import gr.aueb.mscis.softeng.team6.delivery.domain.OrderProduct;
import gr.aueb.mscis.softeng.team6.delivery.persistence.OrderRepository;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ClientDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.FullOrderDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.FullOrderProductDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.OrderDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.OrderProductDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.OrderReviewDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ProductDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.mapper.OrderMapper;
import gr.aueb.mscis.softeng.team6.delivery.service.ClientService;
import gr.aueb.mscis.softeng.team6.delivery.service.OrderService;
import gr.aueb.mscis.softeng.team6.delivery.service.ProductService;
import gr.aueb.mscis.softeng.team6.delivery.service.ReviewService;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
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

  @Inject
  @RestClient
  ClientService clientService;

  @Inject
  @RestClient
  ProductService productService;

  /** Get all the orders. */
  @GET
  @Transactional
  @RolesAllowed({"admin"})
  @Counted(name="countAllOrders",description = "Count how many times AllOrders service has been invoked")
  @Timed(name="timeAllOrders",description = "How long it takes to invoke AllOrders service")
  @Operation(summary = "Gets all orders")
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
  @Path("/{uuid}")
  @RolesAllowed({"admin", "manager", "client"})
  @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 4000, successThreshold = 2)
  @Fallback(fallbackMethod = "fallbackGet")
  @Counted(name="countSingleOrder",description = "Count how many times SingleOrder service has been invoked")
  @Timed(name="timeSingleOrder",description = "How long it takes to invoke SingleOrder service")
  @Operation(summary = "Gets an order", description = "User gives the uuid of an order and operation return the corresponding order.")
  public Response read(@PathParam("uuid") UUID uuid) throws NoSuchElementException {
    var order = repository.findByIdOptional(uuid).orElseThrow();
    JwtUtil.checkManager(jwt, order.getStore_id());
    JwtUtil.checkClient(jwt, order.getClient_uuid());
    ClientDto clientdto = clientService.getClient(order.getClient_uuid());
    List<Long> prod_ids = new ArrayList();
    for(OrderProduct prod : order.getProducts()){
      prod_ids.add(prod.getProduct_id());
    }
    List<ProductDto> productsdto = productService.getProduct(prod_ids);
    OrderDto orderDto = mapper.serialize(order);
    Set<FullOrderProductDto> fullproducts = new HashSet<>();
    int i=0;
    if(productsdto.size()>0) {
      for (OrderProductDto prodDto : orderDto.products()) {
        fullproducts.add(
          new FullOrderProductDto(productsdto.get(i), prodDto.price(), prodDto.quantity(),
            prodDto.review()));
        i++;
      }
    }
    FullOrderDto full = new FullOrderDto(orderDto.uuid(),orderDto.confirmed(),orderDto.delivered(),orderDto.orderedTime(),orderDto.deliveredTime(),orderDto.estimatedWait(),orderDto.review(),clientdto,orderDto.store_id(),fullproducts);
    return Response.ok(full).build();
    //return Response.ok(mapper.serialize(order)).build();
  }

  /**
   * Create a new order.
   *
   * @param dto a order DTO
   */
  @POST
  @Transactional
  @RolesAllowed({"admin", "client"})
  @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 4000, successThreshold = 2)
  @Fallback(fallbackMethod = "fallbackPost")
  @Counted(name="countCreateOrder",description = "Count how many times CreateOrder service has been invoked")
  @Timed(name="timeCreateOrder",description = "How long it takes to invoke CreateOrder service")
  @APIResponses({
    @APIResponse(responseCode = "201", description = "Created"),
    @APIResponse(responseCode = "400", description = "Validation failed")
  })
  @Operation(summary = "Creates a new order.")
  public Response create(@Context UriInfo uriInfo, @Valid OrderDto dto)
      throws PersistenceException {
    JwtUtil.checkClient(jwt, dto.client_uuid());
    var order = mapper.deserialize(dto);
    // NOTE: persistAndFlush doesn't work here
    repository.flush();
    Boolean correctClient = clientService.getClientCheck(order.getClient_uuid());
    List<Long> prod_ids = new ArrayList();
    for(OrderProduct prod : order.getProducts()){
      prod_ids.add(prod.getProduct_id());
    }
    Boolean correctProducts = productService.getProductCheck(prod_ids);
    if(correctClient && correctProducts) {
      order = repository.getEntityManager().merge(order);
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
  @Counted(name="countUpdateOrder",description = "Count how many times UpdateOrder service has been invoked")
  @Timed(name="timeUpdateOrder",description = "How long it takes to invoke UpdateOrder service")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "Updated"),
    @APIResponse(responseCode = "400", description = "Validation failed")
  })
  @Operation(summary = "Updates an order", description = "Given the uuid of an order and its new information operation updates " +
    "order.")
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
  @Counted(name="countDeleteOrder",description = "Count how many times DeleteOrder service has been invoked")
  @Timed(name="timeDeleteOrder",description = "How long it takes to invoke DeleteOrder service")
  @APIResponse(responseCode = "204", description = "Deleted")
  @Operation(summary = "Delete an order", description = "Given a uuid of an order operation deletes it.")
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
  @Counted(name="countConfirmOrder",description = "Count how many times ConfirmOrder service has been invoked")
  @Timed(name="timeConfirmOrder",description = "How long it takes to invoke ConfirmOrder service")
  @APIResponse(responseCode = "202", description = "Accepted")
  @Operation(summary = "Confirms an order", description = "The store manager confirms the order")
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
  @Counted(name="countDeliveredOrder",description = "Count how many times DeliveredOrder service has been invoked")
  @Timed(name="timeDeliveredOrder",description = "How long it takes to invoke DeliveredOrder service")
  @APIResponse(responseCode = "202", description = "Accepted")
  @Operation(summary = "Marks the given order as delivered", description = "Once the order is delivered to the client it can be marked as delivered")
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
  @Counted(name="countReviewOrder",description = "Count how many times ReviewOrder service has been invoked")
  @Timed(name="timeReviewOrder",description = "How long it takes to invoke ReviewOrder service")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "Validation failed")
  })
  @Operation(summary = "Review the given order")
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
  @Counted(name="countStoreOrders",description = "Count how many times StoreOrders service has been invoked")
  @Timed(name="timeStoreOrders",description = "How long it takes to invoke StoreOrders service")
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

  @Counted(name="countGetOrderFallback",description = "Count how many times GetOrderFallback service has been invoked")
  @Timed(name="timeGetOrderFallback",description = "How long it takes to invoke GetOrderFallback service")
  public Response fallbackGet(@PathParam("uuid") UUID uuid){
    return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorMessage("Remote Microservice down")).build();
  }

  @Counted(name="countPostOrderFallback",description = "Count how many times PostOrderFallback service has been invoked")
  @Timed(name="timePostOrderFallback",description = "How long it takes to invoke PostOrderFallback service")
  public Response fallbackPost(@Context UriInfo uriInfo, @Valid OrderDto dto){
    return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorMessage("Remote Microservice down")).build();
  }
}
