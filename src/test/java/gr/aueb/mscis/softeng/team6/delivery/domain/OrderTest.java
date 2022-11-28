package gr.aueb.mscis.softeng.team6.delivery.domain;

import static org.assertj.core.api.Assertions.assertThat;

import gr.aueb.mscis.softeng.team6.delivery.util.EntityManagerUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
class OrderTest {
  private static final BigDecimal TEST_PRICE = BigDecimal.TEN;
  private static final int TEST_QUANTITY = 2;
  private static final long TEST_WAIT = 42L;
  private static final LocalDateTime TEST_TIME = LocalDateTime.of(2015, 3, 14, 9, 26, 53);

  private Client client;
  private Store store;
  private Product product;
  private Order order;

  @BeforeEach
  void setUp() {
    client =
        new Client()
            .setUsername("johndoe")
            .setName("John Doe")
            .setEmail(new EmailAddress("john@doe.com"))
            .setPassword(new Password("j0hnd0e!"))
            .setPhone(new PhoneNumber("6987654321"));
    store = new Store().setName("nameless").setType("none");
    product = new Product().setName("foobar").setPrice(TEST_PRICE);
    order =
        new Order()
            .setClient(client)
            .setStore(store)
            .setEstimatedWait(TEST_WAIT)
            .setProducts(new HashSet<>(1))
            .setConfirmed(false)
            .setDelivered(false)
            .setDeliveredTime(null);
  }

  @Test
  @org.junit.jupiter.api.Order(1)
  void testPersist() {
    order.addProduct(product, TEST_QUANTITY);
    var now = LocalDateTime.now();
    EntityManagerUtil.runTransaction(
        em -> {
          em.persist(client);
          em.persist(store);
          em.persist(product);
          em.persist(order);
          var softly = new SoftAssertions();
          softly.assertThat(order.getUuid()).isNotNull();
          softly.assertThat(order.getEstimatedWait()).isEqualTo(TEST_WAIT);
          softly.assertThat(order.getOrderedTime()).isEqualToIgnoringSeconds(now);
          softly.assertThat(order.isConfirmed()).isFalse();
          softly.assertThat(order.isDelivered()).isFalse();
          softly.assertAll();
        });
  }

  @Test
  @org.junit.jupiter.api.Order(2)
  void testQuery() {
    EntityManagerUtil.runTransaction(
        em -> {
          var order = em.createQuery("from Order", Order.class).getResultList().get(0);
          assertThat(order.getClient()).isEqualTo(client);
          assertThat(order.getStore()).isEqualTo(store);
          assertThat(order.getProducts())
              .hasSize(1)
              .first()
              .satisfies(
                  op -> {
                    assertThat(op.getId()).isNotNull();
                    assertThat(op)
                        .returns(TEST_QUANTITY, OrderProduct::getQuantity)
                        .returns(order, OrderProduct::getOrder)
                        .returns(product.getName(), o -> o.getProduct().getName());
                  });
          em.remove(order);
          em.remove(order.getClient());
          em.remove(order.getStore());
          order.getProducts().forEach(p -> em.remove(p.getProduct()));
        });
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
    order.addProduct(product, TEST_QUANTITY);
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
