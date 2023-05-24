package gr.aueb.mscis.softeng.team6.delivery.health;

import javax.inject.Singleton;

@Singleton
public class ServiceState {

  private boolean healthyState=true;

  public boolean isHealthyState() {
    return healthyState;
  }

  public void setHealthyState(boolean healthyState) {
    this.healthyState = healthyState;
  }
}
