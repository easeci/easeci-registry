package org.easeci.registry.domain.mail;

import lombok.*;

import java.util.Map;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MailSendRequest {
    private String subject;
    private String emailAddress;
    private String templateName;
    private Map<String, String> variables;
}
