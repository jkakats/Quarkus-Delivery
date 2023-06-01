package gr.aueb.mscis.softeng.team6.delivery.health;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

@Liveness
@ApplicationScoped
public class ServiceHealth implements HealthCheck {

  @Inject protected ServiceState serviceState;

  @Override
  public HealthCheckResponse call() {
    if (serviceState.isHealthyState())
      return HealthCheckResponse.up("Client microservice is healthy");
    else return HealthCheckResponse.down("Client microservice is healthy");
  }
}
