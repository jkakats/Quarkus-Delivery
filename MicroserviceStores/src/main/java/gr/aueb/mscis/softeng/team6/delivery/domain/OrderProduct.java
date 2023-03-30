package gr.aueb.mscis.softeng.team6.delivery.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Order product entity.
 *
 * <p>The intermediate table between {@link Order} & {@link Product}.
 *
 * @since 0.1.0
 * @version 1.0.0
 */
@Entity
@Table(
    name = "order_product",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"order_id", "product_id"})})
public class OrderProduct {
  /** Auto-generated ID field. */
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  /** Order relation field. */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order_id")
  private Order order;

  /** Product relation field. */
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "product_id")
  private Product product;

  /** Quantity field. */
  @NotNull
  @Min(1)
  private Integer quantity;

  /** Product review relation field. */
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "product")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private ProductReview review;

  public Long getId() {
    return id;
  }

  public Order getOrder() {
    return order;
  }

  public OrderProduct setOrder(Order order) {
    this.order = order;
    return this;
  }

  public Product getProduct() {
    return product;
  }

  public OrderProduct setProduct(Product product) {
    this.product = product;
    return this;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public OrderProduct setQuantity(Integer quantity) {
    this.quantity = quantity;
    return this;
  }

  public ProductReview getReview() {
    return review;
  }

  public OrderProduct setReview(ProductReview review) {
    this.review = review;
    return this;
  }

  @Override
  public boolean equals(Object that) {
    if (this == that) {
      return true;
    }
    if (!(that instanceof OrderProduct other)) {
      return false;
    }
    return order.equals(other.order) && product.equals(other.product);
  }

  @Override
  public int hashCode() {
    return Objects.hash(order, product);
  }

  @Override
  public String toString() {
    return String.format(
        "OrderProduct{order=\"%s\", product=\"%s\", quantity=%d}",
        order.getUuid(), product.getName(), quantity);
  }
}
