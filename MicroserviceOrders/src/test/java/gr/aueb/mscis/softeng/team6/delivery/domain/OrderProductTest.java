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
    var orderProduct2 = new OrderProduct().setOrder(order).setProduct_id(11);
    orderProduct.setOrder(order).setProduct_id(11);

    assertThat(orderProduct).isEqualTo(orderProduct);
    assertThat(orderProduct).isNotEqualTo(new Object());
    assertThat(orderProduct).hasSameHashCodeAs(orderProduct2);
    orderProduct2.setProduct_id(12);
    assertThat(orderProduct).isNotEqualTo(orderProduct2);
  }

  @Test
  void testToString() {
    orderProduct.setOrder(new Order());
    assertThat(orderProduct)
        .hasToString("OrderProduct{order=\"null\", product=\"0\", quantity=1}");
  }
}
