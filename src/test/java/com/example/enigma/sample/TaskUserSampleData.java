package com.example.enigma.sample;

import com.example.enigma.model.Role;
import com.example.enigma.model.TaskStatus;
import com.example.enigma.model.entity.Task;
import com.example.enigma.model.entity.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TaskUserSampleData {

    public static List<User> users = new ArrayList<>();
    public static List<Task> tasks = new ArrayList<>();

    public static void createDataSet() {
        users = createUsers();
        tasks = createTasks();

        assignTasksToUsers(Map.of(
                0, List.of(0, 1, 2, 3, 5, 7),  // alex
                1, List.of(1, 2, 3, 4, 5, 7),  // john
                2, List.of(2, 4, 5, 6)         // miriam
        ));
    }

    private static void assignTasksToUsers(Map<Integer, List<Integer>> assignments) {
        assignments.forEach((userIndex, taskIndices) -> {
            User user = users.get(userIndex);
            taskIndices.forEach(taskIndex -> user.addTask(tasks.get(taskIndex)));
        });
    }

    private static List<User> createUsers() {
        return new ArrayList<>(Arrays.asList(
                User.builder()
                        .id(1L)
                        .firstName("alex")
                        .lastName("alex")
                        .email("alex@example.com")
                        .password("alex")
                        .role(Role.ROLE_ADMIN)
                        .build(),

                User.builder()
                        .id(2L)
                        .firstName("john")
                        .lastName("john")
                        .email("john@example.com")
                        .password("john")
                        .role(Role.ROLE_USER)
                        .build(),

                User.builder()
                        .id(3L)
                        .firstName("miriam")
                        .lastName("miriam")
                        .email("miriam@example.com")
                        .password("miriam")
                        .role(Role.ROLE_USER)
                        .build(),

                User.builder()
                        .id(4L)
                        .firstName("miriam")
                        .lastName("miriam")
                        .email("miriamAnother@example.com")
                        .password("miriam2")
                        .role(Role.ROLE_USER)
                        .build(),

                User.builder()
                        .id(5L)
                        .firstName("miriam")
                        .lastName("alex")
                        .email("alexMiriam@example.com")
                        .password("miriamalex")
                        .role(Role.ROLE_USER)
                        .build()
        ));
    }

    private static List<Task> createTasks() {
        return new ArrayList<>(Arrays.asList(
                Task.builder()
                        .id(1L)
                        .title("Task 1")
                        .description("Description for task 1")
                        .taskStatus(TaskStatus.DONE)
                        .deadline(LocalDate.now().plusDays(1))
                        .build(),

                Task.builder()
                        .id(2L)
                        .title("Task 2")
                        .description("Description for task 2")
                        .taskStatus(TaskStatus.DONE)
                        .deadline(LocalDate.now().plusDays(2))
                        .build(),

                Task.builder()
                        .id(3L)
                        .title("Task 3")
                        .description("Description for task 3")
                        .taskStatus(TaskStatus.DONE)
                        .deadline(LocalDate.now().plusDays(3))
                        .build(),

                Task.builder()
                        .id(4L)
                        .title("Task 4")
                        .description("Description for task 4")
                        .taskStatus(TaskStatus.TO_DO)
                        .deadline(LocalDate.now().plusDays(1))
                        .build(),

                Task.builder()
                        .id(5L)
                        .title("Task 5")
                        .description("Description for task 5")
                        .taskStatus(TaskStatus.TO_DO)
                        .deadline(LocalDate.now().plusDays(2))
                        .build(),

                Task.builder()
                        .id(6L)
                        .title("Task 6")
                        .description("Description for task 6")
                        .taskStatus(TaskStatus.TO_DO)
                        .deadline(LocalDate.now().plusDays(3))
                        .build(),

                Task.builder()
                        .id(7L)
                        .title("Task 7")
                        .description("Description for task 7")
                        .taskStatus(TaskStatus.IN_PROGRESS)
                        .deadline(LocalDate.now().plusDays(1))
                        .build(),

                Task.builder()
                        .id(8L)
                        .title("Task 8")
                        .description("Description for task 8")
                        .taskStatus(TaskStatus.IN_PROGRESS)
                        .deadline(LocalDate.now().plusDays(2))
                        .build(),

                Task.builder()
                        .id(9L)
                        .title("Task 9")
                        .description("Description for task 9")
                        .taskStatus(TaskStatus.IN_PROGRESS)
                        .deadline(LocalDate.now().plusDays(3))
                        .build(),

                Task.builder()
                        .id(10L)
                        .title("Unassigned Task")
                        .description("This task has no assigned users")
                        .taskStatus(TaskStatus.TO_DO)
                        .deadline(LocalDate.now().plusDays(1))
                        .build()
        ));
    }
}