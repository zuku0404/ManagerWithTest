package com.example.enigma.task_2.service;

import com.example.enigma.task_2.model.entity.Task;
import com.example.enigma.task_2.model.entity.User;
import com.example.enigma.task_2.model.entity.task_dto.TaskDto;
import com.example.enigma.task_2.model.entity.task_dto.TaskWithoutIdDto;
import com.example.enigma.task_2.model.entity.task_dto.TaskWithoutUserDto;
import com.example.enigma.task_2.model.entity.task_dto.mapper.TaskDtoMapper;
import com.example.enigma.task_2.exception.ErrorMessage;
import com.example.enigma.task_2.exception.task.TaskNotFoundException;
import com.example.enigma.task_2.exception.task.TitleAlreadyExistsException;
import com.example.enigma.task_2.exception.user.UserAttachedException;
import com.example.enigma.task_2.exception.user.UserNotFoundException;
import com.example.enigma.task_2.model.Action;
import com.example.enigma.task_2.model.SortDirection;
import com.example.enigma.task_2.model.TaskStatus;
import com.example.enigma.task_2.model.entity.user_dto.UserTaskActionRequest;
import com.example.enigma.task_2.repository.TaskRepository;
import com.example.enigma.task_2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    public final static int PAGE_SIZE = 2;

    public List<TaskDto> findAllDetailed(Long userId, TaskStatus status, int page, boolean sort, SortDirection sortDirection) {
        Pageable pageable = createPageable(page, sort, sortDirection);
        return TaskDtoMapper.mapToTaskDtos(
                taskRepository.findByOptionalUserAndStatusTaskWithUser(userId, status, pageable));
    }

    public List<TaskWithoutUserDto> findAllBasic(Long userId, TaskStatus status, boolean sort, SortDirection sortDirection) {
        return TaskDtoMapper.mapToTaskWithoutUserDtos(
                taskRepository.findByOptionalUserAndStatusTask(userId, status, PageRequest.of(0, Integer.MAX_VALUE, createSort(sort, sortDirection))));
    }

    public List<TaskWithoutUserDto> findUnsigned(int page, boolean sort, SortDirection sortDirection) {
        Pageable pageable = createPageable(page, sort, sortDirection);
        return TaskDtoMapper.mapToTaskWithoutUserDtos(taskRepository.findUnsignedTasks(pageable));
    }

    private Pageable createPageable(int page, boolean sort, SortDirection sortDirection) {
        int pageNumber = page >= 1 ? page - 1 : 0;
        return PageRequest.of(pageNumber, PAGE_SIZE, createSort(sort, sortDirection));
    }

    private Sort createSort(boolean sort, SortDirection sortDirection) {
        Sort sorted = Sort.by("id");
        if (sort) {
            sorted = (sortDirection == SortDirection.DESC) ?
                    Sort.by("deadline").descending() : Sort.by("deadline").ascending();
        }
        return sorted;
    }

    public TaskDto findTaskById(Long id) {
        return TaskDtoMapper.mapToTaskDto(taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, id))));
    }

    public TaskDto findTaskByTitle(String title) {
        return TaskDtoMapper.mapToTaskDto(taskRepository.findByTitle(title)
                .orElseThrow(() -> new TaskNotFoundException(String.format(ErrorMessage.TASK_NOT_FOUND_BY_TITLE, title))));
    }

    @Transactional
    public TaskDto create(TaskWithoutIdDto newTask) {
        if (taskRepository.findByTitle(newTask.title()).isPresent()) {
            throw new TitleAlreadyExistsException(String.format(ErrorMessage.TITLE_ALREADY_EXISTS_WITH_TITLE, newTask.title()));
        }
        List<User> users = newTask.usersIds() == null ? new ArrayList<>() : userRepository.findAllById(newTask.usersIds());
        Task task = new Task(newTask.title(), newTask.description(), newTask.taskStatus(), newTask.deadline());
        users.forEach(user -> user.addTask(task));
        return TaskDtoMapper.mapToTaskDto(taskRepository.save(task));
    }

    @Transactional
    public TaskDto update(Long id, TaskWithoutIdDto updatedTask) {
        return taskRepository.findById(id)
                .map(task -> {
                    String newTitle = updatedTask.title();
                    if (!task.getTitle().equals(newTitle) &&
                            taskRepository.findByTitle(newTitle).isPresent()) {
                        throw new TitleAlreadyExistsException(String.format(ErrorMessage.TITLE_ALREADY_EXISTS_WITH_TITLE, updatedTask.title()));
                    }

                    List<Long> oldUsersIds = task.getUsers().stream()
                            .map(User::getId)
                            .toList();

                    List<Long> userIdsToRemove = oldUsersIds.stream()
                            .filter(oldUserId -> !updatedTask.usersIds().contains(oldUserId))
                            .toList();

                    List<Long> userIdsToAdd = updatedTask.usersIds().stream()
                            .filter(newUserId -> !oldUsersIds.contains(newUserId))
                            .toList();

                    List<User> usersToAdd = userRepository.findAllById(userIdsToAdd);
                    List<User> usersToRemove = userRepository.findAllById(userIdsToRemove);

                    usersToAdd.forEach(user -> user.addTask(task));
                    usersToRemove.forEach(user -> user.removeTask(task));

                    task.setTitle(updatedTask.title());
                    task.setDescription(updatedTask.description());
                    task.setTaskStatus(updatedTask.taskStatus());
                    task.setDeadline(updatedTask.deadline());
                    return TaskDtoMapper.mapToTaskDto(taskRepository.save(task));
                }).orElseThrow(() -> new TaskNotFoundException(String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, id)));
    }

    @Transactional
    public TaskDto changeStatus(Long id, TaskStatus taskStatus) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setTaskStatus(taskStatus);
                    return TaskDtoMapper.mapToTaskDto(taskRepository.save(task));
                }).orElseThrow(() -> new TaskNotFoundException(String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, id)));
    }

    @Transactional
    public TaskDto modifyUserAssignmentToTask(Long id, UserTaskActionRequest userTaskActionRequest) {
        return taskRepository.findById(id)
                .map(task -> {
                    List<Long> useIds = task.getUsers().stream().
                            map(User::getId)
                            .toList();
                    return switch (userTaskActionRequest.action()) {
                        case Action.ADD -> attachUser(useIds, userTaskActionRequest.userId(), task);
                        case Action.REMOVE -> detachUser(useIds, userTaskActionRequest.userId(), task);
                    };
                }).orElseThrow(() -> new TaskNotFoundException(String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, id)));
    }

    private TaskDto attachUser(List<Long> useIds, Long userId, Task task) {
        User user = getUser(userId);
        if (!useIds.contains(userId)) {
            user.addTask(task);
            return TaskDtoMapper.mapToTaskDto(taskRepository.save(task));
        } else {
            throw new UserAttachedException(String.format(ErrorMessage.USER_ALREADY_ATTACHED, userId));
        }
    }

    private TaskDto detachUser(List<Long> useIds, Long userId, Task task) {
        User user = getUser(userId);
        if (useIds.contains(userId)) {
            user.removeTask(task);
            return TaskDtoMapper.mapToTaskDto(taskRepository.save(task));
        } else {
            throw new UserAttachedException(String.format(ErrorMessage.USER_NOT_ATTACHED, userId));
        }
    }

    @Transactional
    public void delete(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, id)));
        new ArrayList<>(task.getUsers()).forEach(user -> user.removeTask(task));
        taskRepository.deleteById(id);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, userId)));
    }
}