package org.easeci.registry.domain.files;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString(exclude = "payload")
@NoArgsConstructor
@AllArgsConstructor
public class FileRepresentation {
    private RegistryStatus status;
    private FileMeta meta;
    private byte[] payload;

    @ToString
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileMeta {
        private String uploaderPrincipalName;
        private String authorFullname;
        private String authorEmail;
        private String company;
        private LocalDateTime creationDate;
        private String performerName;
        private String performerVersion;
        private long performerScriptBytes;
        private String documentationUrl;
        private Boolean validated;
    }
}
