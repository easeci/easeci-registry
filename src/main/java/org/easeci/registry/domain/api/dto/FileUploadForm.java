package org.easeci.registry.domain.api.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class FileUploadForm {

    @NotNull
    @Length(min = 3, max = 60)
    private String authorFullname;

    @NotNull
    @Email
    private String authorEmail;

    @NotNull
    @Length(min = 3, max = 50)
    private String company;

    @NotNull
    @Length(min = 3, max = 50)
    private String performerName;

    @NotNull
    @Pattern(regexp = "^((([0-9]+)\\.([0-9]+)\\.([0-9]+)(?:-([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?)(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?)$")
    private String performerVersion;
}
