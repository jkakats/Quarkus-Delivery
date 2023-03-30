package gr.aueb.mscis.softeng.team6.delivery.serialization.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import gr.aueb.mscis.softeng.team6.delivery.domain.Product;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ProductDto;
import io.quarkus.test.junit.QuarkusTest;
import java.math.BigDecimal;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ProductMapperTest {
  private static final Long TEST_ID = 12L;
  private static final String TEST_NAME = "souvlaki";
  private static final BigDecimal TEST_PRICE = new BigDecimal("3.00");
  private static final String TEST_COMMENT = "With plenty of sauce";

  @Inject protected ProductMapper mapper;

  @Test
  void testDeserialize() {
    var dto = new ProductDto(TEST_ID, TEST_NAME, TEST_PRICE, TEST_COMMENT, null);
    assertThat(mapper.deserialize(dto))
        .returns(dto.id(), Product::getId)
        .returns(dto.name(), Product::getName)
        .returns(dto.price(), Product::getPrice)
        .returns(dto.comment(), Product::getComment);
  }

  @Test
  void testSerialize() {
    var product =
        new Product()
            .setId(TEST_ID)
            .setName(TEST_NAME)
            .setPrice(TEST_PRICE)
            .setComment(TEST_COMMENT);
    assertThat(mapper.serialize(product))
        .returns(product.getId(), ProductDto::id)
        .returns(product.getName(), ProductDto::name)
        .returns(product.getPrice(), ProductDto::price)
        .returns(product.getComment(), ProductDto::comment);
  }
}
