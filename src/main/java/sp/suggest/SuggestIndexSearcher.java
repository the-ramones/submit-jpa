package sp.suggest;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Suggest searcher. Not thread-safe.
 *
 * @author Paul Kulitski
 */
@Component
@Scope("prototype")
public class SuggestIndexSearcher implements IndexSearcher, IndexSuggester {

    protected static final Logger logger = LoggerFactory.getLogger(SuggestIndexSearcher.class);
    @Inject
    SuggestIndex suggestIndex;
    private final static int MAX_RESULT_AMOUNT = 10000;

    private synchronized String normalizeQuery(String query){
        String nQuery = query.trim().toLowerCase();
        try {
            nQuery = new String(nQuery.getBytes("utf-8"),"utf-8");
        } catch (UnsupportedEncodingException ex) {
            logger.warn("Cannot convert to specified encoding: {}", nQuery);
        }
        return nQuery;
    }

    @Override
    public synchronized List<Long> search(String query) {
        return search(query, MAX_RESULT_AMOUNT);
    }

    @Override
    public synchronized List<String> suggest(String query) {
        return suggest(query, MAX_RESULT_AMOUNT);
    }

    private static final String ALLOWED_CHARS_REGEXP = "[ 0-9a-zа-я-#@%&\\$]*";
    private static final String PATTERN_ENCODING = "utf-8";
    
    @Override
    public synchronized List<Long> search(String query, int limit) {
        String nQuery = normalizeQuery(query);
        Set<String> keys = suggestIndex.getKeys();
        Map index = suggestIndex.getIndex();
        List<Long> result = new ArrayList();
        int count = 0;

        logger.debug("IN SEARCH: query={}, keys={}",
                query, keys);

        StringBuilder patternBuilder;
        List<Pattern> patterns = new ArrayList<Pattern>();
        for (String keyPart : nQuery.split(" ")) {
            patternBuilder = new StringBuilder("(?iu)");
            try {
                patternBuilder.append(ALLOWED_CHARS_REGEXP)
                        .append(new String(keyPart.getBytes("ISO-8859-1"), "utf-8"))
                        .append(ALLOWED_CHARS_REGEXP);
            } catch (UnsupportedEncodingException ex) {
                logger.warn("Cannot construct regexp with the given encoding: {}");
            }
            patterns.add(Pattern.compile(patternBuilder.toString()));
        }

        for (String key : keys) {
            for (Pattern pattern : patterns) {
                logger.debug("MATCH {} : {}", key, pattern.matcher(key).matches());
                if (pattern.matcher(key).matches()) {
                    List<Long> partResults = (List<Long>) index.get(key);
                    if ((count + partResults.size()) >= limit) {
                        result.addAll(partResults.subList(0, limit - count - 1));
                        break;
                    } else {
                        count = count + partResults.size();
                        result.addAll(partResults);
                    }
                }
            }
        }

        return result;
    }

    @Override
    public synchronized List<String> suggest(String query, int limit) {
        String nQuery = normalizeQuery(query);
        int count = 0;
        StringBuilder sb = new StringBuilder(nQuery.length());
        String allowedChars = "[ 0-9a-zа-я-#@%&\\$]{1,255}";
        sb.append("^(?iu)").append(allowedChars).append(nQuery).append(allowedChars).append('$');
        Pattern pattern = Pattern.compile(sb.toString());
        Set<String> keys = suggestIndex.getKeys();
        Iterator<String> keysIt = keys.iterator();
        String key;

        logger.debug("IN SUGGEST: query={}, keys={}",
                query, keys);

        List<String> result = new ArrayList();
        while (keysIt.hasNext()) {
            key = keysIt.next();
            if (pattern.matcher(key).matches()) {
                count++;
                result.add(key);
                if (count >= limit) {
                    break;
                }
            }
        }
        return result;
    }
    private final int TRANC_AMOUNT = 3;

    @Override
    public synchronized Long count(String query) {
        long count = 0L;
        String nQuery = normalizeQuery(query);
        Set<String> keys = suggestIndex.getKeys();
        Map index = suggestIndex.getIndex();
        StringBuilder sb = new StringBuilder(nQuery);
        List<Long> result = new ArrayList();
        String attempt;
        int length = sb.length();
        int bound = 0;
        if (length > 3) {
            bound = length - TRANC_AMOUNT;
        }

        logger.debug("IN COUNTER SEARCH: query={}, keys={}",
                query, keys);

        for (int i = 0; i < length - bound; i++) {
            attempt = sb.substring(0, length - i - 1);
            if (keys.contains(attempt)) {
                List<Long> resultList = (List) index.get(attempt);
                if (resultList != null) {
                    count += resultList.size();
                }
            }
        }
        return Long.valueOf(count);
    }
}
