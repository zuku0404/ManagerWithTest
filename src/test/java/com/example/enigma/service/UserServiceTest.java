package com.example.enigma.service;

import com.example.enigma.exception.ErrorMessage;
import com.example.enigma.exception.user.EmailAlreadyExist;
import com.example.enigma.exception.user.UserNotFoundException;
import com.example.enigma.model.Role;
import com.example.enigma.model.entity.Task;
import com.example.enigma.model.entity.User;
import com.example.enigma.model.task_dto.TaskWithoutUserDto;
import com.example.enigma.model.user_dto.UserDto;
import com.example.enigma.model.user_dto.UserPasswordUpdateDto;
import com.example.enigma.model.user_dto.UserWithTaskIdsAndWithoutIdDto;
import com.example.enigma.model.user_dto.UserWithoutTaskDto;
import com.example.enigma.model.user_dto.mapper.AdminPasswordUpdateDto;
import com.example.enigma.model.user_dto.mapper.UserDtoMapper;
import com.example.enigma.repository.TaskRepository;
import com.example.enigma.repository.UserRepository;
import com.example.enigma.sample.TaskUserSampleData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    TaskRepository taskRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    private List<Task> tasks;
    private List<User> users;
    int pageSize;

    @BeforeEach
    public void setup() {
        pageSize = UserService.PAGE_SIZE;
        TaskUserSampleData.createDataSet();
        users = TaskUserSampleData.users;
        tasks = TaskUserSampleData.tasks;
    }

    @Nested
    @DisplayName("Tests for findAllDetailed")
    class FindAllDetailed {

        @Test
        void shouldReturnAllUsersWhenNoFiltersProvided() {
            String name = null;
            String lastName = null;
            runFindAllDetailedTest(name, lastName);
        }

        @Test
        void shouldFilterUsersByName() {
            String name = "miriam";
            String lastName = null;
            runFindAllDetailedTest(name, lastName);
        }

        @Test
        void shouldFilterUsersByLastName() {
            String name = null;
            String lastName = "miriam";
            runFindAllDetailedTest(name, lastName);
        }

        @Test
        void shouldFilterUsersByNameAndLastName() {
            String name = "miriam";
            String lastName = "miriam";
            runFindAllDetailedTest(name, lastName);
        }

        @Test
        void shouldHandleEmptyResultWhenNameDoesNotExist() {
            String name = "name_not_exist";
            String lastName = null;
            runFindAllDetailedTest(name, lastName);
        }

        @Test
        void shouldHandleEmptyResultWhenLastNameDoesNotExist() {
            String name = null;
            String lastName = "name_not_exist";
            runFindAllDetailedTest(name, lastName);
        }

        @Test
        void shouldHandleEmptyResultWhenBothNameAndLastNameDoNotExist() {
            String name = "name_not_exist";
            String lastName = "lastname_not_exist";
            runFindAllDetailedTest(name, lastName);
        }

        private void runFindAllDetailedTest(String name, String lastName) {
            int pageNumber = 1;
            List<User> expectedUsers;
            do {
                expectedUsers = users.stream()
                        .filter(user -> name == null || user.getFirstName().equals(name))
                        .filter(user -> lastName == null || user.getLastName().equals(lastName))
                        .skip((long) (pageNumber - 1) * pageSize)
                        .limit(pageSize)
                        .toList();
                PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
                given(userRepository.findByOptionalNameAndLastNameWithTasks(name, lastName, pageRequest)).willReturn(expectedUsers);
                List<UserDto> expectedResult = UserDtoMapper.mapToUserDtos(expectedUsers);
                List<UserDto> result = userService.findAllDetailed(name, lastName, pageNumber);

                assertThat(result).isEqualTo(expectedResult);
                pageNumber++;
            } while (expectedUsers.size() == pageSize);
        }
    }

    @Nested
    @DisplayName("Tests for findAllBasic")
    class FindAllBasic {

        @Test
        void shouldReturnAllUsersWhenNoFiltersProvided() {
            String name = null;
            String lastName = null;
            runFindAllBasicTest(name, lastName);
        }

        @Test
        void shouldFilterUsersByName() {
            String name = "miriam";
            String lastName = null;
            runFindAllBasicTest(name, lastName);
        }

        @Test
        void shouldFilterUsersByLastName() {
            String name = null;
            String lastName = "miriam";
            runFindAllBasicTest(name, lastName);
        }

        @Test
        void shouldFilterUsersByNameAndLastName() {
            String name = "miriam";
            String lastName = "miriam";
            runFindAllBasicTest(name, lastName);
        }

        @Test
        void shouldHandleEmptyResultWhenNameDoesNotExist() {
            String name = "name_not_exist";
            String lastName = null;
            runFindAllBasicTest(name, lastName);
        }

        @Test
        void shouldHandleEmptyResultWhenLastNameDoesNotExist() {
            String name = null;
            String lastName = "name_not_exist";
            runFindAllBasicTest(name, lastName);
        }

        @Test
        void shouldHandleEmptyResultWhenBothNameAndLastNameDoNotExist() {
            String name = "name_not_exist";
            String lastName = "lastname_not_exist";
            runFindAllBasicTest(name, lastName);
        }

        private void runFindAllBasicTest(String name, String lastName) {
            List<User> expectedUsers = users.stream().toList();
            given(userRepository.findByOptionalNameAndLastName(name, lastName)).willReturn(expectedUsers);
            List<UserWithoutTaskDto> expectedResult = UserDtoMapper.mapToUserWithoutTaskDtos(expectedUsers);
            List<UserWithoutTaskDto> result = userService.findAllBasic(name, lastName);
            assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("Tests for findUserById")
    class FindUserById {
        void findUserByIdShouldReturnUserWhenUserExists() {
            long searchedId = 1;
            Optional<User> expectedUser = users.stream()
                    .filter(user -> user.getId() == searchedId)
                    .findFirst();
            given(userRepository.findById(searchedId)).willReturn(expectedUser);
            UserDto expectedResult = UserDtoMapper.mapToUserDto(expectedUser.orElseThrow());
            UserDto result = userService.findUserById(searchedId);

            assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void findUserByIdShouldThrowExceptionWhenUserNotFound() {
            long searchedId = 187;
            given(userRepository.findById(searchedId)).willReturn(Optional.empty());
            UserNotFoundException exception = assertThrows(
                    UserNotFoundException.class,
                    () -> userService.findUserById(searchedId)
            );
            assertThat(exception.getMessage())
                    .isEqualTo(String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, searchedId));
        }
    }

    @Nested
    @DisplayName("Tests for findUserByEmail")
    class FindUserByEmail {
        void findUserByEmailShouldReturnUserWhenUserExists() {
            String searchedEmail = "alex@example.com";
            Optional<User> expectedUser = users.stream()
                    .filter(user -> user.getEmail().equals(searchedEmail))
                    .findFirst();
            given(userRepository.findByEmail(searchedEmail)).willReturn(expectedUser);
            UserDto expectedResult = UserDtoMapper.mapToUserDto(expectedUser.orElseThrow());
            UserDto result = userService.findUserByEmail(searchedEmail);

            assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void findUserByEmailShouldThrowExceptionWhenUserNotFound() {
            String searchedEmail = "notExistEmail";
            given(userRepository.findByEmail(searchedEmail)).willReturn(Optional.empty());
            UserNotFoundException exception = assertThrows(
                    UserNotFoundException.class,
                    () -> userService.findUserByEmail(searchedEmail)
            );
            assertThat(exception.getMessage())
                    .isEqualTo(String.format(ErrorMessage.USER_NOT_FOUND_BY_EMAIL, searchedEmail));
        }
    }

    @Nested
    @DisplayName("Tests for update")
    class UpdateUser {
        @Test
        void updateShouldThrowEmailAlreadyExistExceptionWhenEmailAlreadyInUse() {
            long updatedUserId = 1L;
            String email = "matihautameki@example.com";
            User user = User.builder()
                    .id(updatedUserId)
                    .firstName("mati")
                    .lastName("hautameki")
                    .email("ma@example.com")
                    .password("user")
                    .role(Role.ROLE_USER)
                    .build();

            User anotherUser = User.builder()
                    .firstName("antonio")
                    .lastName("banderas")
                    .email(email)
                    .build();

            given(userRepository.findByEmail(email)).willReturn(Optional.of(anotherUser));
            UserWithTaskIdsAndWithoutIdDto userDto = UserDtoMapper.mapToUserWithTaskIdsAndWithoutIdDto(
                    User.builder()
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .email(email)
                            .build()
            );
            EmailAlreadyExist exception = assertThrows(
                    EmailAlreadyExist.class,
                    () -> userService.updateUserData(user, userDto)
            );
            assertThat(exception.getMessage())
                    .isEqualTo(String.format(ErrorMessage.EMAIL_ALREADY_EXISTS));
        }

        @Test
        void updateShouldUpdateUserWhenValidDataProvided() {
            long updatedUserId = 1;
            List<Long> listTaskToAddIds = Arrays.asList(1L, 2L, 3L);
            String email = "matihautameki@example.com";
            Set<Task> tasksToAdd = tasks.stream()
                    .filter(task -> listTaskToAddIds.contains(task.getId()))
                    .collect(Collectors.toSet());
            User user = User.builder()
                    .id(updatedUserId)
                    .firstName("mati")
                    .lastName("hautameki")
                    .email("ma@example.com")
                    .password("user")
                    .role( Role.ROLE_USER)
                    .build();

            User updatedUser = User.builder()
                    .id(updatedUserId)
                    .firstName("matiXXXX")
                    .lastName("hautamekiXXXX")
                    .email(email)
                    .password("user")
                    .role( Role.ROLE_USER)
                    .build();
            tasksToAdd.forEach(updatedUser::addTask);

            given(userRepository.findUserWithTasksById(updatedUserId)).willReturn(user);
            given(userRepository.save(ArgumentMatchers.any(User.class))).willReturn(updatedUser);
            UserWithTaskIdsAndWithoutIdDto userDto = UserDtoMapper.mapToUserWithTaskIdsAndWithoutIdDto(user);
            UserDto expectedResult = UserDtoMapper.mapToUserDto(updatedUser);
            UserDto result = userService.updateUserData(user, userDto);
            assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void updateShouldNotUpdateIfEmailIsTheSame() {
            long updatedUserId = 1L;
            String email = "ma@example.com";
            User user = User.builder()
                    .id(updatedUserId)
                    .firstName("mati")
                    .lastName("hautameki")
                    .email(email)
                    .password("user")
                    .role(Role.ROLE_USER)
                    .build();

            UserWithTaskIdsAndWithoutIdDto updatedUserDto = new UserWithTaskIdsAndWithoutIdDto(
                    "mati", "hautameki", email, List.of(1L, 2L)
            );

            given(userRepository.findUserWithTasksById(updatedUserId)).willReturn(user);
            given(userRepository.save(ArgumentMatchers.any(User.class))).willReturn(user);

            UserDto result = userService.updateUserData(user, updatedUserDto);

            assertThat(result.email()).isEqualTo(email);
        }

        @Test
        void updateShouldUpdateUserWithoutChangingTasksIfNoTaskIdsProvided() {
            long updatedUserId = 1L;

            User user = User.builder()
                    .id(updatedUserId)
                    .firstName("mati")
                    .lastName("hautameki")
                    .email("ma@example.com")
                    .password("user")
                    .role(Role.ROLE_USER)
                    .build();
            UserWithTaskIdsAndWithoutIdDto updatedUserDto = new UserWithTaskIdsAndWithoutIdDto(
                    "mati", "hautameki", "ma@example.com", null
            );

            given(userRepository.findUserWithTasksById(updatedUserId)).willReturn(user);
            given(userRepository.save(ArgumentMatchers.any(User.class))).willReturn(user);

            UserDto result = userService.updateUserData(user, updatedUserDto);

            assertThat(result.tasks().stream().map(TaskWithoutUserDto::id).toList()).isEmpty();
        }

        @Test
        void updateShouldNotAddWhenTaskNotFound() {
            long updatedUserId = 1L;
            User user = users.stream().filter(u -> u.getId() == updatedUserId).findFirst().orElseThrow();
            List<Long> newTaskIds = List.of(999L);

            UserWithTaskIdsAndWithoutIdDto updatedUserDto = new UserWithTaskIdsAndWithoutIdDto(
                    "mati", "hautameki", user.getEmail(), newTaskIds
            );

            User updatedUser = User.builder()
                    .id(user.getId())
                    .firstName(updatedUserDto.firstName())
                    .lastName(updatedUserDto.lastName())
                    .email(updatedUserDto.email())
                    .password(user.getPassword())
                    .role(user.getRole())
                    .build();

            given(userRepository.findUserWithTasksById(updatedUserId)).willReturn(user);
            List<Task> currentTasks = user.getTasks().stream().toList();
            given(taskRepository.findAllById(anyList())).willReturn(currentTasks);
            given(taskRepository.findAllById(newTaskIds)).willReturn(Collections.emptyList());
            given(userRepository.save(any())).willReturn(updatedUser);
            UserDto result = userService.updateUserData(user, updatedUserDto);

            assertThat(result.tasks()).isEmpty();
            assertThat(user.getTasks()).isEmpty();
            verify(taskRepository, times(2)).findAllById(anyList());
        }

        @Test
        void updateShouldRemoveTaskFromUserIfTaskIdNoLongerIncluded() {
            long updatedUserId = 1L;
            List<Long> updatedTaskIds = List.of(1L);
            User user = users.stream()
                    .filter(u -> u.getId() == updatedUserId)
                    .findFirst()
                    .orElseThrow();

            UserWithTaskIdsAndWithoutIdDto updatedUserDto = new UserWithTaskIdsAndWithoutIdDto(
                    "mati", "hautameki", user.getEmail(), updatedTaskIds
            );

            List<Task> tasksToRemove = user.getTasks().stream()
                    .filter(task -> !updatedTaskIds.contains(task.getId()))
                    .toList();

            List<Task> tasksToKeep = user.getTasks().stream()
                    .filter(task -> updatedTaskIds.contains(task.getId()))
                    .toList();

            User updatedUser = User.builder()
                    .id(user.getId())
                    .firstName(updatedUserDto.firstName())
                    .lastName(updatedUserDto.lastName())
                    .email(updatedUserDto.email())
                    .password(user.getPassword())
                    .role(user.getRole())
                    .build();
            tasksToKeep.forEach(updatedUser::addTask);

            given(userRepository.findUserWithTasksById(updatedUserId)).willReturn(user);
            given(taskRepository.findAllById(anyList())).willReturn(tasksToRemove);
            given(taskRepository.findAllById(anyList())).willReturn(Collections.emptyList());
            given(userRepository.save(any())).willReturn(updatedUser);

            UserDto result = userService.updateUserData(user, updatedUserDto);

            assertThat(result.tasks().stream().map(TaskWithoutUserDto::id).toList()).containsExactlyInAnyOrder(1L);
            verify(taskRepository, times(2)).findAllById(anyList());
            verify(userRepository).save(any(User.class));
        }

        @Test
        void updateShouldThrowExceptionWhenDtoHasEmptyFields() {
            long updatedUserId = 1L;
            User user = User.builder()
                    .id(updatedUserId)
                    .firstName("mati")
                    .lastName("hautameki")
                    .email("ma@example.com")
                    .password("user")
                    .role(Role.ROLE_USER)
                    .build();

            UserWithTaskIdsAndWithoutIdDto updatedUserDto = new UserWithTaskIdsAndWithoutIdDto(
                    "", "", "", null
            );

            given(userRepository.findUserWithTasksById(updatedUserId)).willReturn(user);
            given(userRepository.save(any())).willThrow(new IllegalArgumentException("Invalid input data"));

            assertThatThrownBy(() -> userService.updateUserData(user, updatedUserDto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid input data");

            verify(userRepository).findUserWithTasksById(updatedUserId);
        }
    }

    @Nested
    @DisplayName("Tests for editUserPasswordByAdmin")
    class EditUserPasswordByAdmin {
        @Test
        void shouldEditUserPasswordByAdminWhenUserExists() {
            String email = "user@example.com";
            String newPassword = "new_password";
            AdminPasswordUpdateDto request = new AdminPasswordUpdateDto(email, newPassword);
            User user = new User();
            user.setEmail(email);
            user.setPassword("old_password");

            given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
            given(passwordEncoder.encode(newPassword)).willReturn("encoded_new_password");

            userService.editUserPasswordByAdmin(request);

            assertThat(user.getPassword()).isEqualTo("encoded_new_password");
            verify(userRepository).save(user);
        }

        @Test
        void shouldThrowExceptionWhenUserNotFoundByEmail() {
            String email = "user@example.com";
            String newPassword = "new_password";
            AdminPasswordUpdateDto request = new AdminPasswordUpdateDto(email, newPassword);

            given(userRepository.findByEmail(email)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.editUserPasswordByAdmin(request))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining(String.format(ErrorMessage.USER_NOT_FOUND_BY_EMAIL, request.email()));
        }
    }

    @Nested
    @DisplayName("Tests for editUserPasswordByUser")
    class EditUserPasswordByUser {
        @Test
        void shouldEditUserPasswordByUserWhenOldPasswordIsCorrect() {
            String oldPassword = "old_password";
            String newPassword = "new_password";
            User currentUser = new User();
            currentUser.setEmail("user@example.com");
            currentUser.setPassword(passwordEncoder.encode(oldPassword));

            UserPasswordUpdateDto request = new UserPasswordUpdateDto(oldPassword, newPassword);

            given(passwordEncoder.matches(oldPassword, currentUser.getPassword())).willReturn(true);
            given(passwordEncoder.encode(newPassword)).willReturn("encoded_new_password");

            userService.editUserPasswordByUser(currentUser, request);

            assertThat(currentUser.getPassword()).isEqualTo("encoded_new_password");
            verify(userRepository).save(currentUser);
        }

        @Test
        void shouldThrowExceptionWhenOldPasswordIsIncorrect() {
            String oldPassword = "wrong_old_password";
            String newPassword = "new_password";
            User currentUser = new User();
            currentUser.setEmail("user@example.com");
            currentUser.setPassword(passwordEncoder.encode("correct_old_password"));

            UserPasswordUpdateDto request = new UserPasswordUpdateDto(oldPassword, newPassword);

            given(passwordEncoder.matches(oldPassword, currentUser.getPassword())).willReturn(false);

            assertThatThrownBy(() -> userService.editUserPasswordByUser(currentUser, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(ErrorMessage.CURRENT_PASSWORD_INVALID);
        }
    }

    @Nested
    @DisplayName("Tests for delete")
    class DeleteUser {
        @Test
        void deleteUser() {
            Long userToDeleteId = 1L;
            willDoNothing().given(userRepository).deleteById(userToDeleteId);
            userService.delete(userToDeleteId);
            verify(userRepository, times(1)).deleteById(userToDeleteId);
        }
    }
}

