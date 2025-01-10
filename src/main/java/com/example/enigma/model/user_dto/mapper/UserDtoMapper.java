package com.example.enigma.model.user_dto.mapper;

import com.example.enigma.model.entity.Task;
import com.example.enigma.model.entity.User;
import com.example.enigma.model.task_dto.mapper.TaskDtoMapper;
import com.example.enigma.model.user_dto.UserDto;
import com.example.enigma.model.user_dto.UserWithTaskIdsAndWithoutIdDto;
import com.example.enigma.model.user_dto.UserWithoutTaskDto;

import java.util.List;

public class UserDtoMapper {
    private UserDtoMapper() {
    }

    public static List<UserWithoutTaskDto> mapToUserWithoutTaskDtos (List<User> users){
        return users.stream()
                .map(UserDtoMapper::mapToUserWithoutTaskDto)
                .toList();
    }

    public static UserWithoutTaskDto mapToUserWithoutTaskDto(User user) {
        return new UserWithoutTaskDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }

    public static List<UserDto> mapToUserDtos (List<User> users){
        return users.stream()
                .map(UserDtoMapper::mapToUserDto)
                .toList();
    }

    public static UserDto mapToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                TaskDtoMapper.mapToTaskWithoutUserDtos(user.getTasks().stream().toList())
        );
    }

    public static UserWithTaskIdsAndWithoutIdDto mapToUserWithTaskIdsAndWithoutIdDto(User user){
        return new UserWithTaskIdsAndWithoutIdDto(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getTasks().stream().map(Task::getId).toList()
        );
    }
}
