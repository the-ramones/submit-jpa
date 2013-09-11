package sp.validation;

import java.util.Calendar;
import java.util.Date;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for {@link sp.validation.ReportDate}
 *
 * @author Paul Kulitski
 */
public class ReportDateConstraintValidator implements ConstraintValidator<ReportDate, Date> {

    final static Calendar STARTUP;

    /**
     * Set up a startup of the application. Valid date value should be after
     * that date
     */
    static {
        STARTUP = Calendar.getInstance();
        STARTUP.set(2000, 1, 1);
    }

    @Override
    public void initialize(ReportDate reportDate) {
    }

    /**
     * Validates: 'an entered date must be strict (not lenient)'
     *
     * @param date an entered date
     * @param context an validation context
     * @return true, if a date is valid; false - otherwise
     */
    @Override
    public boolean isValid(Date date, ConstraintValidatorContext context) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
            if (calendar.before(STARTUP)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "{ReportDate.outrange}").addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
