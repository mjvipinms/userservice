package com.ibs.userservice.controller;

import com.ibs.userservice.dtos.requestDtos.AuthRequest;
import com.ibs.userservice.dtos.responseDtos.AuthResponse;
import com.ibs.userservice.security.AppUserDetails;
import com.ibs.userservice.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     *
     * @param request is the input object with valid username and password
     * @return token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            log.info("Entering into login in AuthController {}", request);
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword())
            );
            UserDetails user = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(user);
            String role = user.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority).orElse("USER");
            log.info("Successfully generated token and exiting from login");
            AppUserDetails appUserDetails = (AppUserDetails) authentication.getPrincipal();
            var userDet = appUserDetails.user();
            Integer userId = userDet.getUserId();
            String userName = userDet.getUserName();

            return ResponseEntity.ok(new AuthResponse(token,role,userId,userName));
        } catch (Exception e) {
            log.error("Exception occurred in login {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}