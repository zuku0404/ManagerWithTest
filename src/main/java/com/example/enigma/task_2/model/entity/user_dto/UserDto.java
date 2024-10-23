package com.example.enigma.task_2.model.entity.user_dto;

import com.example.enigma.task_2.model.entity.task_dto.TaskWithoutUserDto;

import java.util.List;

public record UserDto(
        Long id,
        String name,
        String lastName,
        String email,
        List<TaskWithoutUserDto> tasks) {
}