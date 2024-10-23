package com.example.enigma.task_2.model.entity.task_dto;

import com.example.enigma.task_2.model.TaskStatus;

import java.time.LocalDate;

public record TaskWithoutUserDto(
        Long id,
        String title,
        String description,
        TaskStatus taskStatus,
        LocalDate deadline) {
}