package dev.antonis.your_digital_bridge.user.dto;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterUserRequestDto(
        @NotBlank @Size(max = 255) String fullName,
        @NotBlank @Size(max = 255) @Email String email,
        @NotBlank @Size(max = 255) String address,
        @NotBlank @Size(max = 255) String username,
        @NotNull @Size(min = 8, max = 255) String password
) {
}

