package com.example.authorization_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthorizationController {

    @PostMapping("/check")
    public ResponseEntity<String> checkAuth(
            @RequestHeader String username,
            @RequestHeader String password,
            @RequestHeader String role) {

        // 🔒 Vérification stricte des identifiants
        if ("admin".equals(username) && "admin123".equals(password) && "ADMIN".equals(role)) {
            return ResponseEntity.ok("✅ Authorized: ADMIN access granted");
        } else if ("user".equals(username) && "user123".equals(password) && "USER".equals(role)) {
            return ResponseEntity.ok("✅ Authorized: USER access granted");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("❌ Unauthorized: invalid credentials");
        }
    }
}
