package com.harsha.ticketbooking.controller;

import com.harsha.ticketbooking.dto.request.LoginRequestDto;
import com.harsha.ticketbooking.dto.request.RegisterRequestDto;
import com.harsha.ticketbooking.dto.response.AuthResponseDto;
import com.harsha.ticketbooking.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService ;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto dto) {
        return ResponseEntity.ok(authService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @PathVariable LoginRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
