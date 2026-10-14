package com.caprae.saasquatch.controller;

import com.caprae.saasquatch.dto.AuthRequest;
import com.caprae.saasquatch.dto.AuthResponse;
import com.caprae.saasquatch.dto.RegisterRequest;
import com.caprae.saasquatch.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Enterprise JWT authentication and operator access")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and issue JWT bearer token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Register new operator or searcher account")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated operator profile")
    public ResponseEntity<Map<String, Object>> getCurrentUser(org.springframework.security.core.Authentication authentication) {
        Map<String, Object> profile = new java.util.HashMap<>();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            profile.put("authenticated", true);
            profile.put("username", authentication.getName());
            profile.put("authorities", authentication.getAuthorities());
            profile.put("role", authentication.getAuthorities().stream().findFirst().map(Object::toString).orElse("ROLE_OPERATOR"));
        } else {
            profile.put("authenticated", false);
            profile.put("username", "Guest / Demo Mode");
            profile.put("role", "ROLE_OPERATOR");
        }
        return ResponseEntity.ok(profile);
    }
}
