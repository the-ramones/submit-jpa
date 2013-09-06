package sp.validation;

import java.util.Calendar;
import java.util.Date;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for input dates
 *
 * @author the-ramones
 */
public class ReportDateConstraintValidator implements ConstraintValidator<ReportDate, Date> {

    private ReportDate reportDate;
    
    @Override
    public void initialize(ReportDate reportDate) {
        this.reportDate = reportDate;
    }

    @Override
    public boolean isValid(Date reportDate, ConstraintValidatorContext cvc) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(reportDate);        
        if (calendar.isLenient()) {
            return false;
        } 
        return true;
    }
}
