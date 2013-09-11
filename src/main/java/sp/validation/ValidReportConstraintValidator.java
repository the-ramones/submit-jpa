package sp.validation;

import java.util.Date;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import sp.model.Report;

/**
 * {@link sp.validation.ConstraintValidator} implementation for
 * {@link sp.validation.ValidReport} constraint for validation an object's
 * fields correlation
 *
 * @author Paul Kulitski
 */
public class ValidReportConstraintValidator implements ConstraintValidator<ValidReport, Report> {

    private static final String END_DATE_VIOLATION_TEMPLATE = "report is not valid: end date must be equal or greater than start date";

    @Override
    public void initialize(ValidReport a) {
    }

    /**
     * Validate: 'end date of activity must equal or greater than start date'
     *
     * @param report an object being validated
     * @param context a validation context
     * @return true if the passed object valid; false - otherwise
     */
    @Override
    public boolean isValid(Report report, ConstraintValidatorContext context) {
        Date startDate = report.getStartDate();
        Date endDate = report.getEndDate();
        if (endDate != null) {
            if (startDate.compareTo(endDate) >= 1) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(END_DATE_VIOLATION_TEMPLATE)
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
