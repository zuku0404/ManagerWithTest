package com.example.enigma.task_2.model.entity.user_dto;

public record UserWithoutIdAndTasksDto(
        String name,
        String lastName,
        String email) {
}
