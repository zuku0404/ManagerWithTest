package com.example.enigma.model.user_dto;

import com.example.enigma.model.Action;

public record UserTaskActionRequest(
        Long userId,
        Action action) {
}
