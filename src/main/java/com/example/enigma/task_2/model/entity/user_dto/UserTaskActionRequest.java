package com.example.enigma.task_2.model.entity.user_dto;

import com.example.enigma.task_2.model.Action;

public record UserTaskActionRequest(
        Long userId,
        Action action) {
}
