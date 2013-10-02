package sp.suggest;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Indexing facility for Suggest. Singleton, "Initialization on demand holder"
 *
 * @author Paul Kulitski
 */
@Lazy
@Component
public class SuggestIndex {

    private static final float LOAD_FACTOR = 0.75f;
    private static final int CONCURRENCY_LEVEL = 32;
    private static final int INITIAL_CATACITY = 1024;
    private boolean indexingInProcess = false;
    public Lock lock = new ReentrantLock();

    private static class SuggestIndexHolder {

        private static final ConcurrentHashMap index = new ConcurrentHashMap<String, Set>(
                INITIAL_CATACITY, LOAD_FACTOR, CONCURRENCY_LEVEL);
    }
    public static ConcurrentHashMap index = SuggestIndexHolder.index;

    protected ConcurrentHashMap getIndex() {
        return SuggestIndexHolder.index;
    }

    public void addToIndex(String key, Long docId) {
        Set ids = Collections.synchronizedSet(new HashSet());
        index.putIfAbsent(key, ids);
        Set entry = (Set) index.get(key);
        entry.add(docId);
    }

    public Set<String> getKeys(Long limit) {
        return Collections.unmodifiableSet(index.keySet());
    }

    public synchronized boolean inProgress() {
        try {
            lock.lock();
            return indexingInProcess;
        } finally {
            lock.unlock();
        }

    }

    public synchronized void setInProgress(boolean inProgress) {
        indexingInProcess = inProgress;
    }
}
