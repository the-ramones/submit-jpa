package sp.util;

import ch.qos.logback.classic.LoggerContext;
import java.io.File;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reports! servlet context loader listener implementation
 *
 * @author Paul Kulitski
 */
public class SpContextLoaderListener implements ServletContextListener {

    private static final String REPORTS_FILE_DIRECTORY = "files";
    private static Logger logger = LoggerFactory.getLogger(SpContextLoaderListener.class);
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        /*
         * Create directory for Reports! files
         */
        File filesDirectory = new File(REPORTS_FILE_DIRECTORY);
        if (!filesDirectory.exists()) {
            if (!filesDirectory.mkdirs()) {
                logger.warn("Cannot create Reports! files directory");
            }
        }
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
