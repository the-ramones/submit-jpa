package sp.suggest;

import java.util.List;

/**
 * Interface for suggester
 *
 * @author Paul Kulitski
 */
public interface IndexSuggester {

    List<String> suggest(String query);

    List<String> suggest(String query, int limit);
}
