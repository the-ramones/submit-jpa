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

    final int UPDATE_RATE = 4 * 60 * 1000;

    @Scheduled(fixedRate = UPDATE_RATE)
    void updateIndex();
}
