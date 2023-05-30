package gr.aueb.mscis.softeng.team6.delivery.health;

import gr.aueb.mscis.softeng.team6.delivery.persistence.ProductRepository;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class ServiceCheck implements HealthCheck {

  @Inject protected ProductRepository repository;

  @Override
  public HealthCheckResponse call() {
    if (isReady()) {
      return HealthCheckResponse.up("Products Service is READY");
    }
    return HealthCheckResponse.down("Products Service is NOT READY");
  }

  public boolean isReady() {
    var products = repository.findAll();
    if (products.stream().toList().size() > 0) {
      return true;
    } else {
      return false;
    }
  }
}
