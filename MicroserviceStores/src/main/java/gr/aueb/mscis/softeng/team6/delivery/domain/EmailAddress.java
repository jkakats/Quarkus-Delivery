package gr.aueb.mscis.softeng.team6.delivery.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Email address entity.
 *
 * @since 0.1.0
 */
@Embeddable
public class EmailAddress {
  /** Email address field. */
  @Email
  @NotNull
  @NotBlank
  @Column(nullable = false, length = 320)
  private String email;

  protected EmailAddress() {}

  public EmailAddress(String email) {
    this.email = email;
  }

  public String getEmail() {
    return email;
  }

  public EmailAddress setEmail(String email) {
    this.email = email;
    return this;
  }

  @Override
  public String toString() {
    return email;
  }
}
