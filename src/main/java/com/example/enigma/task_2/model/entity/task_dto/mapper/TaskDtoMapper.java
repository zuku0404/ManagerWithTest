package com.example.enigma.task_2.model.entity.task_dto.mapper;

import com.example.enigma.task_2.model.entity.Task;
import com.example.enigma.task_2.model.entity.User;
import com.example.enigma.task_2.model.entity.task_dto.TaskDto;
import com.example.enigma.task_2.model.entity.task_dto.TaskWithoutIdDto;
import com.example.enigma.task_2.model.entity.task_dto.TaskWithoutUserDto;
import com.example.enigma.task_2.model.entity.user_dto.mapper.UserDtoMapper;

import java.util.List;

public class TaskDtoMapper {
    private TaskDtoMapper(){}


   public static List<TaskDto> mapToTaskDtos (List<Task> tasks){
        return tasks.stream()
                .map(TaskDtoMapper::mapToTaskDto)
                .toList();
   }

    public static TaskDto mapToTaskDto(Task task){
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getTaskStatus(),
                task.getDeadline(),
                UserDtoMapper.mapToUserWithoutTaskDtos(task.getUsers().stream().toList()));
    }

    public static List<TaskWithoutUserDto> mapToTaskWithoutUserDtos (List<Task> tasks){
        return tasks.stream()
                .map(TaskDtoMapper::mapToTaskWithoutUserDto)
                .toList();
    }

    public static TaskWithoutUserDto mapToTaskWithoutUserDto(Task task){
        return new TaskWithoutUserDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getTaskStatus(),
                task.getDeadline());
    }

    public static TaskWithoutIdDto mapToTaskWithoutIdDto(Task task){
        return new TaskWithoutIdDto(
                task.getTitle(),
                task.getDescription(),
                task.getTaskStatus(),
                task.getDeadline(),
                task.getUsers().stream()
                        .map(User::getId)
                        .toList());
    }
}
