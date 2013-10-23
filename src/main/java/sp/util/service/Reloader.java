package sp.util.service;

import org.springframework.scheduling.annotation.Scheduled;

/**
 * Interface for reload task
 *
 * @author Paul Kulitski
 */
public interface Reloader {
    
    @Scheduled(fixedRate = 60000)
    public void reload();
}
