package com.example.enigma.model.user_dto.mapper;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AdminPasswordUpdateDto(
        String email,
        @NotNull(message = "password cannot be null")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,}$",
                message = "Field must contain at least 8 characters long, contain at least one uppercase letter, one lowercase letter, and one special character"
        )
        String newPassword) {
}
