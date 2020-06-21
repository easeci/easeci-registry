package org.easeci.registry.domain.mail;

interface MailSendRequestCreator {

    String buildMailSendRequest(MailSendRequest mailSendRequest);
}