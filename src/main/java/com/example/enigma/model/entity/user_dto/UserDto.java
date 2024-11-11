package com.example.enigma.model.entity.user_dto;

import com.example.enigma.model.entity.task_dto.TaskWithoutUserDto;

import java.util.List;

public record UserDto(
        Long id,
        String name,
        String lastName,
        String email,
        List<TaskWithoutUserDto> tasks) {
}