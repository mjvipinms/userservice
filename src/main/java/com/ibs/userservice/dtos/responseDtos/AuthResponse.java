package com.ibs.userservice.dtos.responseDtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class AuthResponse {
    private String token;
    private String role;
    private Integer userId;
    private String username;
}
