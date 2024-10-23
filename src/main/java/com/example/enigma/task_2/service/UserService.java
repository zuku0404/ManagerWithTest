package com.example.enigma.task_2.service;

import com.example.enigma.task_2.model.entity.Task;
import com.example.enigma.task_2.model.entity.user_dto.UserDto;
import com.example.enigma.task_2.model.entity.user_dto.UserWithTaskIdsAndWithoutIdDto;
import com.example.enigma.task_2.model.entity.user_dto.UserWithoutIdAndTasksDto;
import com.example.enigma.task_2.model.entity.user_dto.mapper.UserDtoMapper;
import com.example.enigma.task_2.model.entity.user_dto.UserWithoutTaskDto;
import com.example.enigma.task_2.exception.ErrorMessage;
import com.example.enigma.task_2.exception.user.EmailAlreadyExist;
import com.example.enigma.task_2.exception.user.UserNotFoundException;
import com.example.enigma.task_2.repository.TaskRepository;
import com.example.enigma.task_2.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    public static final int PAGE_SIZE = 2;

    public List<UserDto> findAllDetailed(String name, String lastName, int page) {
        int pageNumber = page >= 1 ? page - 1 : 0;
        return UserDtoMapper.mapToUserDtos(
                userRepository.findByOptionalNameAndLastNameWithTasks(name, lastName, PageRequest.of(pageNumber, PAGE_SIZE)));
    }

    public List<UserWithoutTaskDto> findAllBasic(String name, String lastName) {
        return UserDtoMapper.mapToUserWithoutTaskDtos(userRepository.findByOptionalNameAndLastName(name, lastName));
    }

    public UserDto findUserById(Long id) {
        return UserDtoMapper.mapToUserDto(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, id))));
    }

    public UserDto findUserByEmail(String email) {
        return UserDtoMapper.mapToUserDto(userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_BY_EMAIL, email))));
    }

    @Transactional
    public UserWithoutTaskDto create(UserWithoutIdAndTasksDto newUser) {
        String email = newUser.email();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExist(String.format(ErrorMessage.EMAIL_ALREADY_EXISTS));
        }
        return UserDtoMapper.mapToUserWithoutTaskDto(
                userRepository.save(UserDtoMapper.mapUserWithoutIdDtoToUser(newUser)));
    }

    @Transactional
    public UserDto update(Long userId, UserWithTaskIdsAndWithoutIdDto updatedUser) {
        return userRepository.findById(userId)
                .map(user -> {
                    if (!user.getEmail().equals(updatedUser.email()) &&
                            userRepository.findByEmail(updatedUser.email()).isPresent()) {
                        throw new EmailAlreadyExist(String.format(ErrorMessage.EMAIL_ALREADY_EXISTS));
                    }
                    List<Long> oldTaskIds = user.getTasks().stream()
                            .map(Task::getId)
                            .toList();

                    List<Long> taskIdsToRemove = oldTaskIds.stream()
                            .filter(oldTaskId -> !updatedUser.taskIds().contains(oldTaskId))
                            .toList();

                    List<Task> taskToRemove = taskRepository.findAllById(taskIdsToRemove);
                    taskToRemove.forEach(user::removeTask);

                    List<Long> taskIdsToAdd = updatedUser.taskIds().stream()
                            .filter(newTaskId -> !oldTaskIds.contains(newTaskId))
                            .toList();

                    List<Task> taskToAdd = taskRepository.findAllById(taskIdsToAdd);
                    taskToAdd.forEach(user::addTask);

                    user.setName(updatedUser.name());
                    user.setEmail(updatedUser.email());
                    user.setLastName(updatedUser.lastName());

                    return UserDtoMapper.mapToUserDto(userRepository.save(user));
                }).orElseThrow(() -> new UserNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, userId)));
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
