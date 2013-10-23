package sp.repository;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import sp.model.Op;

/**
 * EclipseLink-based implementation of {@link OpRepository}
 *
 * @author Paul Kulitski
 * @see Repository
 * @see OpRepository
 */
@Repository
public class OpRepositoryImpl implements OpRepository {

    @PersistenceContext(unitName = "registryPU")
    EntityManager em;

    @Override
    public Op getOpById(Integer id) {
        if (id != null) {
            return em.find(Op.class, id);
        } else {
            return null;
        }
    }

    @Override
    public List<Op> getOpByTitle(String title) {
        if (title != null) {
            TypedQuery<Op> query =
                    em.createNamedQuery("Op.findByTitle", Op.class);
            query.setParameter("title", title);
            return query.getResultList();
        } else {
            return null;
        }
    }

    @Override
    public Op saveOp(Op op) {
        if (op != null) {
            em.persist(op);
            em.flush();
            return op;
        } else {
            return null;
        }
    }
}
