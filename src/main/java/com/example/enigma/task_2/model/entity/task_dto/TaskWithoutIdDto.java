package com.example.enigma.task_2.model.entity.task_dto;

import com.example.enigma.task_2.model.TaskStatus;

import java.time.LocalDate;
import java.util.List;

public record TaskWithoutIdDto(
        String title,
        String description,
        TaskStatus taskStatus,
        LocalDate deadline,
        List<Long> usersIds) {
}
