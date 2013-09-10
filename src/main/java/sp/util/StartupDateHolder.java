package sp.util;

import java.util.Date;

/**
 * Application startup date holder
 *
 * @author Paul Kulitski
 */
public class StartupDateHolder {

    private static final ThreadLocal<Date> dateHolder;

    static {
        dateHolder = new ThreadLocal<Date>();
        dateHolder.set(new Date());
    }

    public StartupDateHolder() {
    }
}
