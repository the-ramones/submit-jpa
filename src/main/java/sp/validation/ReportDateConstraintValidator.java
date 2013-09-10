package sp.validation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Validates dates
 *
 * @author the-ramones
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
        System.out.println("Date: " + date);
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
            if (calendar.before(STARTUP)) {
                DateFormat df = new SimpleDateFormat("dd MMM yyyy", LocaleContextHolder.getLocale());
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "{ReportDate.outrange}").addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
