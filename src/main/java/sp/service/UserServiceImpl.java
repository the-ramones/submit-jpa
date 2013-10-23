package sp.service;

import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sp.model.User;
import sp.repository.UserRepository;

/**
 * JPA-based implementation of {@link UserService}
 *
 * @author Paul Kulitski
 * @see Service
 * @see UserService
 */
@Service
public class UserServiceImpl implements UserService {

    @Inject
    UserRepository userRepository;

    @Override
    public User getUserById(Integer id) {
        return userRepository.getUserById(id);
    }

    @Override
    public List<User> getUserByName(String fullname) {
        return userRepository.getUserByName(fullname);
    }

    @Override
    @Transactional
    public User addUser(User user) {
        return userRepository.saveUser(user);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        if (getUserById(user.getId()) != null) {
            return userRepository.saveUser(user);
        } else {
            return null;
        }
    }
}
