package org.easeci.registry.domain.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadRequest {
    private String authorFullname;
    private String authorEmail;
    private String company;
    private String performerName;
    private String performerVersion;
    private byte[] multipartFile;
}
