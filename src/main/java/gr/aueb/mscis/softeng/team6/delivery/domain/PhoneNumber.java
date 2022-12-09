package gr.aueb.mscis.softeng.team6.delivery.domain;

import gr.aueb.mscis.softeng.team6.delivery.validation.Phone;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Phone number entity.
 *
 * @since 0.1.0
 */
@Embeddable
public class PhoneNumber {
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
