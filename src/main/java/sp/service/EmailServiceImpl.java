package sp.service;

import javax.inject.Inject;
import javax.mail.Session;
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

    @Inject
    Session emailSession;

    @Override
    public void sendEmail(Email email, String... recipients) {
        
    }

}
