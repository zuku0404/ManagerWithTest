package com.example.enigma.authentication;

import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank(message = "first firstName cannot be blank")
        String firstName,
        @NotBlank(message = "last name cannot be blank")
        String lastName,
        @Email(regexp = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "Invalid email format")
        String email,

        @NotNull(message = "Password cannot be null")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,}$",
                message = "Field must contain at least 8 characters long, contain at least one uppercase letter, one lowercase letter, and one special character"
        )
        String password) {
}
