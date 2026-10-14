package com.rssb.web;

import com.rssb.dto.Dtos.*;
import com.rssb.model.AppUser;
import com.rssb.repo.AppUserRepository;
import com.rssb.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AppUserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(AppUserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users; this.encoder = encoder; this.jwt = jwt;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        return users.findByUsername(req.username())
                .filter(u -> encoder.matches(req.password(), u.getPassword()))
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(
                        new AuthResponse(jwt.generate(u.getUsername(), u.getRole()), u.getUsername(), u.getRole())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest req) {
        if (users.existsByUsername(req.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
        AppUser u = users.save(new AppUser(req.username(), encoder.encode(req.password()), "ROLE_USER"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(jwt.generate(u.getUsername(), u.getRole()), u.getUsername(), u.getRole()));
    }
}
