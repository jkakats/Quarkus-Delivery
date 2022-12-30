package gr.aueb.mscis.softeng.team6.delivery.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import gr.aueb.mscis.softeng.team6.delivery.domain.Order;
import gr.aueb.mscis.softeng.team6.delivery.domain.OrderReview;
import gr.aueb.mscis.softeng.team6.delivery.domain.ProductReview;
import gr.aueb.mscis.softeng.team6.delivery.persistence.OrderRepository;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ReviewServiceTest {
  private static final short TEST_RATING = 5;

  private Order order;

  @Inject protected ReviewService service;
  @Inject protected OrderRepository orderRepository;

  @BeforeEach
  void setUp() {
    // language=HQL
    var query = "from Order o join fetch o.products where o.client.username like :username";
    order = orderRepository.stream(query, Map.of("username", "elpap")).findFirst().orElseThrow();
  }

  @Test
  @TestTransaction
  void testReviewOrder() {
    var review = service.reviewOrder(order, TEST_RATING, "hello", List.of(TEST_RATING));
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
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              service.reviewOrder(order, TEST_RATING, null, List.of());
            })
        .withMessage("Order products and ratings must have the same size (1)");
  }

  @Test
  @TestTransaction
  void testReviewOrderWithInvalidRating() {
    assertThatExceptionOfType(ConstraintViolationException.class)
        .isThrownBy(
            () -> {
              service.reviewOrder(order, (short) 6, null, List.of((short) 1));
            })
        .withMessageContaining("must be less than or equal to 5");
  }
}
