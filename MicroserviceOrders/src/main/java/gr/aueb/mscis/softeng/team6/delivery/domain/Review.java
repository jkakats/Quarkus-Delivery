package gr.aueb.mscis.softeng.team6.delivery.domain;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Abstract review entity.
 *
 * @since 0.1.0
 */
@MappedSuperclass
abstract class Review {
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

  public Short getRating() {
    return rating;
  }

  public abstract Review setRating(Short rating);

  @Override
  public abstract String toString();
}
