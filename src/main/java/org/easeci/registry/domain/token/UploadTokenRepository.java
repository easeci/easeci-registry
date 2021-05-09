package org.easeci.registry.domain.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Optional;

interface UploadTokenRepository extends JpaRepository<UploadToken, Long> {

    @Query(value = "select count(*) from upload_token where is_in_use = false", nativeQuery = true)
    int countAllFreeTokens();

    Optional<UploadToken> findByToken(String token);

    @Query("select t.isInUse from UploadToken t where t.token = :token")
    boolean isTokenInUse(@Param("token") String token);

    @Query(value = "select t.id, t.token from upload_token t where t.reserved = false limit 1", nativeQuery = true)
    Optional<UploadTokenDto> findOneToken();

    @Modifying
    @Transactional
    @Query(value = "update upload_token set reserved = true, reserved_by = :principal where id = :tokenId", nativeQuery = true)
    int reserveToken(Long tokenId, String principal);
}
