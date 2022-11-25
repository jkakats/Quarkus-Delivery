package gr.aueb.mscis.softeng.team6.delivery.domain;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.NaturalId;

/**
 * Product entity.
 *
 * @since 0.1.0
 */
@Entity
@Table(name = "product")
public class Product implements Serializable {
  /** Auto-generated ID field. */
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  /** Name field. */
  @NotNull
  @NotBlank
  @NaturalId
  @Column(unique = true)
  private String name;

  /** Price field. */
  @NotNull
  @Column(precision = 8, scale = 2)
  private BigDecimal price;

  /** Optional comment field. */
  @Column(length = 2000)
  private String comment;

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

  @Override
  public boolean equals(Object that) {
    return this == that || (that instanceof Product other && name.equals(other.name));
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public String toString() {
    return String.format("Product{name=\"%s\", price=%.2f, comment=\"%s\"}", name, price, comment);
  }
}
