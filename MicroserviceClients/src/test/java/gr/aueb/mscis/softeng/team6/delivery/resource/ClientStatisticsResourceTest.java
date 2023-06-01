package gr.aueb.mscis.softeng.team6.delivery.resource;

import static io.restassured.RestAssured.with;
import static org.assertj.core.api.Assertions.assertThat;

import gr.aueb.mscis.softeng.team6.delivery.serialization.LocalDateTimeConverter;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ClientDto;
import gr.aueb.mscis.softeng.team6.delivery.service.OrderService;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
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
@TestHTTPEndpoint(ClientStatisticsResource.class)
public class ClientStatisticsResourceTest {

  private static ParamConverter<LocalDateTime> converter;

  private static final long TEST_ID = 4L;
  private static final int MAX = 2;

  @InjectMock @RestClient protected OrderService orderService;

  @BeforeAll
  static void setUp() {
    // NOTE: quarkus-resteasy doesn't handle this automatically in tests
    //  (see https://github.com/quarkusio/quarkus/issues/24715)
    converter = new LocalDateTimeConverter().getConverter(LocalDateTime.class, null, null);
  }

  @BeforeEach
  public void setup2() {
    Mockito.when(
            orderService.getTopClientsOfAStoreForAPeriod(
                4L, LocalDateTime.of(2022, 12, 7, 0, 0), LocalDateTime.of(2022, 12, 11, 0, 0), 3))
        .thenReturn(
            List.of(
                UUID.fromString("4948b178-f325-4f5f-b8ea-0b4d60cd006c"),
                UUID.fromString("4948b178-f325-4f5f-b8ea-0b4d60cd006c"),
                UUID.fromString("4948b178-f325-4f5f-b8ea-0b4d60cd006c")));
  }

  @Test
  public void testClientsStat() {
    var start = converter.toString(LocalDateTime.of(2022, 12, 7, 0, 0));
    var end = converter.toString(LocalDateTime.of(2022, 12, 11, 0, 0));
    var clients =
        with()
            .pathParam("store", TEST_ID)
            .queryParam("start", start)
            .queryParam("end", end)
            .queryParam("max", 3)
            .when()
            .get("clients")
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<ClientStatisticsResource.Result<List<ClientDto>>>() {});
    assertThat(clients.result()).hasSize(3);
  }
}
