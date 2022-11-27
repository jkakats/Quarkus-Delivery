package gr.aueb.mscis.softeng.team6.delivery.service;

import static org.assertj.core.api.Assertions.assertThat;

import gr.aueb.mscis.softeng.team6.delivery.domain.Client;
import gr.aueb.mscis.softeng.team6.delivery.util.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
class AuthenticationServiceTest {
  private static final String TEST_USERNAME = "johndoe";
  private static final String TEST_NAME = "John Doe";
  private static final String TEST_EMAIL = "john@doe.com";
  private static final String TEST_PASSWORD = "j0hnd0e!";
  private static final String TEST_PHONE_NUMBER = "6987654321";

  private static EntityManager em;
  private static ByteArrayOutputStream output;
  private static PrintStream stderr;

  private AuthenticationService service;

  @BeforeAll
  static void setUpAll() {
    stderr = System.err;
    em = EntityManagerUtil.getManager();
  }

  @BeforeEach
  void setUp() {
    service = new AuthenticationService(em);
    output = new ByteArrayOutputStream();
    System.setErr(new PrintStream(output));
  }

  @Test
  @Order(1)
  void testRegisterSuccess() {
    var client =
        service.registerClient(
            TEST_USERNAME, TEST_PASSWORD, TEST_NAME, TEST_EMAIL, TEST_PHONE_NUMBER);
    assertThat(client).isNotNull().doesNotReturn(null, Client::getId);
  }

  @Test
  @Order(2)
  void testLoginSuccess() {
    Client client = service.loginClient(TEST_USERNAME, TEST_PASSWORD);
    assertThat(client).isNotNull().returns(TEST_NAME, Client::getName);
  }

  @Test
  @Order(3)
  void testRegisterInvalidUsername() {
    var client =
        service.registerClient("", TEST_PASSWORD, TEST_NAME, TEST_EMAIL, TEST_PHONE_NUMBER);
    assertThat(client).isNull();
    assertThat(output.toString())
        .contains("username length must be between 1 and 20")
        .contains("username must match \"[a-zA-Z0-9_-]+\"");
  }

  @Test
  @Order(4)
  void testRegisterInvalidPassword() {
    var client =
        service.registerClient(TEST_USERNAME, "", TEST_NAME, TEST_EMAIL, TEST_PHONE_NUMBER);
    assertThat(client).isNull();
    assertThat(output.toString()).isEqualTo("password length must be at least 8\n");
  }

  @Test
  @Order(5)
  void testLoginWrongUsername() {
    var client = service.loginClient("username", TEST_PASSWORD);
    assertThat(client).isNull();
    assertThat(output.toString()).isEqualTo("incorrect username or password\n");
  }

  @Test
  @Order(6)
  void testLoginWrongPassword() {
    var client = service.loginClient(TEST_USERNAME, "password");
    assertThat(client).isNull();
    assertThat(output.toString()).isEqualTo("incorrect username or password\n");
  }

  @AfterEach
  void tearDown() {
    output.reset();
  }

  @AfterAll
  static void tearDownAll() {
    System.setErr(stderr);
    try {
      var tx = em.getTransaction();
      tx.begin();
      em.createQuery("delete Client").executeUpdate();
      tx.commit();
    } finally {
      em.close();
    }
  }
}
