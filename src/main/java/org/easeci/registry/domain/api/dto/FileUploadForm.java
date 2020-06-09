package org.easeci.registry.domain.api.dto;

import lombok.Data;

@Data
public class FileUploadForm {
//    dodać walidację TODO
    private String authorFullname;
    private String authorEmail;
    private String company;
    private String performerName;
    private String performerVersion;
}
