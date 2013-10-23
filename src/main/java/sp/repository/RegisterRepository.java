package sp.repository;

import java.util.Date;
import java.util.List;
import sp.model.Op;
import sp.model.Register;
import sp.model.RegisterId;
import sp.model.User;

/**
 * Interface for registry repository.
 *
 * @author Paul Kulitski
 */
public interface RegisterRepository {

    public Register getRegisterById(RegisterId id);

    public List<Register> getRegistersByOp(Op op);

    public List<Register> getRegistersByUser(User user);

    public List<Register> getRegisterByPeriod(Date startDate, Date endDate);

    public List<Register> getRegister(User user, Op op, Date startDate, Date endDate);

    public Register saveRegister(Register register);
    
    public List<Register> getAll();
    
    public List<Register> getAll(int from, int limit);
    
    public Long count();
    
    public Long count(Op op);
    
    public Long count(User user);
    
    public Long count(User user, Op op);
    
    public Long getLastId();
}
