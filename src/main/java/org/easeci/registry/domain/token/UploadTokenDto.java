package org.easeci.registry.domain.token;

import org.springframework.beans.factory.annotation.Value;

public interface UploadTokenDto {

    @Value("#{target.id}")
    Long getId();

    @Value("#{target.token}")
    String getToken();
}
