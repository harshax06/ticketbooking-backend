package com.harsha.ticketbooking.controller;

import com.harsha.ticketbooking.dto.request.*;
import com.harsha.ticketbooking.dto.response.AuthResponseDto;
import com.harsha.ticketbooking.entity.RefreshToken;
import com.harsha.ticketbooking.service.AuthService;
import com.harsha.ticketbooking.service.JwtService;
import com.harsha.ticketbooking.service.PasswordResetService;
import com.harsha.ticketbooking.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService ;
    private final JwtService jwtService ;
    private final PasswordResetService passwordResetService;
    private final RefreshTokenService refreshTokenService ;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto dto) {
        return ResponseEntity.ok(authService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @PathVariable LoginRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(@Valid @RequestBody RefreshRequestDto dto) {
        RefreshToken valid = refreshTokenService.verify(dto.getRefreshToken());
        String newAccessToken = jwtService.generateToken(valid.getUser().getEmail(), valid.getUser().getRole().name()) ;
        return ResponseEntity.ok(new AuthResponseDto(
                newAccessToken, valid.getToken(), valid.getUser().getEmail(), valid.getUser().getRole().name()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequestDto dto) {
        refreshTokenService.revoke(dto.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto dto) {
        passwordResetService.requestReset(dto.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-token")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequestDto dto) {
        passwordResetService.confirmReset(dto.getToken(), dto.getNewPassword());
        return ResponseEntity.noContent().build();
    }
}
