package com.example.enigma.sample;

import com.example.enigma.model.TaskStatus;
import com.example.enigma.model.entity.Task;
import com.example.enigma.model.entity.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskUserSampleData {

    public static List<User> createUsers() {
        return new ArrayList<>(Arrays.asList(
                new User(1L, "alex", "alex", "alex@example.com"),
                new User(2L, "john", "john", "john@example.com"),
                new User(3L, "miriam", "miriam", "miriam@example.com"),
                new User(4L, "miriam", "miriam", "miriamAnother@example.com"),
                new User(5L, "miriam", "alex", "alexMiriam@example.com")));
    }

    public static List<Task> createTasks() {
        List<User> users = TaskUserSampleData.createUsers();
        List<Task> tasks = new ArrayList<>();
        Task task1 = new Task(1L, "Task 1", "Description for task 1", TaskStatus.DONE, LocalDate.now().plusDays(1));
        Task task2 = new Task(2L, "Task 2", "Description for task 2", TaskStatus.DONE, LocalDate.now().plusDays(2));
        Task task3 = new Task(3L, "Task 3", "Description for task 3", TaskStatus.DONE, LocalDate.now().plusDays(3));

        Task task4 = new Task(4L, "Task 4", "Description for task 4", TaskStatus.TO_DO, LocalDate.now().plusDays(1));
        Task task5 = new Task(5L, "Task 5", "Description for task 5", TaskStatus.TO_DO, LocalDate.now().plusDays(2));
        Task task6 = new Task(6L, "Task 6", "Description for task 6", TaskStatus.TO_DO, LocalDate.now().plusDays(3));

        Task task7 = new Task(7L, "Task 7", "Description for task 7", TaskStatus.IN_PROGRESS, LocalDate.now().plusDays(1));
        Task task8 = new Task(8L, "Task 8", "Description for task 8", TaskStatus.IN_PROGRESS, LocalDate.now().plusDays(2));
        Task task9 = new Task(9L, "Task 9", "Description for task 9", TaskStatus.IN_PROGRESS, LocalDate.now().plusDays(3));
        Task unassignedTask = new Task(10L, "Unassigned Task", "This task has no assigned users", TaskStatus.TO_DO, LocalDate.now().plusDays(1));

        task1.getUsers().add(users.get(0));

        task2.getUsers().add(users.get(0));
        task2.getUsers().add(users.get(1));

        task3.getUsers().add(users.get(0));
        task3.getUsers().add(users.get(1));
        task3.getUsers().add(users.get(2));

        task4.getUsers().add(users.get(1));
        task4.getUsers().add(users.get(0));

        task5.getUsers().add(users.get(1));
        task5.getUsers().add(users.get(2));

        task6.getUsers().add(users.get(0));
        task6.getUsers().add(users.get(1));
        task6.getUsers().add(users.get(2));

        task7.getUsers().add(users.get(2));

        task8.getUsers().add(users.get(1));
        task8.getUsers().add(users.get(0));

        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task4);
        tasks.add(task5);
        tasks.add(task6);
        tasks.add(task7);
        tasks.add(task8);
        tasks.add(task9);
        tasks.add(unassignedTask); // Dodajemy zadanie bez przypisania użytkowników
        return tasks;
    }
}
