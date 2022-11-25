package gr.aueb.mscis.softeng.team6.delivery.domain;

import static org.assertj.core.api.Assertions.assertThat;

import gr.aueb.mscis.softeng.team6.delivery.util.EntityManagerUtil;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
class ProductTest {
  private static final String TEST_NAME = "foobar";
  private static final String TEST_COMMENT = "hello world";
  private static final BigDecimal TEST_PRICE = new BigDecimal("10.00");

  private Product product;

  @BeforeEach
  void setUp() {
    product = new Product().setName(TEST_NAME).setPrice(TEST_PRICE).setComment(TEST_COMMENT);
  }

  @Test
  @Order(1)
  void testPersist() {
    EntityManagerUtil.runTransaction(
        em -> {
          em.persist(product);
          assertThat(product.getId()).isNotNull();
        });
  }

  @Test
  @Order(2)
  void testQuery() {
    EntityManagerUtil.runTransaction(
        em -> {
          var product = em.createQuery("from Product", Product.class).getResultList().get(0);
          assertThat(product)
              .returns(TEST_NAME, Product::getName)
              .returns(TEST_COMMENT, Product::getComment)
              .returns(TEST_PRICE, Product::getPrice);
          em.remove(product);
        });
  }

  @Test
  void testEquals() {
    var product2 = new Product().setName(TEST_NAME);
    assertThat(product).hasSameHashCodeAs(product2);
    product2.setName("other-name");
    assertThat(product).isNotEqualTo(product2);
  }

  @Test
  void testToString() {
    assertThat(product)
        .hasToString("Product{name=\"foobar\", price=10.00, comment=\"hello world\"}");
  }
}
