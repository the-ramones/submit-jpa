package sp.suggest;

import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Interface for index. Use it from {@link IndexSearcher} and
 * {@link IndexSuggester}
 *
 * @author Paul Kulitski
 */
public interface Index {

    void addToIndex(String key, Long docId);

    ConcurrentHashMap<String, LinkedList> getIndex();

    /**
     * Should not change elements
     *
     * @return
     */
    Set<String> getKeys();
}
