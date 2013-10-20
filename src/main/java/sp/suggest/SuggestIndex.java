package sp.suggest;

import java.util.LinkedHashSet;
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
     * Concurrency variables
     */
    Lock processLock = new ReentrantLock();
    Lock writeLock = new ReentrantLock();
    ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private boolean processed = false;
    /*
     * Logger 
     */
    protected static final Logger logger = LoggerFactory.getLogger(SuggestIndex.class);
    /*
     * Indexes
     */
    private ConcurrentHashMap<String, LinkedHashSet> index;
    private ConcurrentHashMap<String, LinkedHashSet> swapIndex;

    @Override
    public ConcurrentHashMap<String, LinkedHashSet> getIndex() {
        return index;
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
    public LinkedHashSet<Long> getValues(String key) {
        return index.get(key);
    }

    public ConcurrentHashMap<String, LinkedHashSet> getSwapIndex() {
        try {
            processLock.lock();
            if (swapIndex != null) {
                return swapIndex;
            } else {
                logger.warn("CREATING A NEW SWAP INDEX");
                swapIndex = new ConcurrentHashMap<String, LinkedHashSet>(
                        INITIAL_CATACITY, LOAD_FACTOR, CONCURRENCY_LEVEL);
                return swapIndex;
            }
        } finally {
            processLock.unlock();
        }

    }

    @Override
    public void addToIndex(String key, Long docId) {
        try {
            writeLock.lockInterruptibly();

            LinkedHashSet ids = new LinkedHashSet<Long>();
            index.putIfAbsent(key, ids);
            if (index.containsKey(key)) {
                LinkedHashSet entry = (LinkedHashSet) index.get(key);
                entry.add(docId);

                logger.error("ADDING to INDEX: {} : {}", key, docId);
            } else {
                index.put(key, ids);
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

            ConcurrentHashMap<String, LinkedHashSet> tempSwapIndex = getSwapIndex();
            LinkedHashSet ids = new LinkedHashSet<Long>();
            tempSwapIndex.putIfAbsent(key, ids);
            if (tempSwapIndex.containsKey(key)) {
                LinkedHashSet entry = (LinkedHashSet) tempSwapIndex.get(key);      
                //todo: BUG HERE?
                entry.add(docId);
            } else {
                //index.put(key, ids);
            }

        } catch (InterruptedException ex) {
            logger.warn("Cannot acquire write lock on the index", ex);
        } finally {
            processLock.unlock();
        }
    }

    public void switchIndexes() {
        if (processed) {
            if (swapIndex != null) {
                index = swapIndex;
                /*
                 * Cleaning-up resources
                 */
                swapIndex = null;
            } else {
                logger.warn("Cannot switch indexes because Swap Index is null.");
            }
        }
    }

    public synchronized void setProcessing(boolean state) {
        processed = state;
    }

    public synchronized boolean isProcessed() {
        return processed;
    }

    public Lock getProcessLock() {
        return processLock;
    }

    public synchronized void process(ConcurrentHashMap<String, LinkedList> newIndex) {
        try {
            processLock.lockInterruptibly();
            processed = true;
            //TODO:actions here
        } catch (InterruptedException ex) {
            logger.warn("Thread has been interrupted while attempting to acquire"
                    + " a lock object for index updating", ex);
        } finally {
            processLock.unlock();
            processed = false;
        }
    }
}
