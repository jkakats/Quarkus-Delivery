package gr.aueb.mscis.softeng.team6.delivery.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Product entity.
 *
 * @since 0.1.0
 * @version 1.0.0
 */
@Entity
@Table(
    name = "product",
    indexes = {@Index(columnList = "name")},
    uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "store_id"})})
public class Product {
  /** Auto-generated ID field. */
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  /** Name field. */
  @NotNull
  @NotBlank
  @Column(columnDefinition = "varchar_ignorecase(255) not null")
  private String name;

  /** Price field. */
  @NotNull
  @Column(precision = 8, scale = 2)
  private BigDecimal price;

  /** Optional comment field. */
  @Column(length = 2000)
  private String comment;

  /** Store relation field. */
  @ManyToOne(fetch = FetchType.LAZY)
  private Store store;

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Product setName(String name) {
    this.name = name;
    return this;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public Product setPrice(BigDecimal price) {
    this.price = price;
    return this;
  }

  public String getComment() {
    return comment;
  }

  public Product setComment(String comment) {
    this.comment = comment;
    return this;
  }

  public Store getStore() {
    return store;
  }

  public Product setStore(Store store) {
    this.store = store;
    return this;
  }

  @Override
  public String toString() {
    return String.format("Product{name=\"%s\", price=%.2f, comment=\"%s\"}", name, price, comment);
  }
}
