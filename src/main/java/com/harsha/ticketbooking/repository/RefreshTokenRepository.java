package com.harsha.ticketbooking.repository;

import com.harsha.ticketbooking.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken , Long> {
    Optional<RefreshToken> findByToken(String token) ;
}
