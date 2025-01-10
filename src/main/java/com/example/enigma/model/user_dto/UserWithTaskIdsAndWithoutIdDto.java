package com.example.enigma.model.user_dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UserWithTaskIdsAndWithoutIdDto(
        @NotBlank(message = "first name cannot be blank")
        String firstName,
        @NotBlank(message = "last name cannot be blank")
        String lastName,
        @Email(regexp = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "Invalid email format")
        String email,
        List<Long> taskIds) {
}