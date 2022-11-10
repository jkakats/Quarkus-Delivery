package gr.aueb.mscis.softeng.team6.delivery.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PhoneNumberTest {
  private static final String TEST_NUMBER = "6987654321";

  @Test
  void testToString() {
    var number = new PhoneNumber().setNumber(TEST_NUMBER);
    assertThat(number).hasToString(number.getNumber());
  }
}
