package com.example.enigma.model.user_dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserPasswordUpdateDto(
        String oldPassword,
        @NotNull(message = "password cannot be null")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,}$",
                message = "Field must be exactly 8 characters long, contain at least one uppercase letter, one lowercase letter, and one special character"
        )
        String newPassword
) {
}
