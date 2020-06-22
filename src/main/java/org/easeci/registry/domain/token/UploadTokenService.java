package org.easeci.registry.domain.token;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class UploadTokenService {
    private UploadTokenRepository uploadTokenRepository;

    @Value("${upload.token.quantity-per-day}")
    private int tokenQuantity;

    UploadTokenService(UploadTokenRepository uploadTokenRepository) {
        this.uploadTokenRepository = uploadTokenRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    void generateTokens() {
        int freeTokens = uploadTokenRepository.countAllFreeTokens();
        List<UploadToken> uploadTokens = generateTokens(tokenQuantity - freeTokens);
        uploadTokenRepository.saveAll(uploadTokens);
        log.info("Generated and saved {} new tokens", uploadTokens.size());
    }

    public UploadToken use(String token, Long releasedForVersionId) {
        return uploadTokenRepository.findByToken(token)
                .map(uploadToken -> {
                    uploadToken.setInUse(true);
                    uploadToken.setUseDate(new Date());
                    uploadToken.setReleasedForVersionId(releasedForVersionId);
                    return uploadToken;
                })
                .map(uploadToken -> uploadTokenRepository.save(uploadToken))
                .orElseThrow();
    }

    public boolean isTokenAvailable(String token) {
        return !uploadTokenRepository.isTokenInUse(token);
    }

    public Optional<UploadTokenDto> getToken() {
        return uploadTokenRepository.findOneToken();
    }

    public void reserveToken(Long tokenId, Principal principal) {
        log.info("Token with id: {}, reserved by {}", tokenId, principal.getName());
        uploadTokenRepository.reserveToken(tokenId, principal.getName());
    }

    private List<UploadToken> generateTokens(int amount) {
        return IntStream.iterate(0, i -> i++)
                .limit(amount)
                .mapToObj(i -> constructOne())
                .collect(Collectors.toList());
    }

    private UploadToken constructOne() {
        return UploadToken.builder()
                .isInUse(false)
                .releaseDate(new Date())
                .token(new String(Base64.getEncoder().encode(UUID.randomUUID().toString().getBytes())))
                .build();
    }
}
