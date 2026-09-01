package com.harsha.ticketbooking.service;

import com.harsha.ticketbooking.dto.request.LoginRequestDto;
import com.harsha.ticketbooking.dto.request.RegisterRequestDto;
import com.harsha.ticketbooking.entity.RefreshToken;
import com.harsha.ticketbooking.entity.Role;
import com.harsha.ticketbooking.entity.User;
import com.harsha.ticketbooking.exception.BadRequestException;
import com.harsha.ticketbooking.repository.UserRepository;
import com.harsha.ticketbooking.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequestDto registerDto;
    private LoginRequestDto loginDto;
    private User existingUser;
    private RefreshToken fakeRefreshToken;

    @BeforeEach
    void setup() {
        registerDto = new RegisterRequestDto();
        registerDto.setName("Test User");
        registerDto.setEmail("test@example.com");
        registerDto.setPassword("securepass123");

        loginDto = new LoginRequestDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("securepass123");

        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("test@example.com");
        existingUser.setPasswordHash("hashed-password");
        existingUser.setRole(Role.USER);

        fakeRefreshToken = new RefreshToken();
        fakeRefreshToken.setToken("fake-refresh-token");
        fakeRefreshToken.setUser(existingUser);
    }

    @Test
    void registersNewUserSuccessfully() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("securepass123")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("fake-jwt-token");
        when(refreshTokenService.createRefreshToken(any(User.class))).thenReturn(fakeRefreshToken);   // <-- add this

        var result = authService.register(registerDto);

        assertEquals("fake-jwt-token", result.getToken());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void throwsWhenRegisteringDuplicateEmail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

        assertThrows(BadRequestException.class, () -> authService.register(registerDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void logsInSuccessfullyWithCorrectCredentials() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("securepass123", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("fake-jwt-token");
        when(refreshTokenService.createRefreshToken(any(User.class))).thenReturn(fakeRefreshToken);   // <-- add this

        var result = authService.login(loginDto);

        assertEquals("fake-jwt-token", result.getToken());
    }

    @Test
    void throwsWhenLoggingInWithWrongPassword() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("securepass123", "hashed-password")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> authService.login(loginDto));
    }

    @Test
    void throwsWhenLoggingInWithNonExistentEmail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> authService.login(loginDto));
    }
}