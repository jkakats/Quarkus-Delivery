package gr.aueb.mscis.softeng.team6.delivery.service;

import static org.assertj.core.api.Assertions.assertThat;

import gr.aueb.mscis.softeng.team6.delivery.domain.Area;
import gr.aueb.mscis.softeng.team6.delivery.domain.Product;
import gr.aueb.mscis.softeng.team6.delivery.domain.Store;
import gr.aueb.mscis.softeng.team6.delivery.util.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
class StoreServiceTest {
  private static final String TEST_NAME = "Pita Gyro";
  private static final String TEST_TYPE = "souvlakia";

  private static EntityManager em;
  private static ByteArrayOutputStream output;
  private static PrintStream stderr;
  private static Product product;
  private static Area area;

  private StoreService service;

  @BeforeAll
  static void setUpAll() {
    stderr = System.err;
    em = EntityManagerUtil.getManager();

    area = new Area().setCity("Athina").setState("Attica").setZipCode(10434);
    product = new Product().setName("product").setPrice(BigDecimal.TEN);
  }

  @BeforeEach
  void setUp() {
    service = new StoreService(em);
    output = new ByteArrayOutputStream();
    System.setErr(new PrintStream(output));
  }

  @Test
  @Order(1)
  void testRegisterStore() {
    var store = service.registerStore(TEST_NAME, TEST_TYPE, Set.of(area), Set.of(product));
    assertThat(store)
        .isNotNull()
        .returns(TEST_NAME, Store::getName)
        .returns(TEST_TYPE, Store::getType);
    assertThat(store.getAreas()).singleElement().isEqualTo(area);
    assertThat(store.getProducts()).singleElement().isEqualTo(product);
  }

  @Test
  void testRegisterStoreDuplicate() {
    var store = service.registerStore(TEST_NAME, TEST_TYPE, Set.of(area), Set.of(product));
    assertThat(store).isNull();
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
      em.createQuery("delete Product").executeUpdate();
      em.createQuery("delete Store").executeUpdate();
      tx.commit();
    } finally {
      em.close();
    }
  }
}
