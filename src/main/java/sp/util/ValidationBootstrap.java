package sp.util;

import javax.validation.Configuration;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.cfg.ConstraintMapping;

/**
 * Programmatic validation bootstrap
 *
 * @author Paul Kulitski
 */
public class ValidationBootstrap {

    public static void main(String[] args) {
        Configuration configuration = Validation.byProvider(HibernateValidator.class).configure();
        ValidatorFactory factory = configuration.buildValidatorFactory();
        Validator validator = factory.getValidator();
        validator.validate(Object.class);
        // programmation constraint mappings
        ConstraintMapping mapping = new ConstraintMapping();
    }
}
