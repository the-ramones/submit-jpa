package sp.service;

import java.util.List;
import sp.model.User;

/**
 * Service interface for {@link User}
 *
 * @author Paul Kulitski
 */
public interface UserService {

    public User getUserById(Integer id);

    public List<User> getUserByName(String fullname);

    public User addUser(User user);

    public User updateUser(User user);
}
