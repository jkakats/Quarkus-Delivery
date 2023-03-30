package gr.aueb.mscis.softeng.team6.delivery.domain;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ReviewTest {
  private static final short TEST_RATING = 1;
  private static final String TEST_COMMENT = "nice";
  private static final String TEST_PRODUCT = "something";

  @Nested
  class OrderReviewTest {
    @Test
    void testToString() {
      var review =
          new OrderReview()
              .setOrder(new Order())
              .setRating(TEST_RATING)
              .setComment(TEST_COMMENT)
              .setProductReviews(emptyList());
      assertThat(review.getId()).isNull();
      assertThat(review).hasToString("OrderReview{order=\"null\", rating=1, comment=\"nice\"}");
    }
  }

  @Nested
  class ProductReviewTest {
    @Test
    void testToString() {
      var product = new OrderProduct().setProduct(new Product().setName(TEST_PRODUCT));
      var review = new ProductReview().setProduct(product).setRating(TEST_RATING);
      assertThat(review.getId()).isNull();
      assertThat(review).hasToString("ProductReview{product=\"something\", rating=1}");
    }
  }
}
