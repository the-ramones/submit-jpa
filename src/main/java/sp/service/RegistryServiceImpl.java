package sp.service;

import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sp.model.Op;
import sp.model.Register;
import sp.model.RegisterId;
import sp.model.User;
import sp.repository.RegisterRepository;

/**
 * JPA-based implementation of {@link RegistryService}
 *
 * @author Paul Kulitski
 * @see RegistryService
 * @see Service
 */
@Service
@PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
public class RegistryServiceImpl implements RegistryService {

    @Inject
    RegisterRepository registerRepository;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Register getRegisterById(RegisterId id) {
        return registerRepository.getRegisterById(id);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Register> getRegistersByOp(Op op) {
        return registerRepository.getRegistersByOp(op);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    //@PreFilter 
    //TODO: filter only user registers, but for admins all
    public List<Register> getRegistersByUser(User user) {
        return registerRepository.getRegistersByUser(user);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Register> getRegisterByPeriod(Date startDate, Date endDate) {
        return registerRepository.getRegisterByPeriod(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Register> getRegister(User user, Op op, Date startDate, Date endDate) {
        return registerRepository.getRegister(user, op, startDate, endDate);
    }

    @Override
    @Transactional
    public Register addRegister(Register register) {
        return registerRepository.saveRegister(register);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Register> getAll() {
        return registerRepository.getAll();
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Register> getRegisters(int from, int limit) {
        return registerRepository.getAll(from, limit);
    }

    @Override
    public Long count() {
        return registerRepository.count();
    }

    @Override
    public Long count(Op op) {
        return registerRepository.count(op);                
    }

    @Override
    public Long count(User user) {
        return registerRepository.count(user);
    }

    @Override
    public Long count(User user, Op op) {
        return registerRepository.count(user, op);
    }
}
