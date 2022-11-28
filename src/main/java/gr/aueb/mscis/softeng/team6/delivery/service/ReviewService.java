package gr.aueb.mscis.softeng.team6.delivery.service;

import gr.aueb.mscis.softeng.team6.delivery.domain.Order;
import gr.aueb.mscis.softeng.team6.delivery.domain.OrderReview;
import jakarta.persistence.EntityManager;
import java.util.List;

/**
 * Service that handles reviews.
 *
 * @since 0.1.0
 */
public class ReviewService extends BaseService {
  public ReviewService(EntityManager em) {
    super(em);
  }

  /**
   * Submit a review for a given order.
   *
   * @param order the order to be reviewed.
   * @param rating the rating of the order.
   * @param comment an optional comment.
   * @param productRatings the ratings of each product.
   * @return a new {@link OrderReview} object or {@code null} on error.
   * @throws IllegalArgumentException if the product ratings array has the wrong size.
   */
  public OrderReview reviewOrder(
      Order order, short rating, String comment, Short[] productRatings) {
    var review = new OrderReview().setOrder(order).setRating(rating).setComment(comment);
    var products = List.copyOf(order.getProducts());
    var size = products.size();
    if (size != productRatings.length) {
      throw new IllegalArgumentException("Order products and ratings must have the same size");
    }
    for (int i = 0; i < size; ++i) {
      if (productRatings[i] != null) {
        review.addProductReview(products.get(i), productRatings[i]);
      }
    }
    return persistObject(review);
  }
}
