package sp.model.dwr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Backend system's state holder
 *
 * @author Paul Kulitski
 */
@Lazy
@Component
@Scope(value = "singleton")
@PropertySource("classpath:report.properties")
public class BackendState extends GenericState {

    @Autowired
    Environment env;
}
