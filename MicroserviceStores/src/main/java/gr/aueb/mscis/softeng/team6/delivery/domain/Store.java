package gr.aueb.mscis.softeng.team6.delivery.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Store entity.
 *
 * @since 0.1.0
 * @version 1.0.0
 */
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
  // language=H2 prefix="call cast(null as " suffix=")"
  @Column(unique = true, columnDefinition = "varchar_ignorecase(255)")
  private String name;

  /** Type field. */
  @NotNull
  @NotBlank
  // language=H2 prefix="call cast(null as " suffix=")"
  @Column(length = 100, columnDefinition = "varchar_ignorecase(100)")
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

  public Long getId() {
    return id;
  }

  public Store setId(Long id) {
    this.id = id;
    return this;
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
