package gr.aueb.mscis.softeng.team6.delivery.service;

import static org.assertj.core.api.Assertions.assertThat;

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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class StatisticsServiceTest {
  private static EntityManager em;
  private static LocalDateTime now;
  private static List<Client> clients;
  private static List<Store> stores;
  private static List<Area> areas;

  private StatisticsService service;

  @BeforeAll
  static void setUpAll() {
    em = EntityManagerUtil.getManager();
    now = LocalDateTime.now();

    var area1 = new Area().setCity("Athina").setState("Attica").setZipCode(11362);
    var area2 = new Area().setCity("Athina").setState("Attica").setZipCode(10434);
    areas = List.of(area1, area2);

    var client1 =
        new Client()
            .setUsername("johndoe")
            .setName("John Doe")
            .setEmail(new EmailAddress("john@doe.com"))
            .setPassword(new Password("j0hnd0e!"))
            .setPhone(new PhoneNumber("6987654321"))
            .setAddress(new Address().setStreet("A").setApartment("1").setArea(area1));
    var client2 =
        new Client()
            .setUsername("janedoe")
            .setName("Jane Doe")
            .setEmail(new EmailAddress("jane@doe.com"))
            .setPassword(new Password("j4ned0e!"))
            .setPhone(new PhoneNumber("6981234567"))
            .setAddress(new Address().setStreet("B").setApartment("2").setArea(area2));
    clients = List.of(client1, client2);

    var product = new Product().setName("the product").setPrice(BigDecimal.TEN);

    var store1 =
        new Store()
            .setName("The Store")
            .setType("supermarket")
            .setAreas(Set.of(area1, area2))
            .setProducts(List.of(product));
    var store2 =
        new Store()
            .setName("My Store")
            .setType("supermarket")
            .setAreas(Set.of(area1))
            .setProducts(List.of(product));
    stores = List.of(store1, store2);

    var order1 =
        new Order()
            .setClient(client1)
            .setStore(store1)
            .setDelivered(true)
            .setDeliveredTime(now.plusMinutes(30));
    order1.addProduct(product, 2);
    var order2 =
        new Order()
            .setClient(client1)
            .setStore(store1)
            .setDelivered(true)
            .setDeliveredTime(now.plusMinutes(20));
    order2.addProduct(product, 3);
    var order3 =
        new Order()
            .setClient(client2)
            .setStore(store2)
            .setDelivered(true)
            .setDeliveredTime(now.plusMinutes(20));
    order3.addProduct(product, 1);

    var tx = em.getTransaction();
    tx.begin();
    em.persist(client1);
    em.persist(client2);
    em.persist(product);
    em.persist(store1);
    em.persist(store2);
    em.persist(order1);
    em.persist(order2);
    em.persist(order3);
    tx.commit();
  }

  @BeforeEach
  void setUp() {
    service = new StatisticsService(em);
  }

  @ParameterizedTest
  @MethodSource("frequentClientsProvider")
  void testFindFrequentClients(Store store, List<Client> clients) {
    var max = 2;
    var start = now.minusDays(1);
    var end = now.plusDays(1);
    var results = service.findFrequentClients(store, start, end, max);
    assertThat(results).hasSizeLessThanOrEqualTo(max).hasSameElementsAs(clients);
  }

  @ParameterizedTest
  @MethodSource("averageDeliveryTimeProvider")
  void testGetAverageDeliveryTime(Store store, Area area, Long average) {
    assertThat(service.getAverageDeliveryTime(store, area)).isEqualTo(average);
  }

  @Test
  void testGetRushHours() {
    var week = StatisticsService.truncateToWeek(now);
    var hour = Integer.valueOf(now.getHour());
    assertThat(service.getRushHours(stores.get(0), week, 0)).allMatch(hour::equals);
  }

  @AfterAll
  static void tearDownAll() {
    try {
      var tx = em.getTransaction();
      tx.begin();
      em.createQuery("delete Order").executeUpdate();
      em.createQuery("delete Product").executeUpdate();
      em.createQuery("delete Client").executeUpdate();
      em.createQuery("delete Store").executeUpdate();
      tx.commit();
    } finally {
      em.close();
    }
  }

  private static Stream<Arguments> frequentClientsProvider() {
    return Stream.of(
        Arguments.of(stores.get(0), clients.subList(0, 1)),
        Arguments.of(stores.get(1), clients.subList(1, 2)));
  }

  private static Stream<Arguments> averageDeliveryTimeProvider() {
    return Stream.of(
        Arguments.of(stores.get(0), areas.get(0), 25L),
        Arguments.of(stores.get(1), areas.get(0), 20L),
        Arguments.of(stores.get(1), areas.get(1), null));
  }
}
