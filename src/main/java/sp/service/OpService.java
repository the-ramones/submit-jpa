package sp.service;

import java.util.List;
import sp.model.Op;

/**
 * Service interface for {@link Op}
 *
 * @author Paul Kulitski
 */
public interface OpService {

    public Op getOpById(Integer id);

    public List<Op> getOpByTitle(String title);

    public Op addOp(Op op);

    public Op updateOp(Op op);
}
