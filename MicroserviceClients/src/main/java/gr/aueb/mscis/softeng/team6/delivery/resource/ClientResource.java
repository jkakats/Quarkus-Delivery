package gr.aueb.mscis.softeng.team6.delivery.resource;

import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;

import gr.aueb.mscis.softeng.team6.delivery.domain.Client;
import gr.aueb.mscis.softeng.team6.delivery.health.ServiceState;
import gr.aueb.mscis.softeng.team6.delivery.persistence.ClientRepository;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ClientDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.mapper.ClientMapper;
import gr.aueb.mscis.softeng.team6.delivery.service.AuthenticationService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.ValidationException;
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
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
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
  @Inject protected AuthenticationService service;
  @Inject protected ClientRepository repository;
  @Inject protected ClientMapper mapper;
  @Inject protected JsonWebToken jwt;

  @Inject ServiceState serviceState;

  /** Get all the clients. */
  @GET
  @Transactional
  @Counted(name = "countListClients", description = "Count how many times list has invoked")
  @Timed(name = "timeListClients", description = "How long it takes to invoke list")
  @Retry(maxRetries = 2)
  @Operation(summary = "Gets all clients")
  public Response list() {
    if (!serviceState.isHealthyState()) {
      try {
        TimeUnit.MILLISECONDS.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
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
  // @RolesAllowed({"client", "admin", "manager"})
  @Counted(name = "countSingleClient", description = "Count how many times read has invoked")
  @Timed(name = "timeSingleClient", description = "How long it takes to invoke read")
  @Retry(maxRetries = 2)
  @Operation(
      description = "Given the uuid of a client operation return client.",
      summary = "Gets a single client")
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
  @Counted(name = "countCreateClient", description = "Count how many times create has invoked")
  @Timed(name = "timeCreateClient", description = "How long it takes to invoke create")
  @Retry(maxRetries = 2)
  @Operation(summary = "Creates a new client")
  public Response create(@Context UriInfo uriInfo, @Valid ClientDto dto)
      throws PersistenceException {
    var client = mapper.deserialize(dto);
    // NOTE: persistAndFlush doesn't work here
    client = repository.getEntityManager().merge(client);
    repository.flush();
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
  @RolesAllowed({"client"})
  @APIResponses({
    @APIResponse(responseCode = "200", description = "Updated"),
    @APIResponse(responseCode = "400", description = "Validation failed")
  })
  @Counted(name = "countUpdateClient", description = "Count how many times update has invoked")
  @Timed(name = "timeUpdateClient", description = "How long it takes to invoke update")
  @Retry(maxRetries = 2)
  @Operation(
      summary = "Updates a client",
      description =
          "User gives client's uuid and information of client which desires"
              + "to be changed and operation updates client.")
  public Response update(@PathParam("uuid") UUID uuid, @Valid ClientDto dto)
      throws NoSuchElementException, PersistenceException {
    JwtUtil.checkClient(jwt, uuid);
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
  @RolesAllowed({"client", "admin"})
  @APIResponse(responseCode = "204", description = "Deleted")
  @Counted(name = "countDeleteClient", description = "Count how many times delete has invoked")
  @Timed(name = "timeDeleteClient", description = "How long it takes to invoke delete")
  @Retry(maxRetries = 2)
  @Operation(
      summary = "Deletes a client",
      description = "User provides the uuid of a client and operation deletes him.")
  public Response delete(@PathParam("uuid") UUID uuid) throws NoSuchElementException {
    JwtUtil.checkClient(jwt, uuid);
    if (!repository.deleteById(uuid)) {
      throw new NoSuchElementException();
    }
    return Response.noContent().build();
  }

  /**
   * Authenticate a client.
   *
   * @param username the client's username
   * @param password the client's password
   */
  @POST
  @Transactional
  @Path("login")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "401", description = "Login failed")
  })
  @Counted(name = "countLoginClient", description = "Count how many times login has invoked")
  @Timed(name = "timeLoginClient", description = "How long it takes to invoke login")
  @Retry(maxRetries = 2)
  @Operation(
      summary = "Authenticate a client",
      description =
          "User provides the username and the password of a client and"
              + "operation authenticates him if username and password are correct.")
  public Response login(
      @FormParam("username") @NotNull String username,
      @FormParam("password") @NotNull String password) {
    try {
      var client = mapper.serialize(service.loginClient(username, password));
      return Response.ok(JwtUtil.clientToken(client)).build();
    } catch (ValidationException exc) {
      return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorMessage(exc)).build();
    }
  }

  /**
   * Checks if a client exists.
   *
   * @param client_uuid the client's uuid
   */
  @GET
  @Transactional
  @Path("check/{client_uuid}")
  @Counted(name = "countCheckIfClientExist", description = "Count how many times check has invoked")
  @Timed(name = "timeCheckIfClientExist", description = "How long it takes to invoke check")
  @Retry(maxRetries = 2)
  public Response check(@PathParam("client_uuid") UUID client_uuid) throws NoSuchElementException {
    if (!serviceState.isHealthyState()) {
      return Response.serverError().build();
    }
    Client client = repository.findById(client_uuid);
    if (client == null) {
      return Response.ok(false).build();
    }
    return Response.ok(true).build();
  }

  /**
   * Finds the clients with a provided zipcode
   *
   * @param zipcode a zipcode
   */
  @GET
  @Transactional
  @Path("zipcode/{zipcode}")
  @Counted(
      name = "countFindClientsWithZipcode",
      description = "Count how many times clientsFromZipcode has invoked")
  @Timed(
      name = "timeFindClientsWithZipcode",
      description = "How long it takes to invoke clientsFromZipcode")
  @Retry(maxRetries = 2)
  public Response clientsFromZipcode(@PathParam("zipcode") int zipcode) {
    System.out.println("called");
    if (!serviceState.isHealthyState()) {
      try {
        TimeUnit.MILLISECONDS.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    List<String> clientList;
    clientList = repository.findByZipcode(zipcode);
    if (clientList.isEmpty()) {
      return Response.status(Response.Status.NOT_FOUND).build();
    } else return Response.ok(clientList).build();
  }
}
