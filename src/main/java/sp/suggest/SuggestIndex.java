package sp.suggest;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * Indexing facility for Suggest. Singleton, "Initialization on demand holder"
 *
 * @author Paul Kulitski
 */
@Component
public class SuggestIndex {

    private static final float LOAD_FACTOR = 0.75f;
    private static final int CONCURRENCY_LEVEL = 32;
    private static final int INITIAL_CATACITY = 1024;

    private static class SuggestIndexHolder {

        private static final ConcurrentHashMap index = new ConcurrentHashMap<String, Set>(
                INITIAL_CATACITY, LOAD_FACTOR, CONCURRENCY_LEVEL);
    }
    public static Map index = SuggestIndexHolder.index;

    public Map getIndex() {
        return SuggestIndexHolder.index;
    }

    public void addToIndex(String key, Long docId) {
        if (!index.containsKey(key)) {
            Set ids = new HashSet();
            index.put(key, ids);
        } else {
            ((Set) index.get(key)).add(docId);
        }
        
    }

    public Set<String> getKeys(Long limit) {
        return Collections.unmodifiableSet(index.keySet());
    }
}
