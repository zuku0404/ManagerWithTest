package com.example.enigma.task_2.model.entity.user_dto;

import java.util.List;

public record UserWithTaskIdsAndWithoutIdDto(
        String name,
        String lastName,
        String email,
        List<Long> taskIds) {
}