package gr.aueb.mscis.softeng.team6.delivery.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductTest {
  private static final String TEST_NAME = "foobar";
  private static final String TEST_COMMENT = "hello world";
  private static final BigDecimal TEST_PRICE = new BigDecimal("10.00");

  @Test
  void testToString() {
    var product = new Product().setName(TEST_NAME).setPrice(TEST_PRICE).setComment(TEST_COMMENT);
    assertThat(product)
        .hasToString("Product{name=\"foobar\", price=10.00, comment=\"hello world\"}");
  }
}
