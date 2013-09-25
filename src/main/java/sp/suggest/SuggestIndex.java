package sp.suggest;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Indexing facility for Suggest. Singleton, "Initialization on demand holder"
 *
 * @author Paul Kulitski
 */
public class SuggestIndex {

    private static final float LOAD_FACTOR = 0.75f;
    private static final int CONCURRENCY_LEVEL = 32;
    private static final int INITIAL_CATACITY = 1024;

    private static class SuggestIndexHolder {

        private static final Map index = new ConcurrentHashMap<String, Set>(
                INITIAL_CATACITY, LOAD_FACTOR, CONCURRENCY_LEVEL);
    }

    public Map getIndex() {
        return SuggestIndexHolder.index;
    }
}
