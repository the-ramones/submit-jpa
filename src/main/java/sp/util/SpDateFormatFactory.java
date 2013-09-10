package sp.util;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Logger;
import org.mockito.internal.listeners.CollectCreatedMocks;

/**
 * Factory for Reports! {@link java.text.DateFormat} being used in
 * {@link org.springframework.stereotype.Controller} instances and
 * {@link sp.util.SpDateTool} class to fix up date formatting in views. As the
 * result custom localized date and time appearance in view
 *
 * @author Paul Kulitski
 * @see DateFormat
 * @see SimpleDateFormat
 */
public class SpDateFormatFactory {
    
    private static final Logger LOG = Logger.getLogger(SpDateFormatFactory.class.getName());
    
    public static DateFormat getDateFormat(String format) {
        return getDateFormat(format, null, null);
    }
    
    public static DateFormat getDateFormat(String format, Locale locale) {
        return getDateFormat(format, locale, null);
    }

    /**
     * Creates a {@link java.text.DateFormat} instance for specified format,
     * locale and time zone
     *
     * @param format
     * @param locale
     * @param timezone
     * @return
     */
    public static DateFormat getDateFormat(String format, Locale locale, TimeZone timezone) {
            LOG.warning("getCustomDateFormat locale: " + locale);
        if (timezone == null) {
            timezone = TimeZone.getDefault();
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        DateFormat df = getCustomDateFormat(format, locale);
        df.setTimeZone(timezone);
        return df;
    }
    /**
     * Custom format for dates like '12 Sep 2012' compatible with
     * {@link java.text.DateFormat}
     */
    private static final String SHORT_MONTHS_FORMAT = "dd MMM yyyy";
    /**
     * Set of Russian locales available in JDK
     */
    private static final Locale[] russianLocales = new Locale[]{
        new Locale.Builder().setLanguage("ru").setRegion("RU").build(),
        Locale.forLanguageTag("ru")
    };

    /**
     * Return custom {@link java.text.DateFormat} for specified locale that
     * conforms specified format. Currently replace only month names for Russian
     * locale
     *
     * @param format date format is applied
     * @param locale locale is applied
     * @return
     */
    private static DateFormat getCustomDateFormat(String format, Locale locale) {
            LOG.warning("getCustomDateFormat locale: " + locale);
        DateFormat df;
           LOG.warning("getCustomDateFormat, format={" + format + "}, locale={" + locale + "}");
           System.out.println("getCustomDateFormat, format={" + format + "}, locale={" + locale + "}");
        Set<Locale> russian = new HashSet<Locale>(Arrays.asList(russianLocales));
        if (russian.contains(locale)) {
               LOG.warning("in Russian locale getCustomDateFormat, format={" + format + "}, locale={" + locale + "}");
               System.out.println("in Russian locale getCustomDateFormat, format={" + format + "}, locale={" + locale + "}");
            df = new SimpleDateFormat(format, new DateFormatSymbolsRu());
        } else {
            df = new SimpleDateFormat(format, locale);
        }
        df.setLenient(false);
        return df;
    }

    /**
     * Holder for Reports! custom date and time format applied to Russian
     * locale. Overrides month names to be in genitive case
     */
    static class DateFormatSymbolsRu extends DateFormatSymbols {
        
        @Override
        public String[] getMonths() {
            return new String[]{
                "Января", "Февраля", "Марта", "Апреля", "Мая", "Июня", "Июля", "Августа", "Сентября", "Октября", "Ноября", "Февраля"
            };
        }
        
        @Override
        public String[] getShortMonths() {
            return new String[]{
                "Янв", "Фев", "Мар", "Апр", "Мая", "Июн", "Июл", "Авг", "Сен", "Окт", "Ноя", "Фев"
            };
        }
    }
}
