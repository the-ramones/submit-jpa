package sp.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Paul Kulitski
 */
public class SpHasherTest {

    final static Logger logger = LoggerFactory.getLogger(SpHasherTest.class);

    public static void main(String[] args) {
        logger.info("1 << 31: {}", (int) 1 << 31);
        logger.info("~((int) 1 << 31): {}", ~((int) 1 << 31));
        logger.info("Math.random: {}", Math.random());
        logger.info("Math.random * ~((int) 1 << 31): {}", Math.random() * ~((int) 1 << 31));
        /*
         * (int) 'double value' = 0 (is equals zero, a little bit strange)
         */
        logger.info("(int) Math.random * ~((int) 1 << 31): {}", (int) Math.random() * ~((int) 1 << 31));
        int init = Math.round((float) Math.random() * (~((int) 1 << 31))) | 1;
        int multiplier = Math.round((float) Math.random() * (~((int) 1 << 31))) | 1;
        logger.info("init: {}, multiplier: {}", init, multiplier);
        logger.info("2 | 1: {}", 2 | 1);
    }
}
