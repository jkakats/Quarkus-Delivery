package gr.aueb.mscis.softeng.team6.delivery.resource;

import static io.restassured.RestAssured.when;
import static io.restassured.RestAssured.with;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;

import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.AreaDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ProductDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.StoreDto;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.common.mapper.TypeRef;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestHTTPEndpoint(StoreResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StoreResourceTest {
  private static final String TEST_NAME = "Krithamos";
  private static final String TEST_TYPE = "restaurant";
  private static final String TEST_PRODUCT_NAME = "Vegan Burger";
  private static final int TEST_ZIP_CODE = 15451;

  private Long id = null;

  @Test
  @Order(1)
  @TestSecurity(
      user = "root",
      roles = {"admin"})
  void testList() {
    var stores = when().get().then().statusCode(200).extract().as(new TypeRef<List<StoreDto>>() {});
    assertThat(stores).hasSize(7).first().returns("Pizza Fan", StoreDto::name);
  }

  @Test
  @Order(2)
  @TestTransaction
  @TestSecurity(
      user = "root",
      roles = {"admin"})
  void testCreate() {
    var areas = Set.of(new AreaDto(TEST_ZIP_CODE, "Athina", "Attica"));
    var body = new StoreDto(null, TEST_NAME, TEST_TYPE, areas, List.of());
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
    assertThat(location).contains("/stores/");
    id = Long.valueOf(location.split("/")[4]);
  }

  @Test
  @Order(3)
  void testRead() {
    var store = when().get("{id}", id).then().statusCode(200).extract().as(StoreDto.class);
    assertThat(store).returns(id, StoreDto::id).returns(TEST_NAME, StoreDto::name);
  }

  @Test
  @Order(4)
  @TestTransaction
  void testUpdate() {
    var areas = Set.of(new AreaDto(TEST_ZIP_CODE, "Athina", "Attica"));
    var product = new ProductDto(null, TEST_PRODUCT_NAME, new BigDecimal("4.50"), "", null);
    var body = new StoreDto(id, TEST_NAME, TEST_TYPE, areas, List.of(product));
    var token = JwtUtil.managerToken(TEST_NAME, id).token();
    var store =
        with()
            .body(body)
            .header("Authorization", "Bearer " + token)
            .contentType(JSON)
            .when()
            .put("{id}", id)
            .then()
            .statusCode(200)
            .extract()
            .as(StoreDto.class);
    assertThat(store.products()).singleElement().returns(TEST_PRODUCT_NAME, ProductDto::name);
  }

  @Test
  @Order(5)
  void testSearch() {
    var stores =
        with()
            .formParam("zip_code", TEST_ZIP_CODE)
            .formParam("products", List.of(TEST_PRODUCT_NAME))
            .when()
            .post("search")
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<List<StoreDto>>() {});
    assertThat(stores).singleElement().returns(id, StoreDto::id);
  }

  @Test
  @Order(6)
  @TestTransaction
  @TestSecurity(
      user = "root",
      roles = {"admin"})
  void testDelete() {
    when().delete("{id}", id).then().statusCode(204);
  }
}
