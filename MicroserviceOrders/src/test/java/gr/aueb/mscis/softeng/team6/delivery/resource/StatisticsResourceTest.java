package gr.aueb.mscis.softeng.team6.delivery.resource;

import static io.restassured.RestAssured.with;
import static org.assertj.core.api.Assertions.assertThat;

import gr.aueb.mscis.softeng.team6.delivery.serialization.LocalDateTimeConverter;
import gr.aueb.mscis.softeng.team6.delivery.service.ClientService;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import io.restassured.common.mapper.TypeRef;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.ext.ParamConverter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
@TestHTTPEndpoint(StatisticsResource.class)
class StatisticsResourceTest {
  private static final long TEST_ID = 4L;
  private static final int TEST_ZIP_CODE = 10434;

  private static ParamConverter<LocalDateTime> converter;

  @InjectMock @RestClient static ClientService clientService;

  @BeforeAll
  static void setUp() {
    // NOTE: quarkus-resteasy doesn't handle this automatically in tests
    //  (see https://github.com/quarkusio/quarkus/issues/24715)
    converter = new LocalDateTimeConverter().getConverter(LocalDateTime.class, null, null);
  }

  @BeforeEach
  public void setup2() {
    Mockito.when(clientService.getClientIds(10434))
        .thenReturn(
            List.of(
                "4948b178-f325-4f5f-b8ea-0b4d64cd006c", "4948b178-f325-4f5f-b8ea-0b4d64cd006e"));
  }

  @Test
  @TestSecurity(
      user = "root",
      roles = {"admin"})
  void testClients() {
    var start = converter.toString(LocalDateTime.of(2022, 12, 7, 0, 0));
    var end = converter.toString(LocalDateTime.of(2022, 12, 11, 0, 0));
    var clients =
        with()
            .pathParam("store", TEST_ID)
            .queryParam("start", start)
            .queryParam("end", end)
            .when()
            .get("topClientUUIDs")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList(".", UUID.class);
    assertThat(clients).hasSizeBetween(1, 10);
  }

  @Test
  @TestSecurity(
      user = "root",
      roles = {"admin"})
  void testDelivery() {
    var average =
        with()
            .pathParam("store", TEST_ID)
            .queryParam("zip_code", TEST_ZIP_CODE)
            .when()
            .get("delivery")
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<StatisticsResource.Result<Long>>() {});
    assertThat(average.result()).isEqualTo(22L);
  }

  /*@Test
  @TestSecurity(
      user = "root",
      roles = {"admin"})
  void testRush() {
    // NOTE: setting the time zone here is necessary
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Athens"));
    var date = converter.toString(LocalDateTime.of(2022, 12, 7, 0, 0));
    var hours =
        with()
            .pathParam("store", TEST_ID)
            .queryParam("week", date)
            .queryParam("limit", 1)
            .when()
            .get("rush")
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<StatisticsResource.Result<List<Integer>>>() {});
    assertThat(hours.result()).singleElement().isEqualTo(12);
  }*/
}
