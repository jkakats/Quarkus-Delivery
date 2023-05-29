package gr.aueb.mscis.softeng.team6.delivery.service;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.byLessThan;

import gr.aueb.mscis.softeng.team6.delivery.domain.Order;
import gr.aueb.mscis.softeng.team6.delivery.domain.OrderProduct;
import gr.aueb.mscis.softeng.team6.delivery.persistence.OrderRepository;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class OrderServiceTest {
  private static final long TEST_WAIT_TIME = 15L;

  @Inject protected OrderService service;
  @Inject protected OrderRepository orderRepository;
  private static final BigDecimal TEST_PRICE = new BigDecimal("6.50");

  @Test
  @TestTransaction
  void testSubmitOrder() {
    UUID client_uuid = UUID.randomUUID();
    long store_id = 4L;
    var p = new OrderProduct().setProduct_id(11L).setPrice(TEST_PRICE).setQuantity(1);
    Set<OrderProduct> products = new HashSet<OrderProduct>();
    products.add(p);
    var order = service.submitOrder(client_uuid, store_id, products);

    assertThat(order)
        .doesNotReturn(null, Order::getUuid)
        .returns(false, Order::isConfirmed)
        .returns(client_uuid, Order::getClient_uuid)
        .returns(store_id, Order::getStore_id);
    assertThat(order.getProducts()).hasSameElementsAs(products);
  }

  @Test
  @TestTransaction
  void testConfirmOrder() {
    var order = orderRepository.find("confirmed", new Object[] {false}).firstResult();
    var provider = new MessageProviderStub();
    service.setMessageProvider(provider);
    service.confirmOrder(order, TEST_WAIT_TIME);

    assertThat(order)
        .returns(true, Order::isConfirmed)
        .returns(TEST_WAIT_TIME, Order::getEstimatedWait);
    assertThat(provider.getMessage())
        .isEqualTo(
            MessageProvider.CONFIRM_MESSAGE.formatted(
                order.getUuid(), new BigDecimal("14.50"), TEST_WAIT_TIME));
  }

  @Test
  @TestTransaction
  void testDeliverOrder() {
    var order = orderRepository.find("delivered", new Object[] {false}).firstResult();
    service.deliverOrder(order);
    assertThat(order.isDelivered()).isTrue();
    assertThat(order.getDeliveredTime()).isCloseTo(now(), byLessThan(1, MINUTES));
  }
}
