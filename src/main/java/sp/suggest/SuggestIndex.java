package sp.suggest;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Indexing facility for Suggest. Singleton, "Initialization on demand holder"
 *
 * @author Paul Kulitski
 */
@Lazy
@Component
public class SuggestIndex implements Index {

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
     * Loggers 
     */
    protected static final Logger logger = LoggerFactory.getLogger(SuggestIndex.class);

    /*
     * static Singleton holder
     */
    private static class SuggestIndexHolder {

        private static final ConcurrentHashMap index = new ConcurrentHashMap<String, LinkedList>(
                INITIAL_CATACITY, LOAD_FACTOR, CONCURRENCY_LEVEL);
    }
    public ConcurrentHashMap<String, LinkedList> index = SuggestIndexHolder.index;
    private static WeakReference<ConcurrentHashMap<String, LinkedList>> swapIndexRef;

    @Override
    public ConcurrentHashMap<String, LinkedList> getIndex() {
        return SuggestIndexHolder.index;
    }

    @Override
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

    public void addToSwapIndex(String key, Long docId) {
        try {
            processLock.lockInterruptibly();
            ConcurrentHashMap<String, LinkedList> swapIndex = swapIndexRef.get();
            LinkedList ids = new LinkedList<Long>();
            swapIndex.putIfAbsent(key, ids);
            if (swapIndex.containsKey(key)) {
                LinkedList entry = (LinkedList) swapIndex.get(key);
                entry.add(docId);
            }

        } catch (InterruptedException ex) {
            logger.warn("Cannot acquire write lock on the index");
        } finally {
            processLock.unlock();
        }
    }

    /**
     * Should not change elements
     *
     * @param limit
     * @return
     */
    @Override
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

    public synchronized void setProcessing(boolean state) {
        processed = state;
    }

    public synchronized boolean isProcessed() {
        return processed;
    }

    public synchronized void process(ConcurrentHashMap<String, LinkedList> newIndex) {
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

    public ConcurrentHashMap<String, LinkedList> getSwapIndex() {
        try {
            processLock.lock();
            if (swapIndexRef != null) {
                ConcurrentHashMap<String, LinkedList> swapIndex = swapIndexRef.get();
                if (swapIndex != null) {
                    swapIndex.clear();
                    return swapIndex;
                }
            }
            return new ConcurrentHashMap<String, LinkedList>(
                    INITIAL_CATACITY, LOAD_FACTOR, CONCURRENCY_LEVEL);
        } finally {
            processLock.unlock();
        }

    }

    public Lock getProcessLock() {
        return processLock;
    }

    public void switchIndexes() {
        if (processed) {
            ConcurrentHashMap<String, LinkedList> swap = index;
            index = swapIndexRef.get();
            swapIndexRef = new WeakReference<ConcurrentHashMap<String, LinkedList>>(swap);
        }
    }
}
