package sp.service;

import org.apache.commons.mail.Email;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Apache Commons Email based implementation of {@link EmailService}
 *
 * @author Paul Kulitski
 */
@Async
@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public void sendEmail(Email email) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
