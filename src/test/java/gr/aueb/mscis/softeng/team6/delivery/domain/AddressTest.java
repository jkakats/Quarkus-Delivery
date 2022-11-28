package gr.aueb.mscis.softeng.team6.delivery.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class AddressTest {

  Address address = new Address().setApartment("45").setStreet("Troias").setArea(new Area());

  @Test
  void testToString() {
    var area = new Area().setCity("Athina").setState("Attica").setZipCode(11362);
    var address = new Address().setStreet("Lefkados").setApartment("47A").setArea(area);
    assertThat(address)
        .hasToString(
            "Address{street=\"Lefkados\", apartment=\"47A\", "
                + "city=\"Athina\", state=\"Attica\", zipCode=11362}");
  }

  @Test
  void checkIfFieldsHaveExpectedValue() {
    assertEquals("Troias", address.getStreet());
    assertEquals("45", address.getApartment());
  }

  @Test
  void checkIfNull() {
    assertNotNull(address);
  }
}
