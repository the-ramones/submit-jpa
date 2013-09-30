package sp.service;

import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
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
public class RegistryServiceImpl implements RegistryService {

    @Inject
    RegisterRepository registerRepository;

    @Override
    @Transactional(readOnly = true)
    public Register getRegisterById(RegisterId id) {
        return registerRepository.getRegisterById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Register> getRegistersByOp(Op op) {
        return registerRepository.getRegistersByOp(op);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Register> getRegistersByUser(User user) {
        return registerRepository.getRegistersByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Register> getRegisterByPeriod(Date startDate, Date endDate) {
        return registerRepository.getRegisterByPeriod(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Register> getRegister(User user, Op op, Date startDate, Date endDate) {
        return registerRepository.getRegister(user, op, startDate, endDate);
    }

    @Override
    @Transactional
    public Register addRegister(Register register) {
        return registerRepository.saveRegister(register);
    }
}
