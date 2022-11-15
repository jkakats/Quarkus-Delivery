package gr.aueb.mscis.softeng.team6.delivery.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import gr.aueb.mscis.softeng.team6.delivery.util.EntityManagerUtil;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
class ClientTest {
  public static final String TEST_USERNAME = "johndoe";
  public static final String TEST_NAME = "John Doe";
  public static final String TEST_EMAIL = "john@doe.com";
  public static final String TEST_PASSWORD = "j0hnd0e!";
  public static final String TEST_PHONE_NUMBER = "6987654321";

  private Client client;

  @BeforeEach
  void setUp() {
    client =
        new Client()
            .setUsername(TEST_USERNAME)
            .setName(TEST_NAME)
            .setEmail(new EmailAddress(TEST_EMAIL))
            .setPassword(new Password(TEST_PASSWORD))
            .setPhone(new PhoneNumber(TEST_PHONE_NUMBER));
  }

  @Test
  @Order(1)
  void testPersist() {
    EntityManagerUtil.runTransaction(
        tx -> {
          tx.persist(client);
          var softly = new SoftAssertions();
          softly.assertThat(client.getId()).isNotNull();
          softly.assertThat(client.getPassword().getPassword()).isNotEqualTo(TEST_PASSWORD);
          softly.assertThat(client.getPassword().verify(TEST_PASSWORD)).isTrue();
          softly.assertAll();
        });
  }

  @Test
  @Order(2)
  void testQuery() {
    EntityManagerUtil.runTransaction(
        tx -> {
          var client = tx.createQuery("from Client", Client.class).getResultList().get(0);
          var softly = new SoftAssertions();
          softly.assertThat(client.getUsername()).isEqualTo(TEST_USERNAME);
          softly.assertThat(client.getName()).isEqualTo(TEST_NAME);
          softly.assertThat(client.getEmail().toString()).isEqualTo(TEST_EMAIL);
          softly.assertThat(client.getPhone().toString()).isEqualTo(TEST_PHONE_NUMBER);
          softly.assertAll();
          tx.createQuery("delete Client").executeUpdate();
        });
  }

  @Test
  void testUsernamePattern() {
    EntityManagerUtil.runTransaction(
        tx -> {
          assertThatExceptionOfType(ConstraintViolationException.class)
              .isThrownBy(() -> tx.persist(client.setUsername("???")))
              .withMessageContaining("must match");
        });
  }

  @Test
  void testUsernameLength() {
    EntityManagerUtil.runTransaction(
        tx -> {
          assertThatExceptionOfType(ConstraintViolationException.class)
              .isThrownBy(() -> tx.persist(client.setUsername("thisusernameiswaytoolong")))
              .withMessageContaining("length must be");
        });
  }

  @Test
  void testNameNotBlank() {
    EntityManagerUtil.runTransaction(
        tx -> {
          assertThatExceptionOfType(ConstraintViolationException.class)
              .isThrownBy(() -> tx.persist(client.setName("")))
              .withMessageContaining("must not be blank");
        });
  }

  @Test
  void testPasswordLength() {
    EntityManagerUtil.runTransaction(
        tx -> {
          assertThatExceptionOfType(ConstraintViolationException.class)
              .isThrownBy(() -> tx.persist(client.setPassword(new Password("short"))))
              .withMessageContaining("length must be");
        });
  }

  @Test
  void testEmailValid() {
    EntityManagerUtil.runTransaction(
        tx -> {
          assertThatExceptionOfType(ConstraintViolationException.class)
              .isThrownBy(() -> tx.persist(client.setEmail(new EmailAddress("invalid"))))
              .withMessageContaining("must be a well-formed email address");
        });
  }

  @Test
  void testPhoneValid() {
    EntityManagerUtil.runTransaction(
        tx -> {
          assertThatExceptionOfType(ConstraintViolationException.class)
              .isThrownBy(() -> tx.persist(client.setPhone(new PhoneNumber("invalid"))))
              .withMessageContaining("must be a valid phone number");
        });
  }

  @Test
  void testEquals() {
    var client2 = new Client().setUsername(TEST_USERNAME);
    assertThat(client).hasSameHashCodeAs(client2);
    client2.setUsername("other_username");
    assertThat(client).isNotEqualTo(client2);
  }

  @Test
  void testToString() {
    assertThat(client)
        .hasToString(
            "Client{username=\"johndoe\", name=\"John Doe\", "
                + "email=\"john@doe.com\", phone=\"6987654321\"}");
  }
}
