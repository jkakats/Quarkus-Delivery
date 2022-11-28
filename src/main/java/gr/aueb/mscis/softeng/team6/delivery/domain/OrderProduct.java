package gr.aueb.mscis.softeng.team6.delivery.domain;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * Order product entity.
 *
 * <p>The intermediate table between {@link Order} & {@link Product}.
 *
 * @since 0.1.0
 */
@Entity
@Table(
    name = "order_product",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"order_id", "product_id"})})
public class OrderProduct implements Serializable {
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
  @OneToOne(fetch = FetchType.LAZY, mappedBy = "product")
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

  protected ProductReview getReview() {
    return review;
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
