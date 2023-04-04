package gr.aueb.mscis.softeng.team6.delivery.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Product review entity.
 *
 * @since 0.1.0
 * @version 1.0.0
 */
@Entity
@Table(name = "product_review")
public class ProductReview extends Review {
  /** Product relation field. */
  @OneToOne(fetch = FetchType.LAZY, optional = false)
  private OrderProduct product;

  /** Parent review relation field. */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "parent")
  private OrderReview parent;

  public OrderProduct getProduct() {
    return product;
  }

  public ProductReview setProduct(OrderProduct product) {
    this.product = product;
    return this;
  }

  public OrderReview getParent() {
    return parent;
  }

  public ProductReview setParent(OrderReview parent) {
    this.parent = parent;
    return this;
  }

  @Override
  public ProductReview setRating(Short rating) {
    this.rating = rating;
    return this;
  }

  @Override
  public String toString() {
    return String.format(
        "ProductReview{product=\"%s\", rating=%d}", product.getProduct_id(), rating);
  }
}
