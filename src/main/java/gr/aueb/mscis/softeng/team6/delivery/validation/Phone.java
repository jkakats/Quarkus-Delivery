package gr.aueb.mscis.softeng.team6.delivery.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Phone number constraint annotation.
 *
 * @since 0.1.0
 */
@Documented
@Target({FIELD, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
@SuppressWarnings({"MissingJavadocMethod"})
public @interface Phone {
  String message() default "must be a valid phone number";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
