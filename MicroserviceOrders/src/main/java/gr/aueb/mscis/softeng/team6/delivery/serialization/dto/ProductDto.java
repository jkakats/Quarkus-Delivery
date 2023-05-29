package gr.aueb.mscis.softeng.team6.delivery.serialization.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * A DTO for the Product entity.
 *
 * @since 1.0.0
 */
public record ProductDto(
    Long id, @NotNull @NotBlank String name, @NotNull BigDecimal price, String comment)
    implements Serializable {}
