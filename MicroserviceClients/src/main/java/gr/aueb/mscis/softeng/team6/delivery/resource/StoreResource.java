package gr.aueb.mscis.softeng.team6.delivery.resource;

import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;

import gr.aueb.mscis.softeng.team6.delivery.domain.Area;
import gr.aueb.mscis.softeng.team6.delivery.persistence.OrderRepository;
import gr.aueb.mscis.softeng.team6.delivery.persistence.StoreRepository;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.StoreDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.mapper.OrderMapper;
import gr.aueb.mscis.softeng.team6.delivery.serialization.mapper.StoreMapper;
import java.util.List;
import java.util.NoSuchElementException;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.enums.Explode;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

/**
 * Store resource class.
 *
 * @since 1.0.0
 */
@RequestScoped
@Path("/stores")
public class StoreResource {
  @Inject protected StoreRepository storeRepository;
  @Inject protected OrderRepository orderRepository;
  @Inject protected StoreMapper storeMapper;
  @Inject protected OrderMapper orderMapper;
  @Inject protected JsonWebToken jwt;

  /** Get all the stores. */
  @GET
  @Transactional
  public Response list() {
    var stores = storeRepository.streamAll().map(storeMapper::serialize).toList();
    return Response.ok(stores).build();
  }

  /**
   * Get a single store.
   *
   * @param id the store's ID
   */
  @GET
  @Transactional
  @Path("{id}")
  public Response read(@PathParam("id") Long id) throws NoSuchElementException {
    var store = storeRepository.findByIdOptional(id).orElseThrow();
    return Response.ok(storeMapper.serialize(store)).build();
  }

  /**
   * Create a new store.
   *
   * @param dto a store DTO
   */
  @POST
  @Transactional
  @RolesAllowed({"admin", "manager"})
  @APIResponses({
    @APIResponse(responseCode = "201", description = "Created"),
    @APIResponse(responseCode = "400", description = "Validation failed")
  })
  public Response create(@Context UriInfo uriInfo, @Valid StoreDto dto)
      throws PersistenceException {
    var store = storeMapper.deserialize(dto);
    // NOTE: persistAndFlush doesn't work here
    store = storeRepository.getEntityManager().merge(store);
    storeRepository.flush();
    var uri = uriInfo.getRequestUriBuilder().path("{uuid}").build(store.getId());
    return Response.created(uri).build();
  }

  /**
   * Update an existing store.
   *
   * @param id the store's ID
   * @param dto the updated store DTO
   */
  @PUT
  @Transactional
  @Path("{id}")
  @RolesAllowed({"admin", "manager"})
  @APIResponses({
    @APIResponse(responseCode = "200", description = "Updated"),
    @APIResponse(responseCode = "400", description = "Validation failed")
  })
  public Response update(@PathParam("id") Long id, @Valid StoreDto dto)
      throws NoSuchElementException, PersistenceException {
    JwtUtil.checkManager(jwt, id);
    var store = storeRepository.findByIdOptional(id, PESSIMISTIC_WRITE).orElseThrow();
    storeMapper.update(store, dto);
    storeRepository.persistAndFlush(store);
    return Response.ok(storeMapper.serialize(store)).build();
  }

  /**
   * Delete the given store.
   *
   * @param id the store's ID
   */
  @DELETE
  @Transactional
  @Path("{id}")
  @RolesAllowed({"admin", "manager"})
  @APIResponse(responseCode = "204", description = "Deleted")
  public Response delete(@PathParam("id") Long id) throws NoSuchElementException {
    JwtUtil.checkManager(jwt, id);
    if (!storeRepository.deleteById(id)) {
      throw new NoSuchElementException();
    }
    return Response.noContent().build();
  }

  /**
   * Search for stores near the given area.
   *
   * @param zipCode the area's zip code
   * @param products a list of products
   */
  @POST
  @Transactional
  @Path("search")
  @Parameter(name = "products", explode = Explode.TRUE)
  public Response search(
      @FormParam("zip_code") @NotNull Integer zipCode,
      @FormParam("products") @NotEmpty List<String> products) {
    var stores = storeRepository.findByArea(new Area().setZipCode(zipCode), products);
    return Response.ok(storeMapper.serialize(stores)).build();
  }

  /**
   * List the store's orders.
   *
   * @param id the store's ID
   */
  @GET
  @Transactional
  @Path("{id}/orders")
  @RolesAllowed({"admin", "manager"})
  public Response orders(@PathParam("id") Long id) throws NoSuchElementException {
    JwtUtil.checkManager(jwt, id);
    Object[] params = {id};
    var orders = orderRepository.stream("store.id", params).map(orderMapper::serialize).toList();
    return Response.ok(orders).build();
  }
}
