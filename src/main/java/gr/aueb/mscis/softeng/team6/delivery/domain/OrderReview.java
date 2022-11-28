package gr.aueb.mscis.softeng.team6.delivery.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "order_review")
public class OrderReview extends Review {
  /** Optional comment field. */
  @Column(length = 1000)
  private String comment;

  /** Order relation field. */
  @OneToOne(fetch = FetchType.LAZY, optional = false)
  protected Order order;

  public String getComment() {
    return comment;
  }

  public OrderReview setComment(String comment) {
    this.comment = comment;
    return this;
  }

  public Order getOrder() {
    return order;
  }

  public OrderReview setOrder(Order order) {
    this.order = order;
    return this;
  }

  /** Get the product reviews associated with this order review. */
  public List<ProductReview> getProductReviews() {
    return order.getProducts().stream().map(OrderProduct::getReview).toList();
  }

  @Override
  public String toString() {
    return String.format(
        "OrderReview{order=\"%s\", rating=%d, comment=\"%s\"}", order.getUuid(), rating, comment);
  }
}
