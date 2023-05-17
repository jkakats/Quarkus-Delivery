package gr.aueb.mscis.softeng.team6.delivery.health;


import gr.aueb.mscis.softeng.team6.delivery.persistence.OrderRepository;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class ServiceCheck implements HealthCheck {

  @Inject
  protected OrderRepository repository;


  @Override
  public HealthCheckResponse call() {
    if (isReady()){
      return HealthCheckResponse.up("Orders Service is READY");
    }
    return HealthCheckResponse.down("Orders Service is NOT READY");
  }
  public boolean isReady(){
    var orders = repository.findAll();
    if(orders.stream().toList().size()>0){
      return true;
    }else{
      return false;
    }

  }

}
