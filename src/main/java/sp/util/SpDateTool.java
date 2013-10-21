package sp.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;
import org.apache.velocity.tools.generic.DateTool;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Redefinition {@link org.apache.velocity.tools.generic.DateTool} through
 * inheritance to format Russian dates in right manner
 *
 * @author Paul Kulitski
 */
public class SpDateTool extends DateTool {

    protected static final Logger logger = Logger.getLogger(SpDateTool.class.getName());

    /**
     * Returns a {@link DateFormat} instance for the specified format,
     * {@link Locale}, and {@link TimeZone}. If the format specified is a
     * standard style pattern, then a date-time instance will be returned with
     * both the date and time styles set to the specified style. If it is a
     * custom format, then creation will be delegated to
     * {@link SpDateFormatFactory} a customized and localized
     * {@link SimpleDateFormat} will be returned.
     *
     * @param format the custom or standard formatting pattern to be used
     * @param locale the {@link Locale} to be used
     * @param timezone the {@link TimeZone} to be used
     * @return an instance of {@link DateFormat}
     * @see SimpleDateFormat
     * @see DateFormat
     * @see SpDateFormatFactory
     */
    @Override
    public DateFormat getDateFormat(String format, Locale locale, TimeZone timezone) {
        DateFormat df = null;
        /*
         * issue: hard-coded
         */
        locale = LocaleContextHolder.getLocale();
        /*
         * Check if format is custom standart Java DateFormat compatible:
         * if - true, delegates to SpDateFormatFactory, else - fallback to 
         * the parrent class method
         */
        if (getStyleAsInt(format) < 0) {
            df = SpDateFormatFactory.getDateFormat(format, locale, timezone);
        } else {
            df = getDateFormat(format, locale, timezone);
        }
        return df;
    }
}
