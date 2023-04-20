package gr.aueb.mscis.softeng.team6.delivery.service;

import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ClientDto;
import java.util.List;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@ApplicationScoped
@Path("/clients")
@RegisterRestClient
public interface ClientService {

  @GET
  @Path("/{zipcode}")
  List<String> getClientIds(@PathParam("zipcode") int zipcode);

  @GET
  @Path("/{client_uuid}")
  ClientDto getClient(@PathParam("client_uuid") UUID client_uuid);

  @GET
  @Path("/check/{client_uuid}")
  Boolean getClientCheck(@PathParam("client_uuid") UUID client_uuid);

}
