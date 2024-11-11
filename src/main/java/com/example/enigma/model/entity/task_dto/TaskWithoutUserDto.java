package com.example.enigma.model.entity.task_dto;

import com.example.enigma.model.TaskStatus;

import java.time.LocalDate;

public record TaskWithoutUserDto(
        Long id,
        String title,
        String description,
        TaskStatus taskStatus,
        LocalDate deadline) {
}