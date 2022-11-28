package gr.aueb.mscis.softeng.team6.delivery.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Area entity.
 *
 * @since 0.1.0
 */
@Embeddable
public class Area implements Serializable {
  /** Zip code field. */
  @Min(10000)
  @Max(89999)
  @Column(name = "zip_code")
  private Integer zipCode;

  /** City field. */
  @NotNull
  @NotBlank
  @Column(length = 100)
  private String city;

  /** State field. */
  @NotNull
  @NotBlank
  @Column(length = 100)
  private String state;

  public Integer getZipCode() {
    return zipCode;
  }

  public Area setZipCode(Integer zipCode) {
    this.zipCode = zipCode;
    return this;
  }

  /** Set the zip code by parsing a string. */
  public Area setZipCode(String zipCode) {
    return setZipCode(Integer.valueOf(zipCode.replaceAll(" ", "")));
  }

  public String getCity() {
    return city;
  }

  public Area setCity(String city) {
    this.city = city;
    return this;
  }

  public String getState() {
    return state;
  }

  public Area setState(String state) {
    this.state = state;
    return this;
  }

  @Override
  public boolean equals(Object that) {
    return this == that || (that instanceof Area other && zipCode.equals(other.zipCode));
  }

  @Override
  public int hashCode() {
    return zipCode.hashCode();
  }

  @Override
  public String toString() {
    return String.format("Area{city=\"%s\", state=\"%s\", zipCode=%d}", city, state, zipCode);
  }
}
