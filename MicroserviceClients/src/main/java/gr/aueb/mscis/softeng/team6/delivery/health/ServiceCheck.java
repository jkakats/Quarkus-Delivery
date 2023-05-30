package gr.aueb.mscis.softeng.team6.delivery.health;

import gr.aueb.mscis.softeng.team6.delivery.persistence.ClientRepository;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class ServiceCheck implements HealthCheck {

  @Inject
  protected ClientRepository repository;

  @Override
  public HealthCheckResponse call () {
    if (isOK()) return HealthCheckResponse.up("Client microservice is ready to answer requests");
    else return HealthCheckResponse.down("Client microservice is not ready to answer requests");
  }

  private boolean isOK () {
    var allClients = repository.findAll();
    if (allClients.list().size() > 0) return true;
    else return false;
  }

}
