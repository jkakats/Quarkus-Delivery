package gr.aueb.mscis.softeng.team6.delivery.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EmailAddressTest {
  private static final String TEST_EMAIL = "email@example.com";

  @Test
  void testToString() {
    var email = new EmailAddress().setEmail(TEST_EMAIL);
    assertThat(email).hasToString(email.getEmail());
  }
}
