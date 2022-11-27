package gr.aueb.mscis.softeng.team6.delivery.domain;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Abstract review entity.
 *
 * @since 0.1.0
 */
@MappedSuperclass
abstract class Review implements Serializable {
  /** Auto-generated ID field. */
  @Id
  @GeneratedValue(strategy = IDENTITY)
  protected Long id;

  /** Rating field. */
  @NotNull
  @Min(0)
  @Max(5)
  @Column(name = "rating")
  protected Short rating;

  public Long getId() {
    return id;
  }

  public short getRating() {
    return rating;
  }

  public Review setRating(Short rating) {
    this.rating = rating;
    return this;
  }

  @Override
  public abstract String toString();
}
