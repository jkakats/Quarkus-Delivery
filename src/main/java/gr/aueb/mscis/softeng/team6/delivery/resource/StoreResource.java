package gr.aueb.mscis.softeng.team6.delivery.resource;

import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;

import gr.aueb.mscis.softeng.team6.delivery.persistence.StoreRepository;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.StoreDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.mapper.StoreMapper;
import java.util.NoSuchElementException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
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
  @Inject protected StoreRepository repository;
  @Inject protected StoreMapper mapper;

  /** Get all the stores. */
  @GET
  @Transactional
  public Response list() {
    var stores = repository.streamAll().map(mapper::serialize).toList();
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
    var store = repository.findByIdOptional(id).orElseThrow();
    return Response.ok(mapper.serialize(store)).build();
  }

  /**
   * Create a new store.
   *
   * @param dto a store DTO
   */
  @POST
  @Transactional
  @APIResponses({
    @APIResponse(responseCode = "201", description = "Created"),
    @APIResponse(responseCode = "400", description = "Validation failed")
  })
  public Response create(@Context UriInfo uriInfo, @Valid StoreDto dto)
      throws PersistenceException {
    var store = mapper.deserialize(dto);
    // NOTE: persistAndFlush doesn't work here
    store = repository.getEntityManager().merge(store);
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
  @APIResponses({
    @APIResponse(responseCode = "200", description = "Updated"),
    @APIResponse(responseCode = "400", description = "Validation failed")
  })
  public Response update(@PathParam("id") Long id, @Valid StoreDto dto)
      throws NoSuchElementException, PersistenceException {
    var store = repository.findByIdOptional(id, PESSIMISTIC_WRITE).orElseThrow();
    mapper.update(store, dto);
    repository.persistAndFlush(store);
    return Response.ok(mapper.serialize(store)).build();
  }

  /**
   * Delete the given store.
   *
   * @param id the store's ID
   */
  @DELETE
  @Transactional
  @Path("{id}")
  @APIResponse(responseCode = "204", description = "Deleted")
  public Response delete(@PathParam("id") Long id) throws NoSuchElementException {
    if (!repository.deleteById(id)) {
      throw new NoSuchElementException();
    }
    return Response.noContent().build();
  }
}
