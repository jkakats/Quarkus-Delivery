package gr.aueb.mscis.softeng.team6.delivery.domain;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

/**
 * Product entity.
 *
 * @since 0.1.0
 */
@NamedQueries({
  @NamedQuery(name = "getAllProducts", query = "from Product join fetch Store", readOnly = true)
})
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
  private List<Store> stores;

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
