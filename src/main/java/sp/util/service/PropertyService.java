package sp.util.service;

import java.util.HashMap;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * {@link Service} for dynamic application properties updates
 *
 * TODO: pull interface from here
 *
 * @author Paul Kulitski
 */
@Service
@PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_ADMIN')")
public class PropertyService {

    @Inject
    @Named("frontendProperties")
    HashMap frontendMap;
    @Inject
    @Named("backendProperties")
    HashMap backendMap;
    protected static final Logger logger = LoggerFactory.getLogger(PropertyService.class);

    public void updateFrontend(String key, String value) {
        update(frontendMap, key, value);
    }

    public void updateBackend(String key, String value) {
        update(backendMap, key, value);
    }

    public String getFrontend(String key) {
        return get(frontendMap, key);
    }

    public String getBackend(String key) {
        return get(backendMap, key);
    }

    private String get(HashMap map, String key) {
        synchronized (map) {
            if (map.containsKey(key)) {
                return (String) map.get(key);
            } else {
                return null;
            }
        }
    }

    private void update(HashMap map, String key, String value) {
        synchronized (map) {
            if (map.containsKey(key)) {
                map.put(key, value);
            } else {
                logger.warn("Unknown property passed: {}", key);
            }
        }
    }
}
