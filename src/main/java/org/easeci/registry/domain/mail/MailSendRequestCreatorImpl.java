package org.easeci.registry.domain.mail;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor
class MailSendRequestCreatorImpl implements MailSendRequestCreator {
    private TemplateEngine templateEngine;

    @Override
    public String buildMailSendRequest(MailSendRequest mailSendRequest) {
        Context context = new Context();
        mailSendRequest.getVariables().forEach(context::setVariable);

        return templateEngine.process(mailSendRequest.getTemplateName(), context);
    }
}
