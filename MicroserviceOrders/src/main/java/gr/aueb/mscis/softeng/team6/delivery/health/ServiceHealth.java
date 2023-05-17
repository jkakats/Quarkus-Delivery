package gr.aueb.mscis.softeng.team6.delivery.health;


import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;


@Liveness
@ApplicationScoped
public class ServiceHealth implements HealthCheck {

  @Override
  public HealthCheckResponse call(){
    if (isHealthy()){
      return HealthCheckResponse.up("Orders Service is HEALTHY");
    }
    return HealthCheckResponse.down("Orders Service is FAILING");
  }

  public boolean isHealthy(){
    return true;
  }
}
