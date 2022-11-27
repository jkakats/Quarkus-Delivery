package gr.aueb.mscis.softeng.team6.delivery.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Address implements Serializable {

  @Column (name = "street")
  private String street;

  @Column (name = "apartment")
  private String apartment;

  public String getStreet() {
    return street;
  }

  public String getApartment() {
    return apartment;
  }
}
