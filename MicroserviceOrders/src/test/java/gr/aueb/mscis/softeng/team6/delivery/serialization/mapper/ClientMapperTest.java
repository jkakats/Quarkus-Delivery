package gr.aueb.mscis.softeng.team6.delivery.serialization.mapper;

import static org.assertj.core.api.Assertions.assertThat;


import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.AddressDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.AreaDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ClientDto;
import io.quarkus.test.junit.QuarkusTest;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ClientMapperTest {
  private static final UUID TEST_UUID = UUID.randomUUID();
  private static final String TEST_USERNAME = "johndoe";
  private static final String TEST_NAME = "John Doe";
  private static final String TEST_EMAIL = "john@doe.com";
  private static final String TEST_PHONE_NUMBER = "6987654321";


  @Test
  void testDeserialize() {
    var address = new AddressDto("Lefkados", "47A", new AreaDto(11362, "Athina", "Attica"));
    var dto =
        new ClientDto(
            TEST_UUID, TEST_USERNAME, null, TEST_NAME, TEST_EMAIL, TEST_PHONE_NUMBER, address);
    Assertions.assertEquals(dto.uuid(), TEST_UUID);
    Assertions.assertEquals(dto.email(), TEST_EMAIL);
    Assertions.assertEquals(dto.phoneNumber(), TEST_PHONE_NUMBER);
  }

}
