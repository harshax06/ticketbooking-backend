package com.harsha.ticketbooking.service;

import com.harsha.ticketbooking.entity.RefreshToken;
import com.harsha.ticketbooking.entity.User;
import com.harsha.ticketbooking.exception.BadRequestException;
import com.harsha.ticketbooking.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository ;

    @Value("${jwt.refresh-expiration-ms}")
    private Long refreshExpirationMs ;

    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000));
        return refreshTokenRepository.save(refreshToken) ;
    }

    public RefreshToken verify(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token")) ;

        if(refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Refresh token is expired or revoked. Please log in again") ;
        }
        return refreshToken ;
    }

    public void revoke(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt) ;
        });
    }
}
