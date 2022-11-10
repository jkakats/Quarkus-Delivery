package gr.aueb.mscis.softeng.team6.delivery.domain;

import gr.aueb.mscis.softeng.team6.delivery.validation.Phone;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Phone number entity.
 *
 * @since 0.1.0
 */
@Embeddable
public class PhoneNumber implements Serializable {
  /** Phone number field. */
  @Phone
  @NotNull
  @NotBlank
  @Column(name = "phone_number", nullable = false, length = 20)
  private String number;

  protected PhoneNumber() {}

  public PhoneNumber(String number) {
    this.number = number;
  }

  public String getNumber() {
    return number;
  }

  public PhoneNumber setNumber(String number) {
    this.number = number;
    return this;
  }

  @Override
  public String toString() {
    return number;
  }
}
