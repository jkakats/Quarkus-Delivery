package gr.aueb.mscis.softeng.team6.delivery.serialization.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * A DTO for the {@link gr.aueb.mscis.softeng.team6.delivery.domain.OrderProduct OrderProduct}
 * entity.
 *
 * @since 1.0.0
 */
public record OrderProductDto(
    Long product_id, BigDecimal price, @NotNull @Min(1) Integer quantity, ProductReviewDto review)
    implements Serializable {}
