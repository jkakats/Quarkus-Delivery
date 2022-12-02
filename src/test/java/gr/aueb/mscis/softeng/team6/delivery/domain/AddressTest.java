package gr.aueb.mscis.softeng.team6.delivery.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AddressTest {
  @Test
  void testToString() {
    var area = new Area().setCity("Athina").setState("Attica").setZipCode(11362);
    var address = new Address().setStreet("Lefkados").setApartment("47A").setArea(area);
    assertThat(address)
        .hasToString(
            "Address{street=\"Lefkados\", apartment=\"47A\", "
                + "city=\"Athina\", state=\"Attica\", zipCode=11362}");
  }
}
