package sp.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Custom date constraint
 * 
 * @author the-ramones
 */
@Documented
@Constraint(validatedBy = ReportDateConstraintValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReportDate {
    
    String message() default "{ReportDate}";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
