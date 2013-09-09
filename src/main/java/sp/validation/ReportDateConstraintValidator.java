package sp.validation;

import java.util.Calendar;
import java.util.Date;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates dates 
 *
 * @author the-ramones
 */
public class ReportDateConstraintValidator implements ConstraintValidator<ReportDate, Date> {

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
            if (calendar.isLenient()) {
                return false;
            } 
        }
        return true;
    }
}
