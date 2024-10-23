package com.example.enigma.task_2.model.entity.user_dto.mapper;

import com.example.enigma.task_2.model.entity.Task;
import com.example.enigma.task_2.model.entity.User;
import com.example.enigma.task_2.model.entity.task_dto.mapper.TaskDtoMapper;
import com.example.enigma.task_2.model.entity.user_dto.UserDto;
import com.example.enigma.task_2.model.entity.user_dto.UserWithTaskIdsAndWithoutIdDto;
import com.example.enigma.task_2.model.entity.user_dto.UserWithoutIdAndTasksDto;
import com.example.enigma.task_2.model.entity.user_dto.UserWithoutTaskDto;

import java.util.List;

public class UserDtoMapper {
    private UserDtoMapper() {
    }

    public static User mapUserWithoutIdDtoToUser(UserWithoutIdAndTasksDto dto) {
        return new User(
                dto.name(),
                dto.lastName(),
                dto.email()
        );
    }

    public static List<UserWithoutTaskDto> mapToUserWithoutTaskDtos (List<User> users){
        return users.stream()
                .map(UserDtoMapper::mapToUserWithoutTaskDto)
                .toList();
    }

    public static UserWithoutTaskDto mapToUserWithoutTaskDto(User user) {
        return new UserWithoutTaskDto(
                user.getId(),
                user.getName(),
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
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                TaskDtoMapper.mapToTaskWithoutUserDtos(user.getTasks().stream().toList())
        );
    }

    public static UserWithoutIdAndTasksDto mapToUserWithoutIdAndTasksDto(User user){
        return new UserWithoutIdAndTasksDto(
                user.getName(),
                user.getLastName(),
                user.getEmail()
        );
    }

    public static UserWithTaskIdsAndWithoutIdDto mapToUserWithTaskIdsAndWithoutIdDto(User user){
        return new UserWithTaskIdsAndWithoutIdDto(
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                user.getTasks().stream().map(Task::getId).toList()
        );
    }
}
