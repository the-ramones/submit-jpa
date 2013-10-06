package sp.suggest;

import java.util.List;

/**
 * Interface for searcher 
 *  
 * @author Paul Kulitski
 */
public interface IndexSearcher {

    List<Long> search(String query);

    List<Long> search(String query, int limit);
    
    Long count(String query);
}
