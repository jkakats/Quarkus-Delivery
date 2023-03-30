package gr.aueb.mscis.softeng.team6.delivery.serialization.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import gr.aueb.mscis.softeng.team6.delivery.domain.Client;
import gr.aueb.mscis.softeng.team6.delivery.domain.EmailAddress;
import gr.aueb.mscis.softeng.team6.delivery.domain.Password;
import gr.aueb.mscis.softeng.team6.delivery.domain.PhoneNumber;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.AddressDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.AreaDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ClientDto;
import io.quarkus.test.junit.QuarkusTest;
import java.util.UUID;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ClientMapperTest {
  private static final UUID TEST_UUID = UUID.randomUUID();
  private static final String TEST_USERNAME = "johndoe";
  private static final String TEST_NAME = "John Doe";
  private static final String TEST_EMAIL = "john@doe.com";
  private static final String TEST_PHONE_NUMBER = "6987654321";

  @Inject protected ClientMapper mapper;

  @Test
  void testDeserialize() {
    var address = new AddressDto("Lefkados", "47A", new AreaDto(11362, "Athina", "Attica"));
    var dto =
        new ClientDto(
            TEST_UUID, TEST_USERNAME, null, TEST_NAME, TEST_EMAIL, TEST_PHONE_NUMBER, address);
    assertThat(mapper.deserialize(dto))
        .returns(dto.uuid(), Client::getUuid)
        .returns(dto.email(), c -> c.getEmail().toString())
        .returns(dto.phoneNumber(), c -> c.getPhone().toString());
  }

  @Test
  void testSerialize() {
    var client =
        new Client()
            .setUuid(TEST_UUID)
            .setUsername(TEST_USERNAME)
            .setName(TEST_NAME)
            .setEmail(new EmailAddress(TEST_EMAIL))
            .setPhone(new PhoneNumber(TEST_PHONE_NUMBER))
            .setPassword(new Password())
            .setAddress(null);
    assertThat(mapper.serialize(client))
        .returns(TEST_UUID, ClientDto::uuid)
        .returns(TEST_EMAIL, ClientDto::email)
        .returns(TEST_PHONE_NUMBER, ClientDto::phoneNumber);
  }

  @Test
  void testUpdate() {
    var client = new Client().setUuid(TEST_UUID).setUsername(TEST_USERNAME);
    var dto =
        new ClientDto(
            TEST_UUID, TEST_USERNAME, null, TEST_NAME, TEST_EMAIL, TEST_PHONE_NUMBER, null);
    assertThat(client.getName()).isNull();
    mapper.update(client, dto);
    assertThat(client.getName()).isEqualTo(TEST_NAME);
  }
}
