package sp.suggest;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Interface for index creator
 *
 * @author Paul Kulitski
 */
@Component
public interface IndexCreator {

    final int UPDATE_RATE_DAY = 24 * 60 * 1000;
    final int UPDATE_RATE_HOURLY = 1 * 60 * 1000;

    @Scheduled(fixedRate = UPDATE_RATE_DAY)
    void updateIndex();

    @Scheduled(fixedRate = UPDATE_RATE_HOURLY)
    public void reloadIndex();
}
