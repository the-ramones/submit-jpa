package sp.config;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import org.apache.commons.mail.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import sp.service.EmailService;

/**
 * Generic Java-based configuration. Additional configuration.
 *
 * @author Paul Kulitski
 */
@Configuration
@PropertySource("classpath:email.config.properties")
public class ReportConfig {

    @Autowired
    Environment env;

    /**
     * Creates JavaMail {@link Session} instance for reusing
     * {@link EmailService} Uses basic authentication. Avoids multiple
     * authentication process while sending e-mails.
     *
     * Use case: {@link Email#setMailSession(javax.mail.Session)}
     *
     * @return JavaMail session
     */
    @Bean
    public Session emailSession() {
        Properties emailProperties = new Properties();
        /*
         * Comprising email properties. Place for refactoring
         */
        emailProperties.put("mail.smtp.host", env.getProperty("mail.smtp.host"));
        emailProperties.put("mail.smtp.port", env.getProperty("mail.smtp.port"));
        emailProperties.put("mail.smtp.auth", env.getProperty("mail.smtp.auth"));
        emailProperties.put("mail.smtp.ssl.enable", env.getProperty("mail.smtp.ssl.enable"));
        emailProperties.put("mail.smtp.ssl.checkserveridentity", env.getProperty("mail.smtp.ssl.checkserveridentity"));
        emailProperties.put("mail.smtp.socketFactory.port", env.getProperty("mail.smtp.socketFactory.port"));
        emailProperties.put("mail.smtp.socketFactory.class", env.getProperty("mail.smtp.socketFactory.class"));
        emailProperties.put("mail.smtp.reportsuccess", env.getProperty("mail.smtp.reportsuccess"));
        emailProperties.put("mail.smtp.user", env.getProperty("mail.smtp.user"));
        emailProperties.put("mail.from", env.getProperty("mail.from"));

        Session session = Session.getInstance(emailProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(env.getProperty("mail.user"), env.getProperty("mail.password"));
            }
        });
        return session;
    }
}
