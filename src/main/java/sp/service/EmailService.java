package sp.service;

import java.util.Locale;
import sp.model.ajax.Statistics;

/**
 * Interface of Reports! e-mail service
 *
 * @author Paul Kulitski
 */
public interface EmailService {

    /**
     * Send the plain email to the array of recipients
     *
     * @param email email massage
     * @param recipients array of recipients
     */
    public void sendSimpleEmail(String subject, String content, String... recipients);

    /**
     * Send the HTML email to the array of recipients with statistics info in 2
     * forms: HTML data in email body
     *
     * @param email email massage
     * @param recipients array of recipients
     */
    public void sendEmailWithStatistics(String htmlContent, Statistics stats, String... recipients);

    /**
     * Send internationalized the HTML email to the array of recipients with
     * statistics info
     *
     * @param email email massage
     * @param recipients array of recipients
     */
    public void sendEmailWithStatistics(String htmlContent, Statistics stats, Locale locale, String... recipients);

    /**
     * Send the HTML email to the array of recipients with statistics info in 2
     * forms: HTML data in email body, PDF file as attachment
     *
     * @param email email massage
     * @param recipients array of recipients
     */
    public void sendEmailWithStatisticsAndPdfAttachment(String htmlContent, Statistics stats, String username, String... recipients);

    /**
     * Send internationalized the HTML email to the array of recipients with
     * statistics info in 2 forms: HTML data in email body, PDF file as
     * attachment
     *
     * @param email email massage
     * @param recipients array of recipients
     */
    public void sendEmailWithStatisticsAndPdfAttachment(String htmlContent, Statistics stats, String username, Locale locale, String... recipients);
}
