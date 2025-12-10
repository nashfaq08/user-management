package com.user.service;

import com.user.dto.*;
import com.user.dto.response.UserRegisterationResponse;
import com.user.entities.RefreshToken;
import com.user.entities.User;
import com.user.exception.UserAlreadyExistsException;
import com.user.exception.UserNotFoundException;
import com.user.repository.RefreshTokenRepository;
import com.user.repository.UserRepository;
import com.user.util.JWTUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAuthService {

    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;


    public ResponseEntity<?> login(LoginRequest loginRequest) {

        log.info("Starting to login the user {}", loginRequest.getUsername());

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UserNotFoundException(loginRequest.getUsername()));

        log.info("Verifying the credentials for user {}", loginRequest.getUsername());

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Invalid credentials"));
        }

        log.info("Generating the token for user {}", loginRequest.getUsername());

        String accessToken = jwtUtil.generateToken(user.getUsername(), user.getRole());
        String refreshToken = UUID.randomUUID().toString();

        log.info("Generating the refresh token for user {}", loginRequest.getUsername());

        RefreshToken rt = new RefreshToken();
        rt.setToken(refreshToken);
        rt.setUser(user);
        rt.setExpiryDate(LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(rt);

        log.info("Authentication info has been stored successfully for user {}", loginRequest.getUsername());

        return ResponseEntity.ok().body(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "name", user.getName()
        ));
    }

    @Transactional
    public ResponseEntity<?> register(RegistrationRequest registrationRequest) {
        log.info("Registering the user with role {} and username {}", registrationRequest.getRole(), registrationRequest.getUsername());

        if (userRepository.findByUsername(registrationRequest.getUsername()).isPresent())
            throw new UserAlreadyExistsException(registrationRequest.getUsername());

        User user = User.builder()
                .name(registrationRequest.getName())
                .username(registrationRequest.getUsername())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .role(registrationRequest.getRole())
                .build();

        userRepository.save(user);
        log.info("User information has been stored Successfully.");

        UserRegisterationResponse userRegisterationResponse = toDTO(user);
        return ResponseEntity.ok(userRegisterationResponse);
    }

    public ResponseEntity<?> refreshToken(Map<String, String> payload) {
        String refreshToken = payload.get("refreshToken");
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (storedToken == null || storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token is expired or invalid");
        }

        String newAccessToken = jwtUtil.generateToken(storedToken.getUser().getUsername(), storedToken.getUser().getRole());
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @Transactional
    public ResponseEntity<?> logout(Authentication authentication, Map<String, String> payload) {

        log.info("Logging out user {}", authentication.getName());

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized access: Authentication is missing or invalid"));
        }

        String refreshToken = payload.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message","Refresh token is required"));
        }

        log.info("Deleting the refresh token information for user {}", authentication.getName());

        refreshTokenRepository.findByToken(refreshToken).ifPresent(refreshTokenRepository::delete);
        return ResponseEntity.ok().body(Map.of("message", "Logged out successfully"));
    }

    @Transactional
    public ResponseEntity<?> logoutAllSessions(Authentication authentication) {

        log.info("Logging out all sessions for user {}", authentication.getName());

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized access: Authentication is missing or invalid"));
        }

        String username = authentication.getName();

        log.info("Fetching the User {}", authentication.getName());

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        log.info("Deleting the User from refresh token table {}", authentication.getName());

        refreshTokenRepository.deleteByUser(user);
        return ResponseEntity.ok().body(Map.of("message", "User " + username + " Logged out from all devices."));
    }

    public UserRegisterationResponse toDTO(User user) {
        UserRegisterationResponse userRegisterationResponse = new UserRegisterationResponse();
        userRegisterationResponse.setUserId(user.getId());
        userRegisterationResponse.setName(user.getName());
        userRegisterationResponse.setRole(user.getRole());
        return userRegisterationResponse;
    }
}
