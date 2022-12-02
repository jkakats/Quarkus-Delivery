package gr.aueb.mscis.softeng.team6.delivery.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Phone number constraint annotation.
 *
 * @since 0.1.0
 */
@Documented
@Target({FIELD, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
public @interface Phone {
  /** The violation message of the constraint. */
  String message() default "must be a valid phone number";

  /**
   * The <i>unused</i> validation groups of the constraint.
   *
   * @hidden
   */
  Class<?>[] groups() default {};

  /**
   * The <i>unused</i> payload of the constraint.
   *
   * @hidden
   */
  Class<? extends Payload>[] payload() default {};
}
