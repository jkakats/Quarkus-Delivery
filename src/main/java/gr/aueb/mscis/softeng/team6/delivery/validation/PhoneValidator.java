package gr.aueb.mscis.softeng.team6.delivery.validation;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Phone number validator.
 *
 * @since 0.1.0
 * @see PhoneNumberUtil#isValidNumberForRegion
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {
  /** The default phone number region. */
  private static final String REGION = "GR";

  private PhoneNumberUtil util;

  @Override
  public void initialize(Phone phoneAnnotation) {
    util = PhoneNumberUtil.getInstance();
  }

  @Override
  public boolean isValid(String number, ConstraintValidatorContext ctx) {
    try {
      final var phone = util.parse(number, REGION);
      return util.isValidNumberForRegion(phone, REGION);
    } catch (NumberParseException e) {
      return false;
    }
  }
}
