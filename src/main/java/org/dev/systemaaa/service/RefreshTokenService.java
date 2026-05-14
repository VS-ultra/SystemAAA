package org.dev.systemaaa.service;

import lombok.RequiredArgsConstructor;
import org.dev.systemaaa.model.entity.RefreshToken;
import org.dev.systemaaa.model.entity.User;
import org.dev.systemaaa.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // Срок жизни refresh токена в днях (задаётся в application.yaml)
    @Value("${jwt.refresh-expiration-days:7}")
    private long refreshExpirationDays;

    /**
     * Создаёт новый refresh token для пользователя и сохраняет в БД.
     * Перед созданием отзывает все предыдущие активные токены этого пользователя
     * (один пользователь — один активный refresh token).
     */
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        refreshTokenRepository.revokeAllByUser(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(refreshExpirationDays));
        refreshToken.setRevoked(false);
        refreshToken.setCreatedAt(LocalDateTime.now());

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken verifyAndGet(String tokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token не найден"));

        if (refreshToken.isRevoked()) {
            throw new IllegalArgumentException("Refresh token отозван");
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Refresh token истёк");
        }

        return refreshToken;
    }

    @Transactional
    public void revokeToken(String tokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token не найден"));
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.revokeAllByUser(user);
    }
}