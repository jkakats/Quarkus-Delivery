package gr.aueb.mscis.softeng.team6.delivery.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

/**
 * Product entity.
 *
 * @since 0.1.0
 * @version 1.0.0
 */
@Entity
@Table(name = "product")
public class Product implements Serializable {
  /** Auto-generated ID field. */
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  /** Name field. */
  @NotNull @NotBlank private String name;

  /** Price field. */
  @NotNull
  @Column(precision = 8, scale = 2)
  private BigDecimal price;

  /** Optional comment field. */
  @Column(length = 2000)
  private String comment;

  /** Stores relation field. */
  @ManyToMany(mappedBy = "products")
  private List<Store> stores = new ArrayList<>();

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

  public List<Store> getStores() {
    return stores;
  }

  @Override
  public String toString() {
    return String.format("Product{name=\"%s\", price=%.2f, comment=\"%s\"}", name, price, comment);
  }
}
