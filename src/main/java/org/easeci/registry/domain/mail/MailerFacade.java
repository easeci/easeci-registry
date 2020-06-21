package org.easeci.registry.domain.mail;

public interface MailerFacade {

    void sendMail(MailSendRequest mailSendRequest);

    void sendMailAsync(MailSendRequest mailSendRequest);
}
