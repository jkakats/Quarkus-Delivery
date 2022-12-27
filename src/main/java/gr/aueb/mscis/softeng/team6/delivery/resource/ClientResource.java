package gr.aueb.mscis.softeng.team6.delivery.resource;

import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;

import gr.aueb.mscis.softeng.team6.delivery.persistence.ClientRepository;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ClientDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.mapper.ClientMapper;
import java.util.NoSuchElementException;
import java.util.UUID;
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
 * Client resource class.
 *
 * @since 1.0.0
 */
@RequestScoped
@Path("/clients")
public class ClientResource {
  @Inject protected ClientRepository repository;
  @Inject protected ClientMapper mapper;

  /** Get all the clients. */
  @GET
  @Transactional
  public Response list() {
    var clients = repository.streamAll().map(mapper::serialize).toList();
    return Response.ok(clients).build();
  }

  /**
   * Get a single client.
   *
   * @param uuid the client's UUID
   */
  @GET
  @Transactional
  @Path("{uuid}")
  public Response read(@PathParam("uuid") UUID uuid) throws NoSuchElementException {
    var client = repository.findByIdOptional(uuid).orElseThrow();
    return Response.ok(mapper.serialize(client)).build();
  }

  /**
   * Create a new client.
   *
   * @param dto a client DTO
   */
  @POST
  @Transactional
  @APIResponses({
    @APIResponse(responseCode = "201", description = "Created"),
    @APIResponse(responseCode = "400", description = "Validation failed")
  })
  public Response create(@Context UriInfo uriInfo, @Valid ClientDto dto)
      throws PersistenceException {
    var client = mapper.deserialize(dto);
    // NOTE: persistAndFlush doesn't work here
    client = repository.getEntityManager().merge(client);
    var uri = uriInfo.getRequestUriBuilder().path("{uuid}").build(client.getUuid());
    return Response.created(uri).build();
  }

  /**
   * Update an existing client.
   *
   * @param uuid the client's UUID
   * @param dto the updated client DTO
   */
  @PUT
  @Transactional
  @Path("{uuid}")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "Updated"),
    @APIResponse(responseCode = "400", description = "Validation failed")
  })
  public Response update(@PathParam("uuid") UUID uuid, @Valid ClientDto dto)
      throws NoSuchElementException, PersistenceException {
    var client = repository.findByIdOptional(uuid, PESSIMISTIC_WRITE).orElseThrow();
    mapper.update(client, dto);
    repository.persistAndFlush(client);
    return Response.ok(mapper.serialize(client)).build();
  }

  /**
   * Delete the given client.
   *
   * @param uuid the client's UUID
   */
  @DELETE
  @Transactional
  @Path("{uuid}")
  @APIResponse(responseCode = "204", description = "Deleted")
  public Response delete(@PathParam("uuid") UUID uuid) throws NoSuchElementException {
    if (!repository.deleteById(uuid)) {
      throw new NoSuchElementException();
    }
    return Response.noContent().build();
  }

  // TODO(ObserverOfTime): implement login
}
