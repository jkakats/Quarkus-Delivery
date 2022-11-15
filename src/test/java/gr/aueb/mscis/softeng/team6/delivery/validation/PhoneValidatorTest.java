package gr.aueb.mscis.softeng.team6.delivery.validation;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class PhoneValidatorTest {
  @InjectSoftAssertions private SoftAssertions softly;

  private PhoneValidator validator;

  @BeforeEach
  void setUp() {
    validator = new PhoneValidator();
    validator.initialize(null);
  }

  @Test
  void testValidateMobile() {
    softly.assertThat(validator.isValid("6946006665", null)).isTrue();
    softly.assertThat(validator.isValid("+30 6946006665", null)).isTrue();
    softly.assertThat(validator.isValid("94278853", null)).isFalse();
    softly.assertThat(validator.isValid("+357 94278853", null)).isFalse();
  }

  @Test
  void testValidateLandline() {
    softly.assertThat(validator.isValid("2106801900", null)).isTrue();
    softly.assertThat(validator.isValid("+30 2106801900", null)).isTrue();
    softly.assertThat(validator.isValid("22628188", null)).isFalse();
    softly.assertThat(validator.isValid("+357 22628188", null)).isFalse();
  }

  @Test
  void testValidateNaN() {
    softly.assertThat(validator.isValid("", null)).isFalse();
    softly.assertThat(validator.isValid("NaN", null)).isFalse();
  }
}
