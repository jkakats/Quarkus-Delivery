package gr.aueb.mscis.softeng.team6.delivery.resource;

import static io.restassured.RestAssured.with;
import static org.assertj.core.api.Assertions.assertThat;


import gr.aueb.mscis.softeng.team6.delivery.domain.Client;
import gr.aueb.mscis.softeng.team6.delivery.service.OrderService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.common.mapper.TypeRef;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
public class ClientStatisticsResourceTest {

  @InjectMock
  @RestClient
  protected OrderService orderService;

  @Test
  public void testClients () {
    Mockito.when (orderService.getTopClientsOfAStoreForAPeriod((long) 4, LocalDateTime.of (2022, 3, 20, 15, 46), LocalDateTime.of (2022, 12, 28, 6, 32), 2)).
      thenReturn (List.of(UUID.fromString("4948b178-f325-4f5f-b8ea-0b4d64cd006c"), UUID.fromString("4948b178-f325-4f5f-b9ea-0b4d64cd006c"), UUID.fromString("4948b178-f325-4f5f-b9ea-0b4d64cd006c")));
    var clients =
      with()
        .pathParam("store", 4)
        .queryParam("start", LocalDateTime.of (2022, 3, 20, 15, 46))
        .queryParam("end", LocalDateTime.of (2022, 12, 28, 6, 32))
        .queryParam("max", 2)
        .when()
        .get("{store}/clients")
        .then()
        .statusCode(200) //Probably prompt 404 code because doesn't have communication with real OrderResource.
        .extract()
        .as(new TypeRef<ClientStatisticsResource.Result<List<Client>>>() {});
    assertThat(clients.result()).hasSize(2);
  }

}





















