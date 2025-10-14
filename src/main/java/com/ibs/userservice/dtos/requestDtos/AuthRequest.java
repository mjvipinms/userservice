package com.ibs.userservice.dtos.requestDtos;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
