package sp.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Validates {@link sp.model.Report} object as a whole
 *
 * @author Paul Kulitski
 */
@Documented
@Constraint(validatedBy = ValidReportConstraintValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidReport {
    
    String message() default "{ValidReport}";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
