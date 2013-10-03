package sp.suggest;

import java.util.List;
import javax.inject.Inject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Suggest searcher
 *
 * @author Paul Kulitski
 */
@Component
@Scope("prototype")
public class SuggestIndexSearcher {

    @Inject
    SuggestIndex suggestIndex;

    public List<Long> search(String query) {

        return null;
    }

    public List<String> suggest(String query) {

        return null;
    }

    public List<Long> search(String query, int limit) {

        return null;
    }

    public List<String> suggest(String query, int limit) {

        return null;
    }
}
