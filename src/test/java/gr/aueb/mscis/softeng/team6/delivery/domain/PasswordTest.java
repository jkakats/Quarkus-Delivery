package gr.aueb.mscis.softeng.team6.delivery.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

class PasswordTest {
  private final String TEST_PASSWORD = "testpassword";

  @Test
  void testHash() {
    var password = new Password().setPassword(TEST_PASSWORD);
    password.hash();
    assertThat(password.getPassword()).startsWith("$argon2i$");
  }

  @Test
  void testVerify() {
    var password = new Password().setPassword(TEST_PASSWORD);
    password.hash();
    var softly = new SoftAssertions();
    softly.assertThat(password.verify(TEST_PASSWORD)).isTrue();
    softly.assertThat(password.verify("otherpassword")).isFalse();
    softly.assertAll();
  }
}
