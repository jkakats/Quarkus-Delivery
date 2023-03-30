package gr.aueb.mscis.softeng.team6.delivery.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderProductTest {
  private OrderProduct orderProduct;

  @BeforeEach
  void setUp() {
    orderProduct = new OrderProduct().setQuantity(1);
  }

  @Test
  void testEquals() {
    var order = new Order();
    var product = new Product().setName("foobar");
    var orderProduct2 = new OrderProduct().setOrder(order).setProduct(product);
    orderProduct.setOrder(order).setProduct(product);

    assertThat(orderProduct).isEqualTo(orderProduct);
    assertThat(orderProduct).isNotEqualTo(new Object());
    assertThat(orderProduct).hasSameHashCodeAs(orderProduct2);
    orderProduct2.setProduct(new Product().setName("barfoo"));
    assertThat(orderProduct).isNotEqualTo(orderProduct2);
  }

  @Test
  void testToString() {
    orderProduct.setOrder(new Order()).setProduct(new Product());
    assertThat(orderProduct)
        .hasToString("OrderProduct{order=\"null\", product=\"null\", quantity=1}");
  }
}
