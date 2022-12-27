package gr.aueb.mscis.softeng.team6.delivery.resource;

import static io.restassured.RestAssured.when;
import static io.restassured.RestAssured.with;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;

import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.AddressDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.AreaDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ClientDto;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
@TestHTTPEndpoint(ClientResource.class)
class ClientResourceTest {
  private static final String TEST_USERNAME = "johndoe3";
  private static final String TEST_NAME = "Johnny Doe";
  private static final String TEST_EMAIL = "john@doe.com";
  private static final String TEST_PHONE_NUMBER = "6987654321";
  private static final String TEST_PASSWORD = "j0hnd0e!";
  private static final String TEST_STREET = "Lefkados";
  private static final String TEST_APARTMENT = "47A";

  private static UUID uuid;

  @Test
  @Order(1)
  void testList() {
    var clients =
        when().get().then().statusCode(200).extract().as(new TypeRef<List<ClientDto>>() {});
    assertThat(clients).hasSize(7).first().returns("johndoe2", ClientDto::username);
  }

  @Test
  @Order(2)
  @TestTransaction
  void testCreate() {
    var address =
        new AddressDto(TEST_STREET, TEST_APARTMENT, new AreaDto(11362, "Athina", "Attica"));
    var body =
        new ClientDto(
            null, TEST_USERNAME, TEST_PASSWORD, TEST_NAME, TEST_EMAIL, TEST_PHONE_NUMBER, address);
    var location =
        with()
            .body(body)
            .contentType(JSON)
            .when()
            .post()
            .then()
            .statusCode(201)
            .extract()
            .header("Location");
    assertThat(location).contains("/client/");
    uuid = UUID.fromString(location.split("/")[4]);
  }

  @Test
  @Order(3)
  void testRead() {
    var client = when().get("{uuid}", uuid).then().statusCode(200).extract().as(ClientDto.class);
    assertThat(client.uuid()).isEqualTo(uuid);
  }

  @Test
  @Order(4)
  @TestTransaction
  void testUpdate() {
    var address =
        new AddressDto(TEST_STREET, TEST_APARTMENT, new AreaDto(11362, "Athina", "Attica"));
    var body =
        new ClientDto(
            uuid,
            TEST_USERNAME,
            TEST_PASSWORD,
            "Johnnie Doe",
            TEST_EMAIL,
            TEST_PHONE_NUMBER,
            address);
    var client =
        with()
            .body(body)
            .contentType(JSON)
            .when()
            .put("{uuid}", uuid)
            .then()
            .statusCode(200)
            .extract()
            .as(ClientDto.class);
    assertThat(client.name()).isNotEqualTo(TEST_NAME).isEqualTo(body.name());
  }

  @Test
  @Order(5)
  @TestTransaction
  void testDelete() {
    when().delete("{uuid}", uuid).then().statusCode(204);
  }

  // TODO: add exception tests
}
