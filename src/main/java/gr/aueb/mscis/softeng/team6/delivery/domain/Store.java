package gr.aueb.mscis.softeng.team6.delivery.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

/**
 * Store entity.
 *
 * @since 0.1.0
 * @version 1.0.0
 */
@SqlResultSetMapping(
    name = "ScalarResult",
    columns = {@ColumnResult(name = "result")})
@NamedQueries({
  @NamedQuery(
      name = "findNearbyStores",
      query =
          """
        from Store s
        join s.areas a
        where :area = a.zipCode
          and :count = (
            select
              count(distinct p.id)
            from s.products p
            where p.id in :products)
        """,
      readOnly = true),
})
// NOTE: these queries have issues in HQL
@NamedNativeQueries({
  @NamedNativeQuery(
      name = "getAverageDeliveryTime",
      query =
          """
        SELECT
          ROUND(AVG(DATEDIFF(MINUTE, `ordered_time`, `delivered_time`))) `result`
        FROM `order` o
        JOIN `store_area` a
          ON o.`store_id` = a.`store_id`
        WHERE o.`delivered` = true
          AND a.`zip_code` = :area
          AND o.`store_id` = :store
        """,
      resultSetMapping = "ScalarResult",
      readOnly = true,
      fetchSize = 1),
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
      resultSetMapping = "ScalarResult",
      readOnly = true)
})
@Entity
@Table(
    name = "store",
    indexes = {@Index(columnList = "type")})
public class Store {
  /** Auto-generated ID field. */
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  /** Name field. */
  @NotNull
  @NotBlank
  @Column(unique = true, columnDefinition = "varchar_ignorecase(255) not null")
  private String name;

  /** Type field. */
  @NotNull
  @NotBlank
  @Column(length = 100, columnDefinition = "varchar_ignorecase(100) not null")
  private String type;

  /** Areas relation field. */
  @ElementCollection
  @CollectionTable(
      name = "store_area",
      joinColumns = {@JoinColumn(name = "store_id")},
      uniqueConstraints = {@UniqueConstraint(columnNames = {"store_id", "zip_code"})})
  private Set<Area> areas = new HashSet<>();

  /** Products relation field. */
  @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
  private List<Product> products = new ArrayList<>();

  /** Orders relation field. */
  @OneToMany(mappedBy = "store")
  private List<Order> orders = new ArrayList<>();

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Store setName(String name) {
    this.name = name;
    return this;
  }

  public String getType() {
    return type;
  }

  public Store setType(String type) {
    this.type = type;
    return this;
  }

  public Set<Area> getAreas() {
    return areas;
  }

  public Store setAreas(Set<Area> areas) {
    this.areas = areas;
    return this;
  }

  public List<Product> getProducts() {
    return products;
  }

  public Store setProducts(List<Product> products) {
    this.products = products;
    return this;
  }

  public List<Order> getOrders() {
    return orders;
  }

  /**
   * Add an area to the store.
   *
   * @param area the area to be added.
   */
  public void addArea(Area area) {
    areas.add(area);
  }

  /**
   * Add a product to the store.
   *
   * @param product the product to be added.
   */
  public void addProduct(Product product) {
    products.add(product);
  }

  @Override
  public boolean equals(Object that) {
    return this == that || (that instanceof Store other && name.equals(other.name));
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public String toString() {
    return String.format("Store{name=\"%s\", type=\"%s\"}", name, type);
  }
}
