package gr.aueb.mscis.softeng.team6.delivery.domain;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Product review entity.
 *
 * @since 0.1.0
 */
@Entity
@Table(name = "product_review")
public class ProductReview extends Review {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  /** Product relation field. */
  @OneToOne(fetch = FetchType.LAZY, optional = false)
  private OrderProduct product;

  /** Parent review relation field. */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "parent")
  private OrderReview parent;

  public Long getId() {
    return id;
  }

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
        "ProductReview{product=\"%s\", rating=%d}", product.getProduct().getName(), rating);
  }
}
