package sp.suggest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.inject.Inject;
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

    @Inject
    SuggestIndex suggestIndex;

    private final static int MAX_RESULT_AMOUNT = 10000;
    
    private synchronized String normalizeQuery(String query) {
        return query.trim().toLowerCase();
    }

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
        int count = 0;
        String nQuery = normalizeQuery(query);
        Set<String> keys = suggestIndex.getKeys();
        Map index = suggestIndex.getIndex();
        StringBuilder sb = new StringBuilder(nQuery);
        List<Long> result = new ArrayList();
        String attempt;
        int length = sb.length();
        for (int i = 0; i < length; i++) {
            attempt = sb.substring(0, length - i - 1);
            if (keys.contains(attempt)) {
                List<Long> resultList = (List) index.get(attempt);
                if (resultList != null) {
                    if ((count + resultList.size()) >= limit) {
                        result.addAll(resultList.subList(0, limit - count - 1));
                        break;
                    } else {
                        result.addAll(resultList);
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
        List<String> result = new ArrayList();
        while (keysIt.hasNext()){
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
}
