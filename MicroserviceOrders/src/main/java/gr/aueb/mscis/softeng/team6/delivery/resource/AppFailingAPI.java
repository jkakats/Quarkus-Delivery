package gr.aueb.mscis.softeng.team6.delivery.resource;

import gr.aueb.mscis.softeng.team6.delivery.health.ServiceState;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@RequestScoped
@Path("/app")
public class AppFailingAPI {

  @Inject
  ServiceState serviceState;

  @POST
  @Transactional
  @RolesAllowed({"admin"})
  @Path("/failing")
  public Response fail(){
    serviceState.setHealthyState(false);
    return Response.ok().build();
  }

  @POST
  @Transactional
  @RolesAllowed({"admin"})
  @Path("/healthy")
  public Response healthy(){
    serviceState.setHealthyState(true);
    return Response.ok().build();
  }


}
