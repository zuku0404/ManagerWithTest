package com.example.enigma.model.entity.user_dto;

public record UserWithoutIdAndTasksDto(
        String name,
        String lastName,
        String email) {
}
