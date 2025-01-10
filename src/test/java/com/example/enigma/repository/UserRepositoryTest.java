package com.example.enigma.repository;


import com.example.enigma.exception.ErrorMessage;
import com.example.enigma.exception.user.UserNotFoundException;
import com.example.enigma.model.Role;
import com.example.enigma.model.TaskStatus;
import com.example.enigma.model.entity.Task;
import com.example.enigma.model.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void shouldCreateUserTest() {
        User user =User.builder()
                .firstName("name1")
                .lastName("last1")
                .email("duplicate@example.com")
                .password("pass")
                .role( Role.ROLE_ADMIN)
                .build();
        User savedUser = userRepository.save(user);
        Assertions.assertThat(savedUser.getId()).isPositive();
    }

    @Test
    void shouldGetUserTest() {
        long searchedUserId = 2L;
        User user = userRepository.findById(searchedUserId)
                .orElseThrow(() -> new UserNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, searchedUserId)));
        Assertions.assertThat(user.getId()).isEqualTo(searchedUserId);
        Assertions.assertThat(user.getFirstName()).isEqualTo("user");
        Assertions.assertThat(user.getLastName()).isEqualTo("user");
        Assertions.assertThat(user.getEmail()).isEqualTo("user1@wp.pl");
    }

    @Test
    void shouldGetListOfUserTest() {
        List<User> users = userRepository.findAll();
        Assertions.assertThat(users).isNotEmpty();
    }

    @Test
    void shouldUpdateUserTest() {
        long searchedUserId = 1L;
        String newEmail = "email@example.com";
        User user = userRepository.findById(searchedUserId)
                .orElseThrow(() -> new UserNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, searchedUserId)));
        user.setEmail(newEmail);
        User userUpdated = userRepository.save(user);
        Assertions.assertThat(userUpdated.getEmail()).isEqualTo(newEmail);
    }

    @Test
    void shouldDeleteUserTest() {
        userRepository.deleteById(1L);
        Optional<User> userOptional = userRepository.findById(1L);
        Assertions.assertThat(userOptional).isEmpty();
    }

    @Test
    void shouldFindTaskByTitleTest() {
        long searchedUserId = 1L;
        User user = userRepository.findById(searchedUserId)
                .orElseThrow(() -> new UserNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, searchedUserId)));
        String email = user.getEmail();
        User userByEmail = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_BY_EMAIL, email)));
        Assertions.assertThat(user).isEqualTo(userByEmail);
    }

    @Test
    void shouldFindByOptionalNameAndLastNameWithTasksTest() {
        PageRequest pageRequest = PageRequest.of(0, 5);
        List<User> users = userRepository.findByOptionalNameAndLastNameWithTasks("user", "user", pageRequest);

        Assertions.assertThat(users).hasSize(1);
        Assertions.assertThat(users.getFirst().getFirstName()).isEqualTo("user");
        Assertions.assertThat(users.getFirst().getLastName()).isEqualTo("user");
        Assertions.assertThat(users.getFirst().getTasks()).isNotEmpty();
    }

    @Test
    void shouldFindByOptionalNameAndLastNameWithTasks_NoLastNameTest() {
        PageRequest pageRequest = PageRequest.of(0, 5);
        List<User> users = userRepository.findByOptionalNameAndLastNameWithTasks("userek", null, pageRequest);

        Assertions.assertThat(users).hasSize(1);
        Assertions.assertThat(users.stream().map(User::getFirstName)).contains("userek");
    }

    @Test
    void shouldFindByOptionalNameAndLastNameWithTasks_AllUsersTest() {
        PageRequest pageRequest = PageRequest.of(0, 5);
        List<User> users = userRepository.findByOptionalNameAndLastNameWithTasks(null, null, pageRequest);

        Assertions.assertThat(users).hasSize(5);
        Assertions.assertThat(users.stream()
                .filter(user -> user.getId() != 1L)
                .findFirst()
                .orElseThrow()
                .getTasks()).isNotEmpty();
    }

    @Test
    void shouldFindByOptionalNameAndLastNameTest() {
        List<User> users = userRepository.findByOptionalNameAndLastName("user", "user");

        Assertions.assertThat(users).hasSize(1);
        Assertions.assertThat(users.getFirst().getFirstName()).isEqualTo("user");
        Assertions.assertThat(users.getFirst().getLastName()).isEqualTo("user");
        Assertions.assertThat(users.getFirst().getEmail()).isEqualTo("user1@wp.pl");
    }

    @Test
    void shouldFindByOptionalNameAndLastName_NoLastNameTest() {
        List<User> users = userRepository.findByOptionalNameAndLastName("user", null);

        Assertions.assertThat(users).hasSize(3);
        Assertions.assertThat(users.stream().map(User::getFirstName)).contains("user");
    }

    @Test
    void shouldNotCreateUserWithNullValuesTest() {
        User user = User.builder()
                .firstName(null)
                .lastName(null)
                .email(null)
                .build();
        Assertions.assertThatThrownBy(() -> userRepository.save(user))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldNotAllowDuplicateEmailsTest() {
        User user1 =User.builder()
                .firstName("name1")
                .lastName("last1")
                .email("duplicate@example.com")
                .password("pass")
                .role( Role.ROLE_ADMIN)
                .build();
        userRepository.save(user1);

        User user2 = User.builder()
                .firstName("name2")
                .lastName("last2")
                .email("duplicate@example.com")
                .build();
        Assertions.assertThatThrownBy(() -> userRepository.save(user2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldHandleUserWithMultipleTasksTest() {
        User user = User.builder()
                .firstName("name1")
                .lastName("last1")
                .email("duplicate@example.com")
                .password("pass")
                .role( Role.ROLE_ADMIN)
                .build();
        userRepository.save(user);
        Task task1 = Task.builder()
                .title("Task1")
                .description("Desc1")
                .taskStatus(TaskStatus.TO_DO)
                .deadline(LocalDate.now())
                .build();
        Task task2 = Task.builder()
                .title("Task2")
                .description("Desc2")
                .taskStatus(TaskStatus.DONE)
                .deadline(LocalDate.now())
                .build();
        user.addTask(task1);
        user.addTask(task2);

        taskRepository.save(task1);
        taskRepository.save(task2);

        User retrievedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Assertions.assertThat(retrievedUser.getTasks()).hasSize(2);
    }

    @Test
    void shouldFindUserByEmailTest() {
        User user = User.builder()
                .firstName("Email")
                .lastName("Test")
                .email("unique@example.com")
                .password("pass")
                .role( Role.ROLE_ADMIN)
                .build();
        userRepository.save(user);

        User retrievedUser = userRepository.findByEmail("unique@example.com")
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Assertions.assertThat(retrievedUser.getEmail()).isEqualTo("unique@example.com");
    }
}
