package com.user.controller;

import com.user.dto.*;
import com.user.service.UserAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserAuthService userAuthService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        return userAuthService.register(registrationRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        return userAuthService.login(loginRequest);
    }

    @PreAuthorize("hasRole('USER') or hasRole('PREMIUM_USER') or hasRole('ADMIN')")
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> payload) {
        return userAuthService.refreshToken(payload);
    }

    @PreAuthorize("hasRole('USER') or hasRole('PREMIUM_USER') or hasRole('ADMIN')")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication, @RequestBody Map<String, String> payload) {
        return userAuthService.logout(authentication, payload);
    }

    @PreAuthorize("hasRole('USER') or hasRole('PREMIUM_USER') or hasRole('ADMIN')")
    @PostMapping("/logout-all")
    public ResponseEntity<?> logoutAllSessions(Authentication authentication) {
        return userAuthService.logoutAllSessions(authentication);
    }
}
