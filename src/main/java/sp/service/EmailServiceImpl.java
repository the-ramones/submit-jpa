package sp.service;

import java.util.Date;
import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Session;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import sp.model.ajax.Statistics;
import sp.util.SpDateFormatFactory;
import sp.util.SpStatsITextPdfBuilder;

/**
 * Apache Commons Email based implementation of {@link EmailService}
 *
 * @author Paul Kulitski
 */
//@Async
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    @Inject
    Session emailSession;
    @Inject
    @Named("emailMessageSource")
    private MessageSource messageSource;
    
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String FROM = "areports@mail.ru";
    private static final String STATISTICS_SUBJECT_I18N_KEY = "email.statistics.subject";
    private static final String STATISTICS_ASOF_I18N_KEY = "email.statistics.asof";
    private static final String PDF_ATTACHMENT_DESCRIPTION_KEY = "email.pdf.description";
    private static final String PDF_ATTACHMENT_NAME_KEY = "email.pdf.name";

    /**
     * Sends a generic text email message to the list of specified recipients
     *
     * @param subject subject of an email
     * @param content text content of an email
     * @param recipients list of recipients
     */
    @Override
    public void sendSimpleEmail(String subject, String content, String... recipients) {
        Email email = new SimpleEmail();
        email.setMailSession(emailSession);
        email.setCharset(DEFAULT_CHARSET);
        email.setSubject(subject);
        try {
            email.setMsg(content);
            email.addTo(recipients);
            email.send();
        } catch (EmailException eex) {
            logger.error("Cannot send an email", eex);
        } catch (Exception ex) {
            logger.error("Cannot set message into email", ex);
        }
    }

    @Override
    public void sendEmailWithStatistics(String htmlContent, Statistics stats, String... recipients) {
        sendEmailWithStatistics(htmlContent, stats, Locale.ENGLISH, recipients);
    }

    /*
     * TODO: Interceptors place? @Async?
     */
    @Override
    public void sendEmailWithStatistics(String htmlContent, Statistics stats, Locale locale, String... recipients) {
        HtmlEmail email = new HtmlEmail();
        email.setMailSession(emailSession);
        email.setCharset(DEFAULT_CHARSET);
        String currentDate = SpDateFormatFactory.getDateFormat().format(new Date());
        String subject = messageSource.getMessage(STATISTICS_ASOF_I18N_KEY, null, locale);
        String asof = messageSource.getMessage(STATISTICS_SUBJECT_I18N_KEY, null, locale);
        StringBuilder sb = new StringBuilder(subject.length() * 2);
        sb.append(subject).append(' ').append(asof).append(' ').append(currentDate);
        email.setSubject(sb.toString());
        try {
            email.setHtmlMsg(htmlContent);
            // TODO: falback text message
            email.setTextMsg(" ");
            email.addTo(recipients);
            email.send();
        } catch (EmailException ex) {
            logger.error("Cannot send an email", ex);
        }
    }

    @Override
    public void sendEmailWithStatisticsAndPdfAttachment(String htmlContent, Statistics stats, String username, String... recipients) {
        sendEmailWithStatisticsAndPdfAttachment(htmlContent, stats, username, Locale.ENGLISH, recipients);
    }

    @Override
    public void sendEmailWithStatisticsAndPdfAttachment(String htmlContent, Statistics stats, String username, Locale locale, String... recipients) {
        logger.debug("Sending email to the client {}", username);
        HtmlEmail email = new HtmlEmail();
        email.setMailSession(emailSession);
        email.setCharset(DEFAULT_CHARSET);
        String currentDate = SpDateFormatFactory.getDateFormat(locale).format(new Date());
        String subject = messageSource.getMessage(STATISTICS_SUBJECT_I18N_KEY, null, locale);
        String asof = messageSource.getMessage(STATISTICS_SUBJECT_I18N_KEY, null, locale);
        StringBuilder sb = new StringBuilder(subject.length() * 2);
        sb.append(subject).append(' ').append(asof).append(' ').append(currentDate);
        email.setSubject(sb.toString());

        /*
         * Attachment is created by SpStatsITextPdfBuilder
         */
        SpStatsITextPdfBuilder itextBuilder = new SpStatsITextPdfBuilder();
        itextBuilder.setStatistics(stats);
        itextBuilder.setMessageSource(messageSource);
        itextBuilder.setLocale(locale);
        itextBuilder.setDate(new Date());
        itextBuilder.setUsername(username);
        String iPath = itextBuilder.build();
        EmailAttachment iAttachment = null;
        if (iPath != null) {
            iAttachment = new EmailAttachment();
            iAttachment.setDisposition(EmailAttachment.ATTACHMENT);
            iAttachment.setDescription(messageSource.getMessage(PDF_ATTACHMENT_DESCRIPTION_KEY, null, locale));            
            iAttachment.setPath(iPath);
            // TODO: bad appearance in Cyrillic
            iAttachment.setName(messageSource.getMessage(PDF_ATTACHMENT_NAME_KEY, null, locale));
        }        
        try {
            email.attach(iAttachment);
            email.setFrom(FROM);
            email.addTo(recipients);
            //TODO: clean-up
            email.addTo("kulickipavel@gmail.com");
            email.setHtmlMsg(htmlContent);
            //TODO: add fallback plain text message
            email.setTextMsg(" ");
            email.send();
            logger.debug("Email has been sended");
        } catch (EmailException ex) {
            logger.error("Cannot send an email", ex);
        }
    }
}
