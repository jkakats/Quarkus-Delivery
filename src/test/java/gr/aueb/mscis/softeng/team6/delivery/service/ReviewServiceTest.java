package gr.aueb.mscis.softeng.team6.delivery.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import gr.aueb.mscis.softeng.team6.delivery.domain.Address;
import gr.aueb.mscis.softeng.team6.delivery.domain.Area;
import gr.aueb.mscis.softeng.team6.delivery.domain.Client;
import gr.aueb.mscis.softeng.team6.delivery.domain.EmailAddress;
import gr.aueb.mscis.softeng.team6.delivery.domain.Order;
import gr.aueb.mscis.softeng.team6.delivery.domain.OrderReview;
import gr.aueb.mscis.softeng.team6.delivery.domain.Password;
import gr.aueb.mscis.softeng.team6.delivery.domain.PhoneNumber;
import gr.aueb.mscis.softeng.team6.delivery.domain.Product;
import gr.aueb.mscis.softeng.team6.delivery.domain.ProductReview;
import gr.aueb.mscis.softeng.team6.delivery.domain.Store;
import gr.aueb.mscis.softeng.team6.delivery.util.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
class ReviewServiceTest {
  private static final short TEST_RATING = 5;

  private static EntityManager em;
  private static ByteArrayOutputStream output;
  private static PrintStream stderr;
  private static Order order;

  private ReviewService service;

  @BeforeAll
  static void setUpAll() {
    stderr = System.err;
    em = EntityManagerUtil.getManager();

    var area = new Area().setCity("Athina").setState("Attica").setZipCode(11362);
    var address = new Address().setStreet("Lefkados").setApartment("47A").setArea(area);
    var client =
        new Client()
            .setUsername("johndoe")
            .setName("John Doe")
            .setAddress(address)
            .setEmail(new EmailAddress("john@doe.com"))
            .setPassword(new Password("j0hnd0e!"))
            .setPhone(new PhoneNumber("6987654321"));
    var product = new Product().setName("product").setPrice(BigDecimal.TEN);
    var store = new Store().setName("store").setType("bakery");
    store.addProduct(product);
    store.addArea(area);
    order = new Order().setClient(client).setStore(store);
    order.addProduct(product, 1);

    var tx = em.getTransaction();
    tx.begin();
    em.persist(client);
    em.persist(product);
    em.persist(store);
    em.persist(order);
    tx.commit();
  }

  @BeforeEach
  void setUp() {
    service = new ReviewService(em);
    output = new ByteArrayOutputStream();
    System.setErr(new PrintStream(output));
  }

  @Test
  @org.junit.jupiter.api.Order(1)
  void testReviewOrder() {
    var review = service.reviewOrder(order, TEST_RATING, "hello", new Short[] {TEST_RATING});
    assertThat(review)
        .isNotNull()
        .returns(order, OrderReview::getOrder)
        .returns(TEST_RATING, OrderReview::getRating)
        .returns("hello", OrderReview::getComment);
    assertThat(review.getProductReviews())
        .singleElement()
        .returns(TEST_RATING, ProductReview::getRating)
        .returns(review, ProductReview::getParent);
  }

  @Test
  void testReviewOrderWithoutRatings() {
    assertThatIllegalArgumentException()
        .isThrownBy(
            () -> {
              service.reviewOrder(order, TEST_RATING, null, new Short[] {});
            })
        .withMessage("Order products and ratings must have the same size");
  }

  @Test
  void testReviewOrderWithInvalidRating() {
    assertThat(service.reviewOrder(order, (short) 6, null, new Short[] {0})).isNull();
    assertThat(output.toString()).startsWith("rating must be less than or equal to 5\n");
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
      em.createQuery("delete OrderReview").executeUpdate();
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
