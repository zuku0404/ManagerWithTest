package com.example.enigma.model.task_dto;

import com.example.enigma.model.TaskStatus;
import com.example.enigma.model.user_dto.UserWithoutTaskDto;

import java.time.LocalDate;
import java.util.List;

public record TaskDto(
        Long id,
        String title,
        String description,
        TaskStatus taskStatus,
        LocalDate deadline,
        List<UserWithoutTaskDto> users) {
}
