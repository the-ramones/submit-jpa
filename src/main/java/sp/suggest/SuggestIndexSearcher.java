package sp.suggest;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Suggest searcher. Thread-safe.
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
    private static final String ALLOWED_CHARS_REGEXP = "[ 0-9a-zа-я-#@%&\\$]*";
    private static final String PATTERN_ENCODING = "utf-8";

    @Override
    public synchronized List<Long> search(String query) {
        return search(query, MAX_RESULT_AMOUNT);
    }

    @Override
    public synchronized List<String> suggest(String query) {
        return suggest(query, MAX_RESULT_AMOUNT);
    }

    @Override
    public synchronized List<Long> search(String query, int limit) {
        String nQuery = normalizeQuery(query);
        Set<String> keys = suggestIndex.getKeys();
        Map index = suggestIndex.getIndex();
        LinkedHashSet<Long> result = new LinkedHashSet<Long>();
        int count = 0;

        logger.debug("IN SEARCH: query={}, keys={}", query, keys);

        StringBuilder patternBuilder;
        List<Pattern> patterns = new ArrayList<Pattern>();
        for (String keyPart : nQuery.split(" ")) {
            patternBuilder = new StringBuilder("^(?iu)");
            patternBuilder.append(ALLOWED_CHARS_REGEXP);
            keyPart = Normalizer.normalize(keyPart, Normalizer.Form.NFD);
            patternBuilder.append(keyPart)
                    .append(ALLOWED_CHARS_REGEXP)
                    .append('$');
            patterns.add(Pattern.compile(patternBuilder.toString()));
        }

        for (String key : keys) {
            for (Pattern pattern : patterns) {
                if (pattern.matcher(key).matches()) {
                    LinkedHashSet<Long> partResults =
                            (LinkedHashSet<Long>) index.get(key);
                    if ((count + partResults.size()) > limit) {
                        Iterator<Long> it = partResults.iterator();
                        for (int i = 0; i < limit - count; i++) {
                            if (it.hasNext()) {
                                result.add(it.next());
                            }
                        }
                        break;
                    } else {
                        count = count + partResults.size();
                        result.addAll(partResults);
                    }
                }
            }
        }
        Iterator<Long> itResult = result.iterator();
        List<Long> list = new ArrayList<Long>(result.size());
        while (itResult.hasNext()) {
            list.add(itResult.next());
        }
        return list;
    }

    @Override
    public synchronized List<String> suggest(String query, int limit) {
        String nQuery = normalizeQuery(query);
        int count = 0;
        StringBuilder sb = new StringBuilder(nQuery.length());
        sb.append("^(?iu)").append(ALLOWED_CHARS_REGEXP);
        nQuery = Normalizer.normalize(nQuery, Normalizer.Form.NFD);
        sb.append(nQuery)
                .append(ALLOWED_CHARS_REGEXP)
                .append('$');
        Pattern pattern = Pattern.compile(sb.toString());
        Set<String> keys = suggestIndex.getKeys();
        Iterator<String> keysIt = keys.iterator();
        String key;

        logger.debug("IN SUGGEST: query={}, keys={}", query, keys);

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

    /*
     *
     * Collator collator = Collator.getInstance();
     * Normalizer.(Normalizer.Form.NFC)
     * TOFIX:   only gets aprrox. upper bound (fix docId overlapping)
     */
    @Override
    public synchronized Long count(String query) {
        long count = 0L;
        String nQuery = normalizeQuery(query);
        Set<String> keys = suggestIndex.getKeys();
        Map index = suggestIndex.getIndex();
        LinkedHashSet<Long> result = new LinkedHashSet<Long>();

        logger.debug("IN SEARCH: query={}, keys={}", query, keys);

        StringBuilder patternBuilder;
        List<Pattern> patterns = new ArrayList<Pattern>();
        for (String keyPart : nQuery.split(" ")) {
            patternBuilder = new StringBuilder("^(?iu)");
            patternBuilder.append(ALLOWED_CHARS_REGEXP);
            keyPart = Normalizer.normalize(keyPart, Normalizer.Form.NFD);
            patternBuilder.append(keyPart)
                    .append(ALLOWED_CHARS_REGEXP)
                    .append('$');
            patterns.add(Pattern.compile(patternBuilder.toString()));
        }

        for (String key : keys) {
            for (Pattern pattern : patterns) {

                if (pattern.matcher(key).matches()) {
                    result.addAll((LinkedHashSet<Long>) index.get(key));
                    count += ((LinkedHashSet<Long>) index.get(key)).size();
                }
            }
        }
        return Long.valueOf(result.size());
    }

    private synchronized String normalizeQuery(String query) {
        String nQuery = query.trim().toLowerCase();
        return nQuery;
    }
}
