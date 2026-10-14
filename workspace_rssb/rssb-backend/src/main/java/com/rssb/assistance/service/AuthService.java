package com.rssb.assistance.service;

import com.rssb.assistance.dto.AuthRequest;
import com.rssb.assistance.dto.AuthResponse;
import com.rssb.assistance.dto.RegisterUserRequest;
import com.rssb.assistance.entity.AppUser;
import com.rssb.assistance.entity.Role;
import com.rssb.assistance.repository.AppUserRepository;
import com.rssb.assistance.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthService(AppUserRepository appUserRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService,
                       JwtService jwtService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterUserRequest request) {
        if (appUserRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        AppUser user = new AppUser();
        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(parseRole(request.getRole()));
        appUserRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);
        String role = userDetails.getAuthorities().stream().findFirst().map(Object::toString).orElse("ROLE_USER");
        return new AuthResponse(token, userDetails.getUsername(), role);
    }

    private Role parseRole(String role) {
        if (role == null || role.isBlank()) {
            return Role.ROLE_USER;
        }

        String normalized = role.trim().toUpperCase();
        if ("ADMIN".equals(normalized) || "ROLE_ADMIN".equals(normalized)) {
            return Role.ROLE_ADMIN;
        }
        if ("VISITOR".equals(normalized) || "ROLE_VISITOR".equals(normalized)) {
            return Role.ROLE_VISITOR;
        }
        return Role.ROLE_USER;
    }
}
