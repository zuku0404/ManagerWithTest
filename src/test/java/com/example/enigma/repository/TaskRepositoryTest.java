package com.example.enigma.repository;

import com.example.enigma.model.Role;
import com.example.enigma.model.entity.Task;
import com.example.enigma.exception.ErrorMessage;
import com.example.enigma.exception.task.TaskNotFoundException;
import com.example.enigma.model.TaskStatus;
import com.example.enigma.model.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCreateTaskTest() {
        Task task = Task.builder()
                .title("test_task")
                .description("test_test_description")
                .taskStatus(TaskStatus.DONE)
                .deadline(LocalDate.of(2024, 3, 21))
                .build();
        taskRepository.save(task);
        Assertions.assertThat(task.getId()).isPositive();
    }

    @Test
    void shouldGetTaskWithPredefinedDataTest() {
        Task task = Task.builder()
                .title("title_99")
                .description("description_1")
                .taskStatus(TaskStatus.DONE)
                .deadline(LocalDate.of(2024, 11, 1))
                .build();
        taskRepository.save(task);
        Task retrievedTask = taskRepository.findById(task.getId())
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        Assertions.assertThat(retrievedTask.getTitle()).isEqualTo(task.getTitle());
        Assertions.assertThat(retrievedTask.getDescription()).isEqualTo(task.getDescription());
    }

    @Test
    void shouldGetListOfTasksTest() {
        List<Task> tasks = taskRepository.findAll();
        Assertions.assertThat(tasks).isNotEmpty();
    }

    @Test
    void shouldUpdateTaskTest() {
        long searchedTaskId = 1L;
        String newTitle = "title";
        Task task = taskRepository.findById(searchedTaskId)
                .orElseThrow(() -> new TaskNotFoundException(String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, searchedTaskId)));
        task.setTitle(newTitle);
        Task taskUpdated = taskRepository.save(task);
        Assertions.assertThat(taskUpdated.getTitle()).isEqualTo(newTitle);
    }

    @Test
    void shouldDeleteTaskTest() {
        Long taskId = 1L;
        taskRepository.deleteById(taskId);
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        Assertions.assertThat(taskOptional).isEmpty();
    }

    @Test
    void shouldFindTaskByTitleTest() {
        long searchedTaskId = 1L;
        Task task = taskRepository.findById(searchedTaskId)
                .orElseThrow(() -> new TaskNotFoundException(String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, searchedTaskId)));
        String title = task.getTitle();
        Task taskByTittle = taskRepository.findByTitle(title)
                .orElseThrow(() -> new TaskNotFoundException(String.format(ErrorMessage.TASK_NOT_FOUND_BY_TITLE, title)));
        Assertions.assertThat(task).isEqualTo(taskByTittle);
    }

    @Test
    void shouldFindTasksForSpecificUserAndStatusDone() {
        Pageable pageRequest = PageRequest.of(0, 10, Sort.by("id"));
        List<Task> tasks = taskRepository.findByOptionalUserAndStatusTaskWithUser(2L, TaskStatus.DONE, pageRequest);
        Assertions.assertThat(tasks).hasSize(2);
        Assertions.assertThat(tasks.stream().map(Task::getTitle).toList()).isEqualTo(List.of("title_3", "title_9"));
    }

    @Test
    void shouldFindTasksForSpecificUserRegardlessOfStatus() {
        Pageable pageRequest = PageRequest.of(0, 10, Sort.by("id"));
        List<Task> tasks = taskRepository.findByOptionalUserAndStatusTaskWithUser(2L, null, pageRequest);
        Assertions.assertThat(tasks).hasSize(8);
        Assertions.assertThat(tasks.stream().map(Task::getTitle)).contains("title_1", "title_2", "title_3", "title_4", "title_6",
                "title_8", "title_9", "title_11");
    }

    @Test
    void shouldFindAllTasksRegardlessOfUserAndStatus() {
        Pageable pageRequest = PageRequest.of(0, 10, Sort.by("id"));
        List<Task> tasks = taskRepository.findByOptionalUserAndStatusTaskWithUser(null, null, pageRequest);
        Assertions.assertThat(tasks).hasSize(10);
    }

    @Test
    void shouldFindTasksForSpecificUserAndStatusDoneWithoutUserJoin() {
        Pageable pageRequest = PageRequest.of(0, 10, Sort.by("id"));
        List<Task> tasks = taskRepository.findByOptionalUserAndStatusTask(2L, TaskStatus.DONE, pageRequest);
        Assertions.assertThat(tasks).hasSize(2);
        Assertions.assertThat(tasks.stream().map(Task::getTitle).toList()).isEqualTo(List.of("title_3", "title_9"));
    }

    @Test
    void shouldFindTasksForSpecificUserRegardlessOfStatusWithoutUserJoin() {
        Pageable pageRequest = PageRequest.of(0, 10, Sort.by("id"));
        List<Task> tasks = taskRepository.findByOptionalUserAndStatusTask(2L, null, pageRequest);
        Assertions.assertThat(tasks).hasSize(8);
        Assertions.assertThat(tasks.stream().map(Task::getTitle)).contains("title_1", "title_2", "title_3", "title_4", "title_6",
                "title_8", "title_9", "title_11");
    }

    @Test
    void shouldFindAllTasksRegardlessOfUserAndStatusWithoutUserJoin() {
        Pageable pageRequest = PageRequest.of(1, 10, Sort.by("id"));
        List<Task> tasks = taskRepository.findByOptionalUserAndStatusTask(null, null, pageRequest);
        Assertions.assertThat(tasks).hasSize(2);
    }

    @Test
    void shouldReturnEmptyPageWhenNoResultsTest() {
        Pageable pageRequest = PageRequest.of(100, 10);
        List<Task> tasks = taskRepository.findByOptionalUserAndStatusTask(null, null, pageRequest);
        Assertions.assertThat(tasks).isEmpty();
    }

    @Test
    void shouldFindTasksWithoutUsersTest() {
        Task taskWithoutUser = Task.builder()
                .title("title_no_user")
                .description("desc_no_user")
                .taskStatus(TaskStatus.TO_DO)
                .deadline(LocalDate.of(2024, 1, 1))
                .build();
        taskRepository.save(taskWithoutUser);

        Pageable pageRequest = PageRequest.of(0, 10);
        List<Task> unsignedTasks = taskRepository.findUnsignedTasks(pageRequest);

        Assertions.assertThat(unsignedTasks).contains(taskWithoutUser);
    }

    @Test
    void shouldHandleInvalidInputsGracefullyTest() {
        Pageable pageRequest = PageRequest.of(0, 10);

        List<Task> tasks = taskRepository.findByOptionalUserAndStatusTask(null, null, pageRequest);
        Assertions.assertThat(tasks).isNotNull();

        List<Task> tasksInvalidUser = taskRepository.findByOptionalUserAndStatusTask(9999L, null, pageRequest);
        Assertions.assertThat(tasksInvalidUser).isEmpty();
    }

    @Test
    void shouldNotCreateTaskWithInvalidValuesTest() {
        Task task = Task.builder()
                .title(null)
                .description(null)
                .taskStatus(null)
                .deadline(null)
                .build();
        Assertions.assertThatThrownBy(() -> taskRepository.save(task))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldCreateTaskWithUsersTest() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password");
        user.setRole(Role.ROLE_ADMIN);
        userRepository.save(user);

        Task task = Task.builder()
                .title("task_with_user")
                .description("desc_with_user")
                .taskStatus(TaskStatus.TO_DO)
                .deadline(LocalDate.of(2025, 1, 1))
                .build();
        user.addTask(task);
        Task savedTask = taskRepository.save(task);

        Assertions.assertThat(savedTask.getUsers()).isNotEmpty();
        Assertions.assertThat(savedTask.getUsers().iterator().next().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void shouldUpdateTaskStatusTest() {
        Task task = Task.builder()
                .title("update_status_task")
                .description("update_status_desc")
                .taskStatus(TaskStatus.TO_DO)
                .deadline(LocalDate.of(2025, 1, 1))
                .build();
        task = taskRepository.save(task);

        task.setTaskStatus(TaskStatus.DONE);
        Task updatedTask = taskRepository.save(task);

        Assertions.assertThat(updatedTask.getTaskStatus()).isEqualTo(TaskStatus.DONE);
        Assertions.assertThat(updatedTask.getTitle()).isEqualTo("update_status_task");
    }

    @Test
    void shouldCreateTaskWithMultipleUsersTest() {
        User user1 =User.builder()
                .firstName("Alice")
                .lastName("Smith")
                .email("alice.smith@example.com")
                .password("password")
                .role(Role.ROLE_ADMIN)
                .build();

        User user2 =User.builder()
                .firstName("Bob")
                .lastName("Brown")
                .email("bob.brown@example.com")
                .password("password")
                .role(Role.ROLE_ADMIN)
                .build();
        userRepository.save(user1);
        userRepository.save(user2);
        Task task = Task.builder()
                .title("shared_task")
                .description("shared_task_desc")
                .taskStatus(TaskStatus.IN_PROGRESS)
                .deadline(LocalDate.of(2025, 1, 1))
                .build();
        user1.addTask(task);
        user2.addTask(task);
        Task savedTask = taskRepository.save(task);

        Assertions.assertThat(savedTask.getUsers()).hasSize(2);
    }

    @Test
    void shouldNotAllowDuplicateTaskTitlesTest() {
        Task task1 = Task.builder()
                .title("unique_title")
                .description("desc1")
                .taskStatus(TaskStatus.TO_DO)
                .deadline(LocalDate.of(2025, 1, 1))
                .build();

        taskRepository.save(task1);
        Task task2 = Task.builder()
                .title("unique_title")
                .description("desc2")
                .taskStatus(TaskStatus.DONE)
                .deadline(LocalDate.of(2025, 2, 1))
                .build();

        Assertions.assertThatThrownBy(() -> taskRepository.save(task2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldAssignAndUnassignUsersFromTaskTest() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password")
                .role( Role.ROLE_ADMIN)
                .build();
        userRepository.save(user);
        Task task = Task.builder()
                .title("assign_task")
                .description("desc_task")
                .taskStatus(TaskStatus.IN_PROGRESS)
                .deadline(LocalDate.of(2025, 1, 1))
                .build();
        taskRepository.save(task);
        user.addTask(task);
        Task updatedTask = taskRepository.save(task);

        Assertions.assertThat(updatedTask.getUsers()).contains(user);

        user.removeTask(task);
        updatedTask = taskRepository.save(task);

        Assertions.assertThat(updatedTask.getUsers()).doesNotContain(user);
    }
}
