package gr.aueb.mscis.softeng.team6.delivery.service;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.byLessThan;

import gr.aueb.mscis.softeng.team6.delivery.domain.Area;
import gr.aueb.mscis.softeng.team6.delivery.domain.Order;
import gr.aueb.mscis.softeng.team6.delivery.domain.OrderProduct;
import gr.aueb.mscis.softeng.team6.delivery.persistence.ClientRepository;
import gr.aueb.mscis.softeng.team6.delivery.persistence.OrderRepository;
import gr.aueb.mscis.softeng.team6.delivery.persistence.ProductRepository;
import gr.aueb.mscis.softeng.team6.delivery.persistence.StoreRepository;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import java.math.BigDecimal;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class OrderServiceTest {
  private static final long TEST_WAIT_TIME = 15L;

  @Inject protected OrderService service;
  @Inject protected OrderRepository orderRepository;
  @Inject protected ProductRepository productRepository;
  @Inject protected StoreRepository storeRepository;
  @Inject protected ClientRepository clientRepository;

  @Test
  @TestTransaction
  void testSubmitOrder() {
    var area = new Area().setZipCode(10434);
    var client = clientRepository.findByUsername("johndoe2").orElse(null);
    var names = productRepository.listNames().subList(0, 1);
    var store = storeRepository.findByArea(area, names).get(0);
    var products =
        store.getProducts().stream()
            .filter(p -> names.contains(p.getName()))
            .map(p -> new OrderProduct().setProduct(p).setQuantity(1))
            .collect(toSet());
    var order = service.submitOrder(client, store, products);

    assertThat(order)
        .doesNotReturn(null, Order::getUuid)
        .returns(false, Order::isConfirmed)
        .returns(client, Order::getClient)
        .returns(store, Order::getStore);
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
