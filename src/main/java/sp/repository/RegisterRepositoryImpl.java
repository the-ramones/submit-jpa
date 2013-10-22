package sp.repository;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import sp.model.Op;
import sp.model.Register;
import sp.model.RegisterId;
import sp.model.User;

/**
 * EclipseLink-based implementation of {@link RegisterRepository} interface.
 *
 * @author Paul Kulitski
 * @see RegisterRepository
 * @see Repository
 */
@Repository
public class RegisterRepositoryImpl implements RegisterRepository {

    @PersistenceContext(unitName = "registryPU")
    EntityManager em;

    @Override
    public Register getRegisterById(RegisterId id) {
        if (id != null) {
            return em.find(Register.class, id);
        } else {
            return null;
        }
    }

    @Override
    public List<Register> getRegistersByOp(Op op) {
        if (op != null) {
            TypedQuery<Register> query =
                    em.createNamedQuery("Register.getRegistersByOp", Register.class);
            query.setParameter("op", op);
            return query.getResultList();
        } else {
            return null;
        }
    }

    @Override
    public List<Register> getRegistersByUser(User user) {
        if (user != null) {
            TypedQuery<Register> query =
                    em.createNamedQuery("Register.getRegistersByUser", Register.class);
            query.setParameter("user", user);
            return query.getResultList();
        } else {
            return null;
        }
    }

    @Override
    public List<Register> getRegisterByPeriod(Date startDate, Date endDate) {
        TypedQuery<Register> query =
                em.createNamedQuery("Register.findByPeriod", Register.class);
        if (startDate != null) {
            query.setParameter("startDate", startDate);
        } else {
            query.setParameter("startDate", new Date(0));
        }
        if (endDate != null) {
            query.setParameter("endDate", endDate);
        } else {
            query.setParameter("endDate", new Date());
        }
        return query.getResultList();
    }

    @Override
    public List<Register> getRegister(User user, Op op, Date startDate, Date endDate) {
        TypedQuery<Register> query =
                em.createNamedQuery("Register.find", Register.class);
        if (op != null && user != null) {
            query.setParameter("user", user);
            query.setParameter("op", op);
            if (startDate != null) {
                query.setParameter("startDate", startDate);
            } else {
                query.setParameter("startDate", new Date(0));
            }
            if (endDate != null) {
                query.setParameter("endDate", endDate);
            } else {
                query.setParameter("endDate", new Date());
            }
            return query.getResultList();
        } else {
            return null;
        }
    }

    @Override
    public Register saveRegister(Register register) {
        if (register != null) {
            em.persist(register);
            em.flush();
            return register;
        } else {
            return null;
        }
    }

    @Override
    public List<Register> getAll() {
        TypedQuery<Register> query =
                em.createNamedQuery("Register.getAll", Register.class);
        return query.getResultList();
    }

    @Override
    public List<Register> getAll(int from, int limit) {
        TypedQuery<Register> query =
                em.createNamedQuery("Register.getAll", Register.class);
        query.setFirstResult(from);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public Long count() {
        TypedQuery<Long> query =
                em.createNamedQuery("Register.countAll", Long.class);
        return query.getSingleResult();
    }

    @Override
    public Long count(Op op) {
        TypedQuery<Long> query =
                em.createNamedQuery("Register.countByOp", Long.class);
        query.setParameter("op", op);
        return query.getSingleResult();
    }

    @Override
    public Long count(User user) {
        TypedQuery<Long> query =
                em.createNamedQuery("Register.countByUser", Long.class);
        query.setParameter("user", user);
        return query.getSingleResult();

    }

    @Override
    public Long count(User user, Op op) {
        TypedQuery<Long> query =
                em.createNamedQuery("Register.countByUserAndOp", Long.class);
        query.setParameter("op", op);
        query.setParameter("user", user);
        return query.getSingleResult();

    }

    @Override
    public Long getLastId() {
        TypedQuery<Long> query = 
               em.createNamedQuery("Register.getLastIdByUserAndOp", Long.class);

        query.setMaxResults(1);
        return query.getResultList().get(0);
    }
}
