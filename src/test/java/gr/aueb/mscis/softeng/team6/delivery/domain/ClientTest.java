package gr.aueb.mscis.softeng.team6.delivery.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientTest {
  private static final String TEST_USERNAME = "johndoe";
  private static final String TEST_NAME = "John Doe";
  private static final String TEST_EMAIL = "john@doe.com";
  private static final String TEST_PASSWORD = "j0hnd0e!";
  private static final String TEST_PHONE_NUMBER = "6987654321";
  private static final String TEST_STREET = "Lefkados";
  private static final String TEST_APARTMENT = "47A";

  private Client client;

  @BeforeEach
  void setUp() {
    var area = new Area().setCity("Athina").setState("Attica").setZipCode(11362);
    var address = new Address().setStreet(TEST_STREET).setApartment(TEST_APARTMENT).setArea(area);
    client =
        new Client()
            .setUsername(TEST_USERNAME)
            .setName(TEST_NAME)
            .setAddress(address)
            .setEmail(new EmailAddress(TEST_EMAIL))
            .setPassword(new Password(TEST_PASSWORD))
            .setPhone(new PhoneNumber(TEST_PHONE_NUMBER));
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
