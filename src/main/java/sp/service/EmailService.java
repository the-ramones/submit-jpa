package sp.service;

import org.apache.commons.mail.Email;

/**
 * Interface of Reports! e-mail service 
 * 
 * @author Paul Kulitski
 */
public interface EmailService {
    
    public void sendEmail(Email email);

}
