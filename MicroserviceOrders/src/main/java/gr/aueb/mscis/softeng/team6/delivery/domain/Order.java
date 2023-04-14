package gr.aueb.mscis.softeng.team6.delivery.domain;

import static java.time.temporal.ChronoUnit.MINUTES;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

@SqlResultSetMapping(
  name = "ScalarResult",
  columns = {@ColumnResult(name = "result")})
@NamedQueries({
  @NamedQuery(
    name = "findFrequentClients",
    query =
      """
    select o.client_uuid from Order o
      where o.store_id = :store
      and (o.orderedTime between :start and :end)
    group by o.client_uuid
    order by count(o) desc
    """
    )})

@NamedNativeQueries({
  @NamedNativeQuery(
    name = "getAverageDeliveryTime",
    query =
      """
    SELECT
      ROUND(AVG(DATEDIFF(MINUTE, `ordered_time`, `delivered_time`))) `result`
    FROM `order`
    WHERE `delivered` = true
      AND `store_id` = :store
      AND `client_uuid` IN :clientUUIDs
    """,
    resultSetMapping = "ScalarResult"
    ),
  @NamedNativeQuery(
    name = "getRushHours",
    query =
      """
    SELECT
      EXTRACT(HOUR FROM `ordered_time`) `result`
    FROM `order`
    WHERE `store_id` = :store
      AND DATE_TRUNC(WEEK, `ordered_time`) = :week
    GROUP BY `result`
    HAVING COUNT(`result`) > :limit
    """,
    resultSetMapping = "ScalarResult"
    )
})
/**
 * Order entity.
 *
 * @since 0.1.0
 * @version 1.0.0
 */
@Entity
@Table(
    name = "`order`",
    indexes = {@Index(columnList = "ordered_time")})
public class Order {
  /** Auto-generated UUID field. */
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  // language=H2 prefix="call "
  @ColumnDefault("random_uuid()")
  // language=H2 prefix="call cast(null as " suffix=")"
  @Column(columnDefinition = "uuid")
  private UUID uuid;

  /** Order confirmation field. */
  // language=H2 prefix="call " suffix=""
  @ColumnDefault("false")
  @Column(nullable = false)
  private Boolean confirmed = false;

  /** Delivery completion field. */
  // language=H2 prefix="call " suffix=""
  @ColumnDefault("false")
  @Column(nullable = false)
  private Boolean delivered = false;

  /** Order time field. */
  @CreationTimestamp
  // language=H2 prefix="call " suffix=""
  @ColumnDefault("current_timestamp(0)")
  @Column(
      name = "ordered_time",
      updatable = false,
      // language=H2 prefix="call cast(null as " suffix=")"
      columnDefinition = "timestamp(0)")
  private LocalDateTime orderedTime;

  /** Delivery time field. */
  @Column(
      name = "delivered_time",
      // language=H2 prefix="call cast(null as " suffix=")"
      columnDefinition = "timestamp(0)")
  private LocalDateTime deliveredTime;

  /** Estimated waiting time field (in minutes). */
  @Column(name = "estimated_wait")
  private Long estimatedWait;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "order")
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(insertable = false)
  private OrderReview review;

  /** Client relation field. */
  @Column(name="client_uuid")
  @Type(type = "uuid-char")
  private UUID client_uuid;

  /** Store relation field. */
  @Column(name="store_id")
  private Long store_id;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Set<OrderProduct> products = new HashSet<>();

  /** Actual waiting time field (in minutes). */
  @Transient private Long actualWait;

  public UUID getUuid() {
    return uuid;
  }

  public Order setUuid(UUID uuid) {
    this.uuid = uuid;
    return this;
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

  public Order setOrderedTime(LocalDateTime orderedTime) {
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

  public OrderReview getReview() {
    return review;
  }

  public Order setReview(OrderReview review) {
    this.review = review;
    return this;
  }

  public UUID getClient_uuid() {
    return client_uuid;
  }

  public Order setClient_uuid(UUID client_uuid) {
    this.client_uuid = client_uuid;
    return this;
  }

  public Long getStore_id() {
    return store_id;
  }

  public Order setStore_id(Long store_id) {
    this.store_id = store_id;
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
     //* @param product_id the product to be added.
     //* @param quantity the quantity of the product.
     */
  public void addProduct(long product_id,BigDecimal price, int quantity) {
    var orderProduct = new OrderProduct().setOrder(this).setProduct_id(product_id).setPrice(price).setQuantity(quantity);
    this.products.add(orderProduct);
  }

  /** Calculate the total cost of the order. */
  public BigDecimal getCost() {
    return products.stream()
        .map(p -> new BigDecimal(p.getQuantity()).multiply(p.getPrice()))
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
