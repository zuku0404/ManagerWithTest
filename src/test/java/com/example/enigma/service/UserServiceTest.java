package com.example.enigma.service;

import com.example.enigma.model.entity.Task;
import com.example.enigma.model.entity.User;
import com.example.enigma.model.entity.user_dto.UserDto;
import com.example.enigma.model.entity.user_dto.UserWithTaskIdsAndWithoutIdDto;
import com.example.enigma.model.entity.user_dto.UserWithoutIdAndTasksDto;
import com.example.enigma.model.entity.user_dto.UserWithoutTaskDto;
import com.example.enigma.model.entity.user_dto.mapper.UserDtoMapper;
import com.example.enigma.sample.TaskUserSampleData;
import com.example.enigma.exception.ErrorMessage;
import com.example.enigma.exception.user.EmailAlreadyExist;
import com.example.enigma.exception.user.UserNotFoundException;
import com.example.enigma.repository.TaskRepository;
import com.example.enigma.repository.UserRepository;
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

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    TaskRepository taskRepository;

    @InjectMocks
    UserService userService;

    private List<Task> tasks;
    private List<User> users;
    int pageSize;

    @BeforeEach
    public void setup() {
        pageSize = UserService.PAGE_SIZE;
        users = TaskUserSampleData.createUsers();
        tasks = TaskUserSampleData.createTasks();
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
                        .filter(user -> name == null || user.getName().equals(name))
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
    @DisplayName("Tests for create")
    class CreateUser {
        @Test
        void createUserShouldCreateUserWhenEmailDoesNotExist() {
            User user = new User("mati", "hautameki", "matihautameki@example.com");
            given(userRepository.save(ArgumentMatchers.any(User.class))).willReturn(user);
            given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.empty());
            UserWithoutIdAndTasksDto withoutIdAndTasksDto = UserDtoMapper.mapToUserWithoutIdAndTasksDto(user);
            UserWithoutTaskDto expectedResult = UserDtoMapper.mapToUserWithoutTaskDto(user);
            UserWithoutTaskDto result = userService.create(withoutIdAndTasksDto);

            assertThat(result.name()).isEqualTo(expectedResult.name());
            assertThat(result.lastName()).isEqualTo(expectedResult.lastName());
            assertThat(result.email()).isEqualTo(expectedResult.email());
        }

        @Test
        void createUserShouldThrowExceptionWhenEmailAlreadyExists() {
            User user = new User("mati", "hautameki", "matihautameki@example.com");
            given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
            UserWithoutIdAndTasksDto withoutIdAndTasksDto = UserDtoMapper.mapToUserWithoutIdAndTasksDto(user);
            EmailAlreadyExist exception = assertThrows(
                    EmailAlreadyExist.class,
                    () -> userService.create(withoutIdAndTasksDto)
            );
            assertThat(exception.getMessage())
                    .isEqualTo(String.format(ErrorMessage.EMAIL_ALREADY_EXISTS));
        }
    }

    @Nested
    @DisplayName("Tests for update")
    class UpdateUser {
        @Test
        void updateShouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
            long updatedUserId = 1L;
            User user = new User("mati", "hautameki", "ma@example.com");
            given(userRepository.findById(updatedUserId)).willReturn(Optional.empty());
            UserWithTaskIdsAndWithoutIdDto userDto = UserDtoMapper.mapToUserWithTaskIdsAndWithoutIdDto(user);
            UserNotFoundException exception = assertThrows(
                    UserNotFoundException.class,
                    () -> userService.update(updatedUserId, userDto)
            );
            assertThat(exception.getMessage())
                    .isEqualTo(String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, updatedUserId));
        }

        @Test
        void updateShouldThrowEmailAlreadyExistExceptionWhenEmailAlreadyInUse() {
            long updatedUserId = 1L;
            String email = "matihautameki@example.com";

            User user = new User(updatedUserId, "mati", "hautameki", "ma@example.com", new HashSet<>());
            User anotherUser = new User("antonio", "banderas", email);

            given(userRepository.findById(updatedUserId)).willReturn(Optional.of(user));
            given(userRepository.findByEmail(email)).willReturn(Optional.of(anotherUser));
            UserWithTaskIdsAndWithoutIdDto userDto = UserDtoMapper.mapToUserWithTaskIdsAndWithoutIdDto(
                    new User(user.getName(), user.getLastName(), email)
            );
            EmailAlreadyExist exception = assertThrows(
                    EmailAlreadyExist.class,
                    () -> userService.update(updatedUserId, userDto)
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
            User user = new User(updatedUserId, "mati", "hautameki", "ma@example.com", new HashSet<>());
            User updatedUser = new User(updatedUserId, "matiXXXX", "hautamekiXXXX", email, tasksToAdd);

            given(userRepository.findById(updatedUserId)).willReturn(Optional.of(user));
            given(userRepository.save(ArgumentMatchers.any(User.class))).willReturn(updatedUser);
            UserWithTaskIdsAndWithoutIdDto userDto = UserDtoMapper.mapToUserWithTaskIdsAndWithoutIdDto(user);
            UserDto expectedResult = UserDtoMapper.mapToUserDto(updatedUser);
            UserDto result = userService.update(updatedUserId, userDto);
            assertThat(result).isEqualTo(expectedResult);
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

