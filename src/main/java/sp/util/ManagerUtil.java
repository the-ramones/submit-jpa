package sp.util;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component; 

/**
 *  Utility component to be used for application system properties
 * 
 * @author Paul Kulitski
 */
@Component
@PropertySource("classpath:email.config.properties, report-servlet.properties, report.properties ")
public class ManagerUtil {

    @Autowired
    Environment env;

    public Properties getEmailProperties() {
        return new Properties();
    }

    public Properties getBackEndProperties() {
        return new Properties();
    }

    public Properties getFrontEndProperties() {
        return new Properties();
    }

    public Properties getTxProperties() {
        return new Properties();
    }

    public Properties getCacheProperties() {
        return new Properties();
    }

    public Properties getJpaProperties() {
        return new Properties();
    }
}
