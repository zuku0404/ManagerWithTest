package com.example.enigma.model.task_dto;

import com.example.enigma.model.TaskStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

public record TaskWithoutIdDto(
        @NotBlank(message = "title cannot be blank")
        String title,
        @NotBlank(message = "description cannot be blank")
        String description,
        TaskStatus taskStatus,
        @Future(message = "Deadline must be in the future")
        LocalDate deadline,
        List<Long> usersIds) {
}
