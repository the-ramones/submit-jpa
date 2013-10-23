package sp.util;

import java.util.Date;
import java.util.Locale;
import org.springframework.context.MessageSource;
import sp.model.ajax.Statistics;

/**
 * Reports! contract for PDF builder implementations
 *
 * @author Paul Kulitski
 */
public interface SpPdfBuilder {

    /*
     * i10n keys
     */
    static final String PDF_TITLE_KEY = "email.pdf.title";
    static final String PDF_AMOUNTPERFORMERS_KEY = "email.pdf.countperformers";
    static final String PDF_AMOUTACTIVITIES_KEY = "email.pdf.countactivities";
    static final String PDF_PERFORMER_KEY = "email.pdf.performers";
    static final String PDF_ACTIVITIES_KEY = "email.pdf.activities";
    static final String PDF_AVG_KEY = "email.pdf.avg";
    static final String PDF_FOOTER_KEY = "email.pdf.footer";
    static final String PDF_DAYS_KEY = "email.pdf.days";
    /*
     * system properties
     */
    static final String PDF_PATH_PREFIX = "files/";

    /**
     * Build a PDF file with statistics to be used as a attachment to an e-mail.
     * {@link SpStatsPdfBuilder#setStatistics(sp.model.ajax.Statistics)} and
     * {@link SpStatsPdfBuilder#setUsername(java.lang.String)} have to be used
     * before invocation.
     *
     * @return a path to PDF file
     */
    String build();

    Date getDate();

    Locale getLocale();

    Statistics getStatistics();

    String getUsername();

    void setDate(Date date);

    void setLocale(Locale locale);

    void setMessageSource(MessageSource messageSource);

    void setStatistics(Statistics statistics);

    void setUsername(String username);
}
