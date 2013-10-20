package sp.util.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Loads properties at startup and checks for update every minute   
 * 
 * @author Paul Kulitski
 */
@Component
@DependsOn(value = {"frontendProperties", "backendProperties"})
public class PropertiesReloader implements Reloader {

    private static final Logger logger = LoggerFactory.getLogger(Properties.class);

    @PostConstruct
    public void initMaps() {
        load();
    }

    @Override
    public void reload() {
        //TODO: reload properties stub
    }
    @Inject
    @Named("frontendProperties")
    HashMap frontendMap;
    @Inject
    @Named("backendProperties")
    HashMap backendMap;

    /*
     * TODO: race condition check?
     */
    private synchronized void load() {
        ClassPathResource bcl = new ClassPathResource("report.properties");
        ClassPathResource fcl = new ClassPathResource("report-servlet.properties");
        Properties backendProperties = new Properties();
        Properties frontendProperties = new Properties();
        try {
            backendProperties.load(bcl.getInputStream());
            frontendProperties.load(fcl.getInputStream());
            synchronized (backendMap) {
                for (String key : backendProperties.stringPropertyNames()) {
                    backendMap.put(key, backendProperties.get(key));
                }
            }
            synchronized (frontendMap) {
                for (String key : frontendProperties.stringPropertyNames()) {
                    frontendMap.put(key, frontendProperties.getProperty(key));
                }
            }
        } catch (IOException ex) {
            logger.error("Cannot load properties from file. It can be cause of application corruption", ex);
        }
    }
}
