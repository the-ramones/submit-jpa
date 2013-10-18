package sp.dwr;

import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Service enables the class to be used as a Spring service
 * @RemoteProxy enables the class to be used as a DWR service
 * @Transactional enables transaction support for this class
 *
 * NOTE: Annotation-based configuration doesn't work (cause hard integration
 * with used version of Spring Framework)
 */
@Service("springService")
@RemoteProxy(name = "dwrService")
@Transactional
public class ArithmeticService {

    protected static Logger logger = LoggerFactory.getLogger(ArithmeticService.class);

    /**
     * @RemoteMethod exposes this method to DWR. Your JSP pages can access this
     * method in javascript. Your Spring beans can still access this method.
     */
    @RemoteMethod
    public Integer add(Integer operand1, Integer operand2) {
        logger.debug("Adding two numbers");
        return operand1 + operand2;
    }

    @RemoteMethod
    public Integer sub(Integer operand1, Integer operand2) {
        logger.debug("Substracting two numbers");
        return operand1 - operand2;
    }

    @RemoteMethod
    public Integer mul(Integer operand1, Integer operand2) {
        logger.debug("Multiplicating two numbers");
        return operand1 * operand2;
    }

    @RemoteMethod
    public Integer div(Integer operand1, Integer operand2) {
        logger.debug("Deviding two numbers");
        return operand1 / operand2;
    }
}
