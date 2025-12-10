package com.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[a-zA-Z0-9_]{5,15}$", message = "Username must be 5-15 characters and alphanumeric with underscores")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}
