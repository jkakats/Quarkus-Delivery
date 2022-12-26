package gr.aueb.mscis.softeng.team6.delivery.serialization.dto;

import java.io.Serializable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * A DTO for the {@link gr.aueb.mscis.softeng.team6.delivery.domain.OrderReview OrderReview} entity.
 *
 * @since 1.0.0
 */
public record OrderReviewDto(@NotNull @Min(0) @Max(5) Short rating, String comment)
    implements Serializable {}
