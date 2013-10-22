package sp.service;

import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sp.model.Op;
import sp.repository.OpRepository;

/**
 * JPA-based implementation of {@link OpService}
 *
 * @author Paul Kulitski
 * @see OpService
 * @see Service
 */
@Service
public class OpServiceImpl implements OpService {

    @Inject
    OpRepository opRepository;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Op getOpById(Integer id) {
        return opRepository.getOpById(id);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Op> getOpByTitle(String title) {
        return opRepository.getOpByTitle(title);
    }

    @Override
    @Transactional
    public Op addOp(Op op) {
        return opRepository.saveOp(op);
    }

    @Override
    @Transactional
    public Op updateOp(Op op) {
        if (getOpById(op.getId()) != null) {
            return opRepository.saveOp(op);
        } else {
            return null;
        }
    }
}
