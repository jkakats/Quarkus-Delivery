package gr.aueb.mscis.softeng.team6.delivery.domain;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

/**
 * Store entity.
 *
 * @since 0.1.0
 */
@Entity
@Table(name = "store")
@NamedQueries({
  @NamedQuery(
      name = "findStoresByArea",
      query = "from Store s where :area in elements(s.areas)",
      readOnly = true)
})
public class Store implements Serializable {
  /** Auto-generated ID field. */
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  /** Name field. */
  @NotNull
  @NotBlank
  @Column(unique = true)
  private String name;

  /** Type field. */
  @NotNull
  @NotBlank
  @Column(length = 100)
  private String type;

  /** Areas relation field. */
  @ElementCollection
  @CollectionTable(
      name = "store_area",
      joinColumns = {@JoinColumn(name = "store_id")},
      uniqueConstraints = {@UniqueConstraint(columnNames = {"store_id", "zip_code"})})
  private Set<Area> areas = new HashSet<>();

  /** Products relation field. */
  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "store_product",
      joinColumns = {@JoinColumn(name = "store_id")},
      inverseJoinColumns = {@JoinColumn(name = "product_id")})
  private Set<Product> products = new HashSet<>();

  /** Orders relation field. */
  @OneToMany(mappedBy = "store")
  private List<Order> orders;

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

  public Set<Product> getProducts() {
    return products;
  }

  public Store setProducts(Set<Product> products) {
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
