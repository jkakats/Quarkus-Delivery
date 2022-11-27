package gr.aueb.mscis.softeng.team6.delivery.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import gr.aueb.mscis.softeng.team6.delivery.util.EntityManagerUtil;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import java.util.Collections;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
class ClientTest {
  private static final String TEST_USERNAME = "johndoe";
  private static final String TEST_NAME = "John Doe";
  private static final String TEST_EMAIL = "john@doe.com";
  private static final String TEST_PASSWORD = "j0hnd0e!";
  private static final String TEST_PHONE_NUMBER = "6987654321";

  private Client client;

  @BeforeEach
  void setUp() {
    client =
        new Client()
            .setUsername(TEST_USERNAME)
            .setName(TEST_NAME)
            .setEmail(new EmailAddress(TEST_EMAIL))
            .setPassword(new Password(TEST_PASSWORD))
            .setPhone(new PhoneNumber(TEST_PHONE_NUMBER))
            .setOrders(Collections.emptyList());
  }

  @Test
  @Order(1)
  void testPersist() {
    EntityManagerUtil.runTransaction(
        em -> {
          em.persist(client);
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
        em -> {
          var client = em.createQuery("from Client", Client.class).getResultList().get(0);
          assertThat(client)
              .returns(TEST_USERNAME, Client::getUsername)
              .returns(TEST_NAME, Client::getName)
              .returns(TEST_EMAIL, c -> c.getEmail().toString())
              .returns(TEST_PHONE_NUMBER, c -> c.getPhone().toString());
          assertThat(client.getOrders()).isEmpty();
          em.remove(client);
        });
  }

  @Test
  void testPasswordLength() {
    EntityManagerUtil.runTransaction(
        em -> {
          assertThatExceptionOfType(ValidationException.class)
              .isThrownBy(() -> em.persist(client.setPassword(new Password("short"))))
              .withMessageContaining("length must be");
        });
  }

  @Test
  void testPhoneValid() {
    EntityManagerUtil.runTransaction(
        em -> {
          assertThatExceptionOfType(ConstraintViolationException.class)
              .isThrownBy(() -> em.persist(client.setPhone(new PhoneNumber("invalid"))))
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
