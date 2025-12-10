package com.user.dto.response;

import com.user.constants.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterationResponse {

    private UUID userId;
    private String name;
    private Role role;

}
