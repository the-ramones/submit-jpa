package sp.util;

import ch.qos.logback.classic.LoggerContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.LoggerFactory;

/**
 * Reports! servlet context loader listener implementation
 *
 * @author Paul Kulitski
 */
public class SpContextLoaderListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        /*
         * Clean up of Logback resources
         */
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.stop();
    }
}
