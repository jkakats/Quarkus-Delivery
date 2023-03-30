package gr.aueb.mscis.softeng.team6.delivery.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class PhoneValidatorTest {
  private PhoneValidator validator;

  @BeforeEach
  void setUp() {
    validator = new PhoneValidator();
    validator.initialize(null);
  }

  @ParameterizedTest
  @ValueSource(strings = {"6946006665", "+30 6946006665"})
  void testValidMobile(String number) {
    assertThat(validator.isValid(number, null)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {"94278853", "+357 94278853"})
  void testInvalidMobile(String number) {
    assertThat(validator.isValid(number, null)).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"2106801900", "+30 2106801900"})
  void testValidLandline(String number) {
    assertThat(validator.isValid(number, null)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {"22628188", "+357 22628188"})
  void testInvalidLandline(String number) {
    assertThat(validator.isValid(number, null)).isFalse();
  }

  @ParameterizedTest
  @EmptySource
  @ValueSource(strings = {"NaN"})
  void testInvalidNumber(String number) {
    assertThat(validator.isValid(number, null)).isFalse();
  }
}
