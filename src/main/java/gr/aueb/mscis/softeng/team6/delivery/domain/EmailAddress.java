package gr.aueb.mscis.softeng.team6.delivery.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Email address entity.
 *
 * @since 0.1.0
 */
@Embeddable
public class EmailAddress implements Serializable {
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
