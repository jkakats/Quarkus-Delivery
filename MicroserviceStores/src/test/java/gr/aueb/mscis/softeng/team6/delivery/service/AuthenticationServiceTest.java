package gr.aueb.mscis.softeng.team6.delivery.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import gr.aueb.mscis.softeng.team6.delivery.domain.Client;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AuthenticationServiceTest {
  private static final String TEST_USERNAME = "johndoe";
  private static final String TEST_NAME = "John Doe";
  private static final String TEST_EMAIL = "john@doe.com";
  private static final String TEST_PASSWORD = "j0hnd0e!";
  private static final String TEST_PHONE_NUMBER = "6987654321";

  @Inject protected AuthenticationService service;

  @Test
  @TestTransaction
  void testRegisterSuccess() {
    var client =
        service.registerClient(
            TEST_USERNAME, TEST_PASSWORD, TEST_NAME, TEST_EMAIL, TEST_PHONE_NUMBER);
    assertThat(client)
        .doesNotReturn(null, Client::getUuid)
        .returns(TEST_USERNAME, Client::getUsername)
        .returns(true, c -> c.getPassword().verify(TEST_PASSWORD));
  }

  @Test
  void testLoginSuccess() {
    var client = service.loginClient("johndoe2", TEST_PASSWORD);
    assertThat(client)
        .returns(TEST_NAME, Client::getName)
        .returns(TEST_EMAIL, c -> c.getEmail().toString())
        .returns(TEST_PHONE_NUMBER, c -> c.getPhone().toString());
  }

  @Test
  void testRegisterInvalidUsername() {
    assertThatExceptionOfType(ConstraintViolationException.class)
        .isThrownBy(
            () -> {
              service.registerClient("", TEST_PASSWORD, TEST_NAME, TEST_EMAIL, TEST_PHONE_NUMBER);
            })
        .withMessageContaining("length must be between 1 and 20");
  }

  @Test
  void testRegisterInvalidPassword() {
    assertThatExceptionOfType(ValidationException.class)
        .isThrownBy(
            () -> {
              service.registerClient(TEST_USERNAME, "", TEST_NAME, TEST_EMAIL, TEST_PHONE_NUMBER);
            })
        .withMessage("password length must be at least 8");
  }

  @Test
  void testLoginWrongUsername() {
    assertThatExceptionOfType(ValidationException.class)
        .isThrownBy(
            () -> {
              service.loginClient("username", TEST_PASSWORD);
            })
        .withMessage("incorrect username or password");
  }

  @Test
  void testLoginWrongPassword() {
    assertThatExceptionOfType(ValidationException.class)
        .isThrownBy(
            () -> {
              service.loginClient(TEST_USERNAME, "password");
            })
        .withMessage("incorrect username or password");
  }
}
