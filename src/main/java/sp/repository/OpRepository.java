package sp.repository;

import java.util.List;
import sp.model.Op;

/**
 * Interface for {@link Op} repositories
 * 
 * @author Paul Kulitski
 */
public interface OpRepository {
    
    /**
     * Search for {@link Op} by ID
     * 
     * @param id unique identifier of an operation
     * @return a found operation
     */
    public Op getOpById(Integer id);

    /**
     * Search for {@link Op} by title
     * 
     * @param title a title of an operation
     * @return a found operation
     */
    public List<Op> getOpByTitle(String title);

    /**
     * Saves {@link Op} to the persistent store
     * 
     * @param op an operation to be saved
     * @return a saved operation
     */
    public Op saveOp(Op op);
}
