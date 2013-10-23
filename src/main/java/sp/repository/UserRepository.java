package sp.repository;

import java.util.List;
import sp.model.User;

/**
 * Interface for {@link User} repositories
 *
 * @author Paul Kulitski
 */
public interface UserRepository {

    /**
     * Search for {@link User} by ID
     *
     * @param id unique identifier of an user
     * @return a found user
     */
    public User getUserById(Integer id);

    /**
     * Search for {@link User} by full name
     *
     * @param fullname a full name of an user
     * @return a found user
     */
    public List<User> getUserByName(String fullname);

    /**
     * Add user to the persistent storage
     *
     * @param user an user to be saved
     * @return an saved user
     */
    public User saveUser(User user);
}
