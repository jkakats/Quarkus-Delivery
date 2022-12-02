package gr.aueb.mscis.softeng.team6.delivery.domain;

import static java.time.temporal.ChronoUnit.MINUTES;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Order entity.
 *
 * @since 0.1.0
 * @version 0.1.1
 */
@Entity
@Table(
    name = "`order`",
    indexes = {@Index(columnList = "ordered_time")})
public class Order implements Serializable {
  /** Auto-generated UUID field. */
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @ColumnDefault("random_uuid()")
  private UUID uuid;

  /** Order confirmation field. */
  @ColumnDefault("false")
  @Column(nullable = false)
  private Boolean confirmed = false;

  /** Delivery completion field. */
  @ColumnDefault("false")
  @Column(nullable = false)
  private Boolean delivered = false;

  /** Order time field. */
  @CreationTimestamp
  @ColumnDefault("current_timestamp(0)")
  @Column(name = "ordered_time", updatable = false, columnDefinition = "timestamp(0)")
  private LocalDateTime orderedTime;

  /** Delivery time field. */
  @Column(name = "delivered_time", columnDefinition = "timestamp(0)")
  private LocalDateTime deliveredTime;

  /** Estimated waiting time field (in minutes). */
  @Column(name = "estimated_wait", updatable = false)
  private Long estimatedWait;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "order")
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(insertable = false)
  private OrderReview review;

  /** Client relation field. */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(updatable = false)
  private Client client;

  /** Store relation field. */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(updatable = false)
  private Store store;

  /** Order products relation field. */
  @Size(min = 1)
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Set<OrderProduct> products = new HashSet<>();

  /** Actual waiting time field (in minutes). */
  @Transient private Long actualWait;

  public UUID getUuid() {
    return uuid;
  }

  public Boolean isConfirmed() {
    return confirmed;
  }

  public Order setConfirmed(Boolean confirmed) {
    this.confirmed = confirmed;
    return this;
  }

  public Boolean isDelivered() {
    return delivered;
  }

  public Order setDelivered(Boolean delivered) {
    this.delivered = delivered;
    return this;
  }

  public LocalDateTime getOrderedTime() {
    return orderedTime;
  }

  protected Order setOrderedTime(LocalDateTime orderedTime) {
    this.orderedTime = orderedTime;
    return this;
  }

  public LocalDateTime getDeliveredTime() {
    return deliveredTime;
  }

  public Order setDeliveredTime(LocalDateTime deliveredTime) {
    this.deliveredTime = deliveredTime;
    return this;
  }

  public Long getEstimatedWait() {
    return estimatedWait;
  }

  public Order setEstimatedWait(Long estimatedWait) {
    this.estimatedWait = estimatedWait;
    return this;
  }

  /** Calculate the actual waiting time. */
  public Long getActualWait() {
    if (actualWait != null) {
      return actualWait;
    }
    if (orderedTime == null || deliveredTime == null) {
      return null;
    }
    actualWait = orderedTime.until(deliveredTime, MINUTES);
    return actualWait;
  }

  public Client getClient() {
    return client;
  }

  public Order setClient(Client client) {
    this.client = client;
    return this;
  }

  public Store getStore() {
    return store;
  }

  public Order setStore(Store store) {
    this.store = store;
    return this;
  }

  public OrderReview getReview() {
    return review;
  }

  public Order setReview(OrderReview review) {
    this.review = review;
    return this;
  }

  public Set<OrderProduct> getProducts() {
    return products;
  }

  public Order setProducts(Set<OrderProduct> products) {
    this.products = products;
    return this;
  }

  /**
   * Add a product to the order.
   *
   * @param product the product to be added.
   * @param quantity the quantity of the product.
   */
  public void addProduct(Product product, int quantity) {
    var orderProduct = new OrderProduct().setOrder(this).setProduct(product).setQuantity(quantity);
    this.products.add(orderProduct);
  }

  /** Calculate the total cost of the order. */
  public BigDecimal getCost() {
    return products.stream()
        .map(p -> new BigDecimal(p.getQuantity()).multiply(p.getProduct().getPrice()))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  @Override
  public String toString() {
    return String.format(
        "Order{uuid=\"%s\", confirmed=%b, delivered=%b, orderedTime=\"%tF %<tT\","
            + " deliveredTime=\"%tF %<tT\", estimatedWait=%d, actualWait=%d}",
        uuid, confirmed, delivered, orderedTime, deliveredTime, estimatedWait, actualWait);
  }
}
