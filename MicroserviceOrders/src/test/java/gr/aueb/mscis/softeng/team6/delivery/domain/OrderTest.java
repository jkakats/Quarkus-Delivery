package gr.aueb.mscis.softeng.team6.delivery.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderTest {
  private static final LocalDateTime TEST_TIME = LocalDateTime.of(2015, 3, 14, 9, 26, 53);
  private static final BigDecimal TEST_PRICE = BigDecimal.TEN;
  private static final int TEST_QUANTITY = 2;
  private static final long TEST_WAIT = 42L;

  private Order order;

  @BeforeEach
  void setUp() {
    order =
        new Order()
            .setClient_uuid(null)
            .setStore_id(null)
            .setEstimatedWait(TEST_WAIT)
            .setProducts(new HashSet<>(1))
            .setConfirmed(false)
            .setDelivered(false)
            .setDeliveredTime(null);
  }

  @Test
  void testActualWait() {
    assertThat(order.getActualWait()).isNull();
    var wait = TEST_TIME.plusMinutes(TEST_WAIT);
    order.setOrderedTime(TEST_TIME).setDeliveredTime(wait);
    assertThat(order)
        .returns(wait, Order::getDeliveredTime)
        .returns(TEST_WAIT, Order::getActualWait);
  }

  @Test
  void testCost() {
    var cost = TEST_PRICE.multiply(new BigDecimal(TEST_QUANTITY));
    order.addProduct(2,TEST_PRICE, TEST_QUANTITY);
    assertThat(order.getCost()).isEqualTo(cost);
  }

  @Test
  void testToString() {
    assertThat(order.setOrderedTime(TEST_TIME))
        .hasToString(
            "Order{uuid=\"null\", confirmed=false, delivered=false, "
                + "orderedTime=\"2015-03-14 09:26:53\", deliveredTime=\"null null\", "
                + "estimatedWait=42, actualWait=null}");
  }
}
