package org.easeci.registry.domain.mail;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@AllArgsConstructor
public class MailerFacadeImpl implements MailerFacade {
    private JavaMailSender javaMailSender;
    private TemplateEngine templateEngine;

    @Override
    public void sendMail(MailSendRequest mailSendRequest) {
        MailSendRequestCreator creator = new MailSendRequestCreatorImpl(templateEngine);
        String processedMailTemplate = creator.buildMailSendRequest(mailSendRequest);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");

        try {
            messageHelper.setSubject(mailSendRequest.getSubject());
            messageHelper.setTo(mailSendRequest.getEmailAddress());
            messageHelper.setText(processedMailTemplate, true);
        } catch (MessagingException e) {
            log.error("Email sending error: Cannot send email to: " + mailSendRequest.getEmailAddress());
        }

        javaMailSender.send(mimeMessage);
    }

    @Override
    public void sendMailAsync(MailSendRequest mailSendRequest) {
        CompletableFuture.runAsync(() -> sendMail(mailSendRequest))
                .whenComplete((aVoid, throwable) -> log.info("Mail {} send with success", mailSendRequest.toString()))
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    log.error("Mail {} was probably not send correctly", mailSendRequest.toString());
                    return null;
                });
    }

}
