package gr.aueb.mscis.softeng.team6.delivery.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.Valid;
import java.io.Serializable;

/**
 * Address entity.
 *
 * @since 0.1.0
 */
@Embeddable
public class Address implements Serializable {
  /** Street field. */
  @Column(name = "address_street", length = 100)
  private String street;

  /** Apartment field. */
  @Column(name = "address_apartment", length = 100)
  private String apartment;

  /** Area field. */
  @Embedded
  @Valid
  @AttributeOverrides({
    @AttributeOverride(name = "zipCode", column = @Column(name = "address_zip_code")),
    @AttributeOverride(name = "city", column = @Column(name = "address_city")),
    @AttributeOverride(name = "state", column = @Column(name = "address_state")),
    @AttributeOverride(name = "street", column = @Column(name = "address_street")),
  })
  private Area area;

  public String getStreet() {
    return street;
  }

  public Address setStreet(String street) {
    this.street = street;
    return this;
  }

  public String getApartment() {
    return apartment;
  }

  public Address setApartment(String apartment) {
    this.apartment = apartment;
    return this;
  }

  public Area getArea() {
    return area;
  }

  public Address setArea(Area area) {
    this.area = area;
    return this;
  }

  @Override
  public String toString() {
    return String.format(
        "Address{street=\"%s\", apartment=\"%s\", city=\"%s\", state=\"%s\", zipCode=\"%d\"}",
        street, apartment, area.getCity(), area.getState(), area.getZipCode());
  }
}
