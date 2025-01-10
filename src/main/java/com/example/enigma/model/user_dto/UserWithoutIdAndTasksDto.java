package com.example.enigma.model.user_dto;

public record UserWithoutIdAndTasksDto(
        String name,
        String lastName,
        String email) {
}
