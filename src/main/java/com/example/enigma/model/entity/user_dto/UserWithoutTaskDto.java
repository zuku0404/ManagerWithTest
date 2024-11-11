package com.example.enigma.model.entity.user_dto;

public record UserWithoutTaskDto(
        Long id,
        String name,
        String lastName,
        String email) {
}
