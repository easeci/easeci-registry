package org.easeci.registry.domain.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

interface UploadTokenRepository extends JpaRepository<UploadToken, Long> {

    @Query(value = "select count(*) from upload_token where is_in_use = false", nativeQuery = true)
    int countAllFreeTokens();

    Optional<UploadToken> findByToken(String token);
}
