package com.example.enigma.service;

import com.example.enigma.model.entity.Task;
import com.example.enigma.model.entity.User;
import com.example.enigma.model.user_dto.UserDto;
import com.example.enigma.model.user_dto.UserPasswordUpdateDto;
import com.example.enigma.model.user_dto.UserWithTaskIdsAndWithoutIdDto;
import com.example.enigma.model.user_dto.UserWithoutTaskDto;
import com.example.enigma.model.user_dto.mapper.UserDtoMapper;
import com.example.enigma.exception.ErrorMessage;
import com.example.enigma.exception.user.EmailAlreadyExist;
import com.example.enigma.exception.user.UserNotFoundException;
import com.example.enigma.model.user_dto.mapper.AdminPasswordUpdateDto;
import com.example.enigma.repository.TaskRepository;
import com.example.enigma.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;
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

    public UserDto findCurrentUser(User user) {
        return UserDtoMapper.mapToUserDto(
                userRepository.findById(user.getId())
                        .orElseThrow(() -> new UserNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, user.getId()))));
    }

    public UserDto findUserByEmail(String email) {
        return UserDtoMapper.mapToUserDto(userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_BY_EMAIL, email))));
    }

    @Transactional
    public UserDto updateUserData(User currentUser, UserWithTaskIdsAndWithoutIdDto updatedUser) {
        if (!currentUser.getEmail().equals(updatedUser.email()) &&
                userRepository.findByEmail(updatedUser.email()).isPresent()) {
            throw new EmailAlreadyExist(String.format(ErrorMessage.EMAIL_ALREADY_EXISTS));
        }
        currentUser = userRepository.findUserWithTasksById(currentUser.getId());
        if (updatedUser.taskIds() != null) {
            List<Long> oldTaskIds = currentUser.getTasks().stream()
                    .map(Task::getId)
                    .toList();

            List<Long> taskIdsToRemove = oldTaskIds.stream()
                    .filter(oldTaskId -> !updatedUser.taskIds().contains(oldTaskId))
                    .toList();

            List<Task> taskToRemove = taskRepository.findAllById(taskIdsToRemove);
            taskToRemove.forEach(currentUser::removeTask);

            List<Long> taskIdsToAdd = updatedUser.taskIds().stream()
                    .filter(newTaskId -> !oldTaskIds.contains(newTaskId))
                    .toList();

            List<Task> taskToAdd = taskRepository.findAllById(taskIdsToAdd);
            taskToAdd.forEach(currentUser::addTask);
        }

        currentUser.setFirstName(updatedUser.firstName());
        currentUser.setEmail(updatedUser.email());
        currentUser.setLastName(updatedUser.lastName());

        return UserDtoMapper.mapToUserDto(userRepository.save(currentUser));
    }

    public void editUserPasswordByAdmin(AdminPasswordUpdateDto request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_BY_EMAIL, request.email())));
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    public void editUserPasswordByUser(User currentUser, UserPasswordUpdateDto request) throws IllegalArgumentException {
        if (!passwordEncoder.matches(request.oldPassword(), currentUser.getPassword())) {
            throw new IllegalArgumentException(ErrorMessage.CURRENT_PASSWORD_INVALID);
        }

        currentUser.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(currentUser);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}