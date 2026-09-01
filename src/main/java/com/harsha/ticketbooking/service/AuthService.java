package com.harsha.ticketbooking.service;

import com.harsha.ticketbooking.dto.request.LoginRequestDto;
import com.harsha.ticketbooking.dto.request.RegisterRequestDto;
import com.harsha.ticketbooking.dto.response.AuthResponseDto;
import com.harsha.ticketbooking.entity.RefreshToken;
import com.harsha.ticketbooking.entity.Role;
import com.harsha.ticketbooking.entity.User;
import com.harsha.ticketbooking.exception.BadRequestException;
import com.harsha.ticketbooking.repository.UserRepository;
import com.harsha.ticketbooking.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository ;
    private final PasswordEncoder passwordEncoder ;
    private final JwtService jwtService ;
    private final RefreshTokenService refreshTokenService ;

    public AuthResponseDto register(RegisterRequestDto dto) {
        if(userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new BadRequestException("An account with this email is already exists");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.USER);

        User saved = userRepository.save(user);
        String accessToken = jwtService.generateToken(saved.getEmail(), saved.getRole().name());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return new AuthResponseDto(accessToken, user.getEmail(), user.getRole().name(), refreshToken.getToken());
    }

    public AuthResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BadRequestException(("Invalid email or password")));

        if(!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid email or password");
        }

        String accessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return new AuthResponseDto(accessToken, user.getEmail(), user.getRole().name(),  refreshToken.getToken());
    }
}
