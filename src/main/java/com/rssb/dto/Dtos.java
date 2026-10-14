package com.rssb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class Dtos {
    private Dtos() {}

    public record AuthRequest(@NotBlank @Size(max = 100) String username,
                              @NotBlank @Size(min = 6, max = 100) String password) {}
    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record AuthResponse(String token, String username, String role) {}
    public record RegistrationRequest(@NotBlank @Size(max = 200) String name,
                                      @Size(max = 30) String phone,
                                      @Size(max = 200) String location) {}
    public record WalkieRequest(@Size(max = 100) String channel,
                                @NotBlank @Size(max = 2000) String message) {}
}
