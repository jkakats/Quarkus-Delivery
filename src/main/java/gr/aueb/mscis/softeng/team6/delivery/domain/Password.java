package gr.aueb.mscis.softeng.team6.delivery.domain;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.ValidationException;

/**
 * Password entity.
 *
 * @since 0.1.0
 */
@Embeddable
public class Password {
  /** Argon2 password hashing function. */
  private static final Argon2 ARGON2 = Argon2Factory.create();

  /** Password field. */
  @Column(nullable = false, updatable = false, length = 100)
  private String password;

  public Password() {}

  public Password(String password) {
    this.password = password;
  }

  /**
   * Hash the password before it is stored.
   *
   * @throws ValidationException if the password is too short
   * @see Argon2#hash(int, int, int, byte[])
   */
  @PrePersist
  @PreUpdate
  public void hash() throws ValidationException {
    byte[] bytes = password.getBytes();
    // HACK: manually check password length
    if (bytes.length < 8) {
      ARGON2.wipeArray(bytes);
      throw new ValidationException("password length must be at least 8");
    }
    password = ARGON2.hash(4, 1 << 16, 2, bytes);
    ARGON2.wipeArray(bytes);
  }

  /**
   * Verify that the given password matches the stored hash.
   *
   * @param password the input password.
   * @return {@code true} if the password was verified.
   * @see Argon2#verify(String, byte[])
   */
  public boolean verify(String password) {
    return ARGON2.verify(this.password, password.getBytes());
  }

  public String getPassword() {
    return password;
  }

  public Password setPassword(String password) {
    this.password = password;
    return this;
  }
}
