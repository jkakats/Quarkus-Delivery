package gr.aueb.mscis.softeng.team6.delivery.serialization.dto;

import java.io.Serializable;

/**
 * A DTO for the {@link gr.aueb.mscis.softeng.team6.delivery.domain.Address Address} entity.
 *
 * @since 1.0.0
 */
public record AddressDto(String street, String apartment, AreaDto area) implements Serializable {}
