package gr.aueb.mscis.softeng.team6.delivery.resource;

import static io.restassured.RestAssured.when;
import static io.restassured.RestAssured.with;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;

import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ClientDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.OrderDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.OrderProductDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.OrderReviewDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ProductDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.StoreDto;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.common.mapper.TypeRef;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestHTTPEndpoint(OrderResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderResourceTest {
  private static final int TEST_QUANTITY = 2;
  private static final long TEST_WAIT = 15L;
  private static final short TEST_RATING = 3;
  private static final String TEST_COMMENT = "nice";
  private static final BigDecimal TEST_COST = new BigDecimal("6.50");
  private static final UUID TEST_CLIENT_UUID =
      UUID.fromString("4948b178-f325-4f5f-b8ea-0b4d64cd006c");

  private static UUID uuid = null;

  @Test
  @Order(1)
  @TestSecurity(
      user = "root",
      roles = {"admin"})
  void testList() {
    var orders = when().get().then().statusCode(200).extract().as(new TypeRef<List<OrderDto>>() {});
    assertThat(orders).hasSize(5).first().returns("eudim", o -> o.client().username());
  }

  @Test
  @Order(2)
  @TestTransaction
  void testCreate() {
    var client =
        new ClientDto(
            TEST_CLIENT_UUID, "jonhndoe2", null, "John Doe", "john@doe.com", "6987654321", null);
    var store = new StoreDto(2L, "Domino's Pizza", "Pizza", Set.of(), List.of());
    var product = new ProductDto(11L, "Pizza Margarita", TEST_COST, null, store);
    var orderProducts = Set.of(new OrderProductDto(product, TEST_QUANTITY, null));
    var body =
        new OrderDto(null, false, false, null, null, null, null, client, store, orderProducts);
    var token = JwtUtil.clientToken(client).token();
    var location =
        with()
            .body(body)
            .header("Authorization", "Bearer " + token)
            .contentType(JSON)
            .when()
            .post()
            .then()
            .statusCode(201)
            .extract()
            .header("Location");
    assertThat(location).contains("/orders/");
    uuid = UUID.fromString(location.split("/")[4]);
  }

  @Test
  @Order(3)
  @TestSecurity(
      user = "root",
      roles = {"admin"})
  void testRead() {
    var order = when().get("{uuid}", uuid).then().statusCode(200).extract().as(OrderDto.class);
    assertThat(order)
        .returns(uuid, OrderDto::uuid)
        .returns(TEST_CLIENT_UUID, o -> o.client().uuid());
  }

  @Test
  @Order(4)
  @TestTransaction
  @TestSecurity(
      user = "root",
      roles = {"admin"})
  void testUpdate() {
    var client =
        new ClientDto(
            TEST_CLIENT_UUID,
            "jonhndoe2",
            "j0hnd0e!",
            "John Doe",
            "john@doe.com",
            "6987654321",
            null);
    var store = new StoreDto(2L, "Domino's Pizza", "Pizza", Set.of(), List.of());
    var date = LocalDateTime.now().minusMinutes(15L);
    var body = new OrderDto(uuid, false, false, date, null, null, null, client, store, null);
    var order =
        with()
            .body(body)
            .contentType(JSON)
            .when()
            .put("{uuid}", uuid)
            .then()
            .statusCode(200)
            .extract()
            .as(OrderDto.class);
    assertThat(order.orderedTime()).isEqualTo(date);
  }

  @Test
  @Order(5)
  @TestTransaction
  @TestSecurity(
      user = "root",
      roles = {"admin"})
  void testConfirm() {
    var confirm =
        with()
            .formParam("estimated_wait", TEST_WAIT)
            .when()
            .post("{uuid}/confirm", uuid)
            .then()
            .statusCode(202)
            .extract()
            .as(OrderResource.Confirmation.class);
    var cost = TEST_COST.multiply(new BigDecimal(TEST_QUANTITY));
    assertThat(confirm)
        .returns(uuid, OrderResource.Confirmation::uuid)
        .returns(cost, OrderResource.Confirmation::cost)
        .returns(TEST_WAIT, OrderResource.Confirmation::estimatedWait);
  }

  @Test
  @Order(6)
  @TestTransaction
  @TestSecurity(
      user = "root",
      roles = {"admin"})
  void testDeliver() {
    when().post("{uuid}/deliver", uuid).then().statusCode(202);
  }

  @Test
  @Order(7)
  @TestTransaction
  void testReview() {
    var client =
        new ClientDto(
            TEST_CLIENT_UUID, "jonhndoe2", null, "John Doe", "john@doe.com", "6987654321", null);
    var token = JwtUtil.clientToken(client).token();
    var review =
        with()
            .formParam("rating", TEST_RATING)
            .formParam("comment", TEST_COMMENT)
            .formParam("product_ratings", List.of(TEST_RATING))
            .header("Authorization", "Bearer " + token)
            .when()
            .post("{uuid}/review", uuid)
            .then()
            .statusCode(200)
            .extract()
            .as(OrderReviewDto.class);
    assertThat(review)
        .returns(TEST_RATING, OrderReviewDto::rating)
        .returns(TEST_COMMENT, OrderReviewDto::comment);
  }

  @Test
  @Order(8)
  @TestTransaction
  @TestSecurity(
      user = "root",
      roles = {"admin"})
  void testReviewInvalid() {
    var error =
        with()
            .formParam("rating", TEST_RATING)
            .formParam("comment", TEST_COMMENT)
            .formParam("product_ratings", List.of(1, 2))
            .when()
            .post("{uuid}/review", uuid)
            .then()
            .statusCode(400)
            .extract()
            .as(ErrorMessage.class);
    assertThat(error.error()).isEqualTo("Order products and ratings must have the same size (1)");
  }

  @Test
  @Order(9)
  @TestTransaction
  @TestSecurity(
      user = "root",
      roles = {"admin"})
  void testDelete() {
    when().delete("{uuid}", uuid).then().statusCode(204);
  }
}
