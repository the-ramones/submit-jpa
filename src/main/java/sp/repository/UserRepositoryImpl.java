package sp.repository;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import sp.model.User;

/**
 * EclipseLink-based implementation of {@link UserRepository}
 *
 * @author Paul Kulitksi
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    @PersistenceContext(unitName = "registryPU")
    EntityManager em;

    @Override
    public User getUserById(Integer id) {
        return em.find(User.class, id);
    }

    @Override
    public List<User> getUserByName(String fullname) {
        if (fullname != null) {
            TypedQuery<User> query =
                    em.createNamedQuery("User.findByFullname", User.class);
            query.setParameter("fullname", fullname);
            return query.getResultList();
        } else {
            return null;
        }
    }

    @Override
    public User saveUser(User user) {
        em.persist(user);
        em.flush();
        return user;
    }
}
