package gr.aueb.mscis.softeng.team6.delivery.resource;

import static io.restassured.RestAssured.when;
import static io.restassured.RestAssured.with;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;

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
@TestHTTPEndpoint(ProductResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductResourceTest {
  private static final String TEST_NAME = "Special Burger";
  private static final BigDecimal TEST_PRICE = new BigDecimal("6.00");
  private static final String TEST_COMMENT = "Very special";

  private static Long id = null;

  @Test
  @Order(1)
  void testList() {
    var stores =
        when().get().then().statusCode(200).extract().as(new TypeRef<List<ProductDto>>() {});
    assertThat(stores).hasSize(15).first().returns("Πίτα Γύρο Χοιρινό", ProductDto::name);
  }

  @Test
  @Order(2)
  @TestTransaction
  @TestSecurity(
      user = "root",
      roles = {"admin"})
  void testCreate() {
    var store = new StoreDto(2L, "Domino's Pizza", "Pizza", Set.of(), List.of());
    var body = new ProductDto(null, TEST_NAME, TEST_PRICE, TEST_COMMENT, store);
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
    assertThat(location).contains("/products/");
    id = Long.valueOf(location.split("/")[4]);
  }

  @Test
  @Order(3)
  void testRead() {
    var product = when().get("{id}", id).then().statusCode(200).extract().as(ProductDto.class);
    assertThat(product).returns(id, ProductDto::id).returns(TEST_NAME, ProductDto::name);
  }

  @Test
  @Order(4)
  @TestTransaction
  void testUpdate() {
    var price = TEST_PRICE.subtract(new BigDecimal("1.00"));
    var store = new StoreDto(2L, "Domino's Pizza", "Pizza", Set.of(), List.of());
    var body = new ProductDto(null, TEST_NAME, price, TEST_COMMENT, store);
    var token = JwtUtil.managerToken("Domino's", 2L).token();
    var product =
        with()
            .body(body)
            .header("Authorization", "Bearer " + token)
            .contentType(JSON)
            .when()
            .put("{id}", id)
            .then()
            .statusCode(200)
            .extract()
            .as(ProductDto.class);
    assertThat(product.price()).isNotEqualTo(TEST_PRICE).isEqualTo(price);
  }

  @Test
  @Order(5)
  void testCatalogue() {
    var catalogue =
        List.of(
            "Πίτα Γύρο Χοιρινό",
            "Πίτα Γύρο Κοτόπουλο",
            "Cheese Burger",
            "Special Burger",
            "Pizza Margarita",
            "Mexican Pizza");
    var result =
        when().get("catalogue").then().statusCode(200).extract().as(new TypeRef<List<String>>() {});
    assertThat(result).hasSize(6).hasSameElementsAs(catalogue);
  }

  @Test
  @Order(6)
  void testSearch() {
    var products =
        with()
            .queryParam("q", TEST_NAME.toLowerCase())
            .when()
            .get("search")
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<List<ProductDto>>() {});
    assertThat(products).hasSize(2).allMatch(p -> p.name().equals(TEST_NAME));
  }

  @Test
  @Order(7)
  @TestTransaction
  @TestSecurity(
      user = "root",
      roles = {"admin"})
  void testDelete() {
    when().delete("{id}", id).then().statusCode(204);
  }
}
