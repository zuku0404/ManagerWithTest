package com.example.enigma.repository;

import com.example.enigma.model.entity.Task;
import com.example.enigma.exception.ErrorMessage;
import com.example.enigma.exception.task.TaskNotFoundException;
import com.example.enigma.model.TaskStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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

    @Test
    void shouldCreateTaskTest() {
        Task task = new Task("test_task", "test_test_description", TaskStatus.DONE, LocalDate.of(2024, 3, 21));
        taskRepository.save(task);
        Assertions.assertThat(task.getId()).isPositive();
    }

    @Test
    void shouldGetTaskTest() {
        long searchedTaskId = 1L;
        Task task = taskRepository.findById(searchedTaskId)
                .orElseThrow(() -> new TaskNotFoundException(String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, searchedTaskId)));
        Assertions.assertThat(task.getId()).isEqualTo(1L);
        Assertions.assertThat(task.getTitle()).isEqualTo("title_1");
        Assertions.assertThat(task.getDescription()).isEqualTo("description_1");
        Assertions.assertThat(task.getTaskStatus()).isEqualTo(TaskStatus.TO_DO);
        Assertions.assertThat(task.getDeadline()).isEqualTo(LocalDate.of(2024, 11, 1));
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
        taskRepository.deleteById(1L);
        Optional<Task> taskOptional = taskRepository.findById(1L);
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
        List<Task> tasks = taskRepository.findByOptionalUserAndStatusTaskWithUser(1L, TaskStatus.DONE, pageRequest);
        Assertions.assertThat(tasks).hasSize(2);
        Assertions.assertThat(tasks.stream().map(Task::getTitle).toList()).isEqualTo(List.of("title_3", "title_9"));
    }

    @Test
    void shouldFindTasksForSpecificUserRegardlessOfStatus() {
        Pageable pageRequest = PageRequest.of(0, 10, Sort.by("id"));
        List<Task> tasks = taskRepository.findByOptionalUserAndStatusTaskWithUser(1L, null, pageRequest);
        Assertions.assertThat(tasks).hasSize(4);
        Assertions.assertThat(tasks.stream().map(Task::getTitle)).contains("title_1", "title_3", "title_5", "title_9");
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
        List<Task> tasks = taskRepository.findByOptionalUserAndStatusTask(1L, TaskStatus.DONE, pageRequest);
        Assertions.assertThat(tasks).hasSize(2);
        Assertions.assertThat(tasks.stream().map(Task::getTitle).toList()).isEqualTo(List.of("title_3", "title_9"));
    }

    @Test
    void shouldFindTasksForSpecificUserRegardlessOfStatusWithoutUserJoin() {
        Pageable pageRequest = PageRequest.of(0, 10, Sort.by("id"));
        List<Task> tasks = taskRepository.findByOptionalUserAndStatusTask(1L, null, pageRequest);
        Assertions.assertThat(tasks).hasSize(4);
        Assertions.assertThat(tasks.stream().map(Task::getTitle)).contains("title_1", "title_3", "title_5", "title_9");
    }

    @Test
    void shouldFindAllTasksRegardlessOfUserAndStatusWithoutUserJoin() {
        Pageable pageRequest = PageRequest.of(1, 10, Sort.by("id"));
        List<Task> tasks = taskRepository.findByOptionalUserAndStatusTask(null, null, pageRequest);
        Assertions.assertThat(tasks).hasSize(2);
    }
}
