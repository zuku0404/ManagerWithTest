package com.example.enigma.task_2.repository;


import com.example.enigma.task_2.model.entity.User;
import com.example.enigma.task_2.exception.ErrorMessage;
import com.example.enigma.task_2.exception.user.UserNotFoundException;
import com.example.enigma.task_2.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCreateUserTest() {
        User user = new User("user_name", "user_lastName", "xxx@example.com");
        userRepository.save(user);
        Assertions.assertThat(user.getId()).isPositive();
    }

    @Test
    void shouldGetUserTest() {
        long searchedUserId = 1L;
        User user = userRepository.findById(searchedUserId)
                .orElseThrow(() -> new UserNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, searchedUserId)));
        Assertions.assertThat(user.getId()).isEqualTo(1L);
        Assertions.assertThat(user.getName()).isEqualTo("name_1");
        Assertions.assertThat(user.getLastName()).isEqualTo("lastName_1");
        Assertions.assertThat(user.getEmail()).isEqualTo("name.lastName1@example.com");
    }

    @Test
    void shouldGetListOfUserTest() {
        List<User> users = userRepository.findAll();
        Assertions.assertThat(users).isNotEmpty();
    }

    @Test
    void shouldUpdateTaskTest() {
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
        List<User> users = userRepository.findByOptionalNameAndLastNameWithTasks("name_1", "lastName_1", pageRequest);

        Assertions.assertThat(users).hasSize(1);
        Assertions.assertThat(users.getFirst().getName()).isEqualTo("name_1");
        Assertions.assertThat(users.getFirst().getLastName()).isEqualTo("lastName_1");
        Assertions.assertThat(users.getFirst().getTasks()).isNotEmpty();
    }

    @Test
    void shouldFindByOptionalNameAndLastNameWithTasks_NoLastNameTest() {
        PageRequest pageRequest = PageRequest.of(0, 5);
        List<User> users = userRepository.findByOptionalNameAndLastNameWithTasks("name_1", null, pageRequest);

        Assertions.assertThat(users).hasSize(1);
        Assertions.assertThat(users.stream().map(User::getName)).contains("name_1");
    }

    @Test
    void shouldFindByOptionalNameAndLastNameWithTasks_AllUsersTest() {
        PageRequest pageRequest = PageRequest.of(0, 5);
        List<User> users = userRepository.findByOptionalNameAndLastNameWithTasks(null, null, pageRequest);

        Assertions.assertThat(users).hasSize(5); // Assume there are 5 users in the DB
        Assertions.assertThat(users.getFirst().getTasks()).isNotEmpty(); // Ensure tasks are fetched
    }

    @Test
    void shouldFindByOptionalNameAndLastNameTest() {
        List<User> users = userRepository.findByOptionalNameAndLastName("name_1", "lastName_1");

        Assertions.assertThat(users).hasSize(1);
        Assertions.assertThat(users.getFirst().getName()).isEqualTo("name_1");
        Assertions.assertThat(users.getFirst().getLastName()).isEqualTo("lastName_1");
    }

    @Test
    void shouldFindByOptionalNameAndLastName_NoLastNameTest() {
        List<User> users = userRepository.findByOptionalNameAndLastName("name_10", null);

        Assertions.assertThat(users).hasSize(2);
        Assertions.assertThat(users.stream().map(User::getName)).contains("name_10");
    }
}
