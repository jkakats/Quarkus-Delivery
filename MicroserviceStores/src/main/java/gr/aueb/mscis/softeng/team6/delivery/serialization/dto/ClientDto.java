package gr.aueb.mscis.softeng.team6.delivery.serialization.dto;

import gr.aueb.mscis.softeng.team6.delivery.validation.Phone;
import java.io.Serializable;
import java.util.UUID;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

/**
 * A DTO for the {@link gr.aueb.mscis.softeng.team6.delivery.domain.Client Client} entity.
 *
 * @since 1.0.0
 */
public record ClientDto(
    UUID uuid,
    @NotNull @Length(min = 1, max = 20) @Pattern(regexp = "[a-zA-Z0-9_-]+") String username,
    @NotNull @Length(min = 8) String password,
    @NotNull @NotBlank String name,
    @Email @NotNull @NotBlank String email,
    @NotNull @NotBlank @Phone String phoneNumber,
    AddressDto address)
    implements Serializable {}
