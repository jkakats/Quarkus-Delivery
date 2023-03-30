package gr.aueb.mscis.softeng.team6.delivery.serialization.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * A DTO for the {@link gr.aueb.mscis.softeng.team6.delivery.domain.Store Store} entity.
 *
 * @since 1.0.0
 */
public record StoreDto(
    Long id,
    @NotNull @NotBlank String name,
    @NotNull @NotBlank String type,
    Set<AreaDto> areas,
    List<ProductDto> products)
    implements Serializable {}
