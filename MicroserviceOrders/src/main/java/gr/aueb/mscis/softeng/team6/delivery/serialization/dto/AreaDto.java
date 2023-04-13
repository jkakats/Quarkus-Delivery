package gr.aueb.mscis.softeng.team6.delivery.serialization.dto;

import java.io.Serializable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * A DTO for the Area entity.
 *
 * @since 1.0.0
 */
public record AreaDto(
    @Min(10000) @Max(89999) Integer zipCode,
    @NotNull @NotBlank String city,
    @NotNull @NotBlank String state)
    implements Serializable {
  /*@Override
  public String toString() {
    return String.format("%s %d, %s", city, zipCode, state);
  }*/
}
