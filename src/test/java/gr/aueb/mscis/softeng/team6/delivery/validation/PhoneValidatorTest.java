package gr.aueb.mscis.softeng.team6.delivery.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PhoneValidatorTest {
  private PhoneValidator validator;

  @BeforeEach
  void setUp() {
    validator = new PhoneValidator();
    validator.initialize(null);
  }

  @Test
  void testValidateMobile() {
    assertThat(validator.isValid("6946006665", null)).isTrue();
    assertThat(validator.isValid("+30 6946006665", null)).isTrue();
    assertThat(validator.isValid("94278853", null)).isFalse();
    assertThat(validator.isValid("+357 94278853", null)).isFalse();
  }

  @Test
  void testValidateLandline() {
    assertThat(validator.isValid("2106801900", null)).isTrue();
    assertThat(validator.isValid("+30 2106801900", null)).isTrue();
    assertThat(validator.isValid("22628188", null)).isFalse();
    assertThat(validator.isValid("+357 22628188", null)).isFalse();
  }

  @Test
  void testValidateNaN() {
    assertThat(validator.isValid("", null)).isFalse();
    assertThat(validator.isValid("NaN", null)).isFalse();
  }
}
