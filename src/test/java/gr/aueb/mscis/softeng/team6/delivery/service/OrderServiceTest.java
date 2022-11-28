package gr.aueb.mscis.softeng.team6.delivery.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import gr.aueb.mscis.softeng.team6.delivery.domain.Address;
import gr.aueb.mscis.softeng.team6.delivery.domain.Area;
import gr.aueb.mscis.softeng.team6.delivery.domain.Client;
import gr.aueb.mscis.softeng.team6.delivery.domain.EmailAddress;
import gr.aueb.mscis.softeng.team6.delivery.domain.Order;
import gr.aueb.mscis.softeng.team6.delivery.domain.Password;
import gr.aueb.mscis.softeng.team6.delivery.domain.PhoneNumber;
import gr.aueb.mscis.softeng.team6.delivery.domain.Product;
import gr.aueb.mscis.softeng.team6.delivery.domain.Store;
import gr.aueb.mscis.softeng.team6.delivery.util.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
class OrderServiceTest {
  private static final Long TEST_WAIT_TIME = 15L;

  private static EntityManager em;
  private static ByteArrayOutputStream output;
  private static PrintStream stderr;
  private static List<Product> products;
  private static List<Store> stores;
  private static Client client;
  private static Order order;

  private OrderService service;

  @BeforeAll
  static void setUpAll() {
    stderr = System.err;
    em = EntityManagerUtil.getManager();

    var area = new Area().setCity("Athina").setState("Attica").setZipCode(11362);
    var address = new Address().setStreet("Lefkados").setApartment("47A").setArea(area);
    client =
        new Client()
            .setUsername("johndoe")
            .setName("John Doe")
            .setAddress(address)
            .setEmail(new EmailAddress("john@doe.com"))
            .setPassword(new Password("j0hnd0e!"))
            .setPhone(new PhoneNumber("6987654321"));

    products = new ArrayList<>(3);
    products.add(new Product().setName("product1").setPrice(BigDecimal.TEN));
    products.add(new Product().setName("product2").setPrice(BigDecimal.ONE));
    products.add(new Product().setName("product3").setPrice(BigDecimal.ONE));

    stores = new ArrayList<>(3);

    var store = new Store().setName("store1").setType("bakery");
    store.addProduct(products.get(0));
    store.addProduct(products.get(1));
    store.addArea(area);
    stores.add(store);

    store = new Store().setName("store2").setType("bakery");
    store.addProduct(products.get(1));
    store.addProduct(products.get(2));
    store.addArea(area);
    stores.add(store);

    area = new Area().setCity("Athina").setState("Attica").setZipCode(10434);
    store = new Store().setName("store3").setType("bakery");
    store.addProduct(products.get(0));
    store.addProduct(products.get(1));
    store.addArea(area);
    stores.add(store);

    var tx = em.getTransaction();
    tx.begin();
    em.persist(client);
    products.forEach(em::persist);
    stores.forEach(em::persist);
    tx.commit();
  }

  @BeforeEach
  void setUp() {
    service = new OrderService(em);
    output = new ByteArrayOutputStream();
    System.setErr(new PrintStream(output));
  }

  @Test
  @org.junit.jupiter.api.Order(1)
  void testGetAllProducts() {
    assertThat(service.getAllProducts()).hasSameElementsAs(products);
  }

  @Test
  @org.junit.jupiter.api.Order(2)
  void testChooseProducts() {
    order = service.chooseProducts(products.subList(0, 2), new int[] {2, 2});
    assertThat(order.getProducts()).hasSize(2).allMatch(o -> o.getQuantity() == 2);
  }

  @Test
  @org.junit.jupiter.api.Order(3)
  void testFindNearbyStores() {
    var stores = service.findNearbyStores(client, order);
    assertThat(stores).hasSize(1).first().isEqualTo(stores.get(0));
  }

  @Test
  @org.junit.jupiter.api.Order(4)
  void testSubmitOrder() {
    order = service.submitOrder(client, stores.get(0), order);
    assertThat(order).returns(client, Order::getClient).returns(stores.get(0), Order::getStore);
  }

  @Test
  @org.junit.jupiter.api.Order(5)
  void testConfirmOrder() {
    var provider = new MessageProviderStub();
    service.setMessageProvider(provider);

    order = service.confirmOrder(order, TEST_WAIT_TIME);
    assertThat(order)
        .returns(true, Order::isConfirmed)
        .returns(TEST_WAIT_TIME, Order::getEstimatedWait);

    var message =
        String.format(
            OrderService.CONFIRM_MESSAGE, order.getUuid(), order.getCost(), TEST_WAIT_TIME);
    assertThat(provider.getMessage()).isEqualTo(message);
  }

  @Test
  @org.junit.jupiter.api.Order(6)
  void testDeliverOrder() {
    order = service.deliverOrder(order);
    var now = LocalDateTime.now();
    var softly = new SoftAssertions();
    softly.assertThat(order.isDelivered()).isTrue();
    softly.assertThat(order.getDeliveredTime()).isEqualToIgnoringSeconds(now);
    softly.assertThat(order.getActualWait()).isNotNull();
    softly.assertAll();
  }

  @Test
  void testChooseProductsWithWrongQuantities() {
    assertThatIllegalArgumentException()
        .isThrownBy(
            () -> {
              service.chooseProducts(products, new int[] {1, 2, 3, 4});
            })
        .withMessage("Products and quantities must have the same size");
  }

  @Test
  void testSubmitOrderWithoutProducts() {
    var order = new Order().setClient(client).setStore(stores.get(0));
    assertThat(service.submitOrder(client, stores.get(0), order)).isNull();
    assertThat(output.toString()).startsWith("products size must be between 1");
  }

  @Test
  void testConfirmOrderWithoutStore() {
    var order = new Order().setClient(client);
    order.addProduct(products.get(2), 1);
    service.setMessageProvider(new MessageProviderStub());
    assertThat(service.confirmOrder(order, TEST_WAIT_TIME)).isNull();
    assertThat(output.toString()).isEqualTo("could not execute statement\n");
  }

  @Test
  void testDeliverOrderWithoutClient() {
    var order = new Order().setStore(stores.get(2));
    order.addProduct(products.get(2), 1);
    service.setMessageProvider(new MessageProviderStub());
    assertThat(service.deliverOrder(order)).isNull();
    assertThat(output.toString()).isEqualTo("could not execute statement\n");
  }

  @AfterEach
  void tearDown() {
    output.reset();
  }

  @AfterAll
  static void tearDownAll() {
    System.setErr(stderr);
    try {
      var tx = em.getTransaction();
      tx.begin();
      em.createQuery("delete Order").executeUpdate();
      em.createQuery("delete Store").executeUpdate();
      em.createQuery("delete Product").executeUpdate();
      em.createQuery("delete Client").executeUpdate();
      tx.commit();
    } finally {
      em.close();
    }
  }
}
