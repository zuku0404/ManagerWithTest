package com.example.enigma.model.user_dto;

import com.example.enigma.model.Role;
import com.example.enigma.model.task_dto.TaskWithoutUserDto;

import java.util.List;

public record UserDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        Role role,
        List<TaskWithoutUserDto> tasks) {
}