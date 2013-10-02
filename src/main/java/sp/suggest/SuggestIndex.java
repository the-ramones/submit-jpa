package sp.suggest;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Exchanger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Indexing facility for Suggest. Singleton, "Initialization on demand holder"
 *
 * @author Paul Kulitski
 */
@Component
public class SuggestIndex {

    /*
     * index properties
     */
    private static final float LOAD_FACTOR = 0.75f;
    private static final int CONCURRENCY_LEVEL = 32;
    private static final int INITIAL_CATACITY = 1024;
    /*
     * facility properties
     */
    Lock processLock = new ReentrantLock();
    Lock writeLock = new ReentrantLock();
    ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private boolean processed = false;

    /*
     * static Singleton holder
     */
    private static class SuggestIndexHolder {

        private static final ConcurrentHashMap index = new ConcurrentHashMap<String, LinkedList>(
                INITIAL_CATACITY, LOAD_FACTOR, CONCURRENCY_LEVEL);
    }
    public ConcurrentHashMap<String, LinkedList> index = SuggestIndexHolder.index;

    public ConcurrentHashMap<String, LinkedList> getIndex() {
        return SuggestIndexHolder.index;
    }

    public void addToIndex(String key, Long docId) {
        try {
            writeLock.lockInterruptibly();

            LinkedList ids = new LinkedList<Long>();
            index.putIfAbsent(key, ids);
            if (index.containsKey(key)) {
                LinkedList entry = (LinkedList) index.get(key);
                entry.add(docId);
            }

        } catch (InterruptedException ex) {
            logger.warn("Cannot acquire write lock on the index");
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Should not change elements
     *
     * @param limit
     * @return
     */
    public Set<String> getKeys() {
        return index.keySet();
    }

    /**
     * Should not change elements
     *
     * @param key
     * @return
     */
    public List getValues(String key) {
        return index.get(key);
    }
    protected static final Logger logger = LoggerFactory.getLogger(SuggestIndex.class);

    public synchronized void setProcessing(boolean state) {
        processed = state;
    }

    public synchronized boolean isProcessed() {
        return processed;
    }

    public synchronized void process() {
        try {
            processLock.lockInterruptibly();
            processed = true;

        } catch (InterruptedException ex) {
            logger.warn("Thread has been interrupted while attempting to acquire"
                    + " a lock object for index updating", ex);
        } finally {
            processLock.unlock();
            processed = false;
        }
    }
    
    private ConcurrentHashMap<String, Set> swapIndex = new ConcurrentHashMap<String, Set>(
            INITIAL_CATACITY, LOAD_FACTOR, CONCURRENCY_LEVEL);
    private Exchanger indexSwitcher = new Exchanger();

    public synchronized void switchIndex() {
        //indexSwitcher.
    }
}
