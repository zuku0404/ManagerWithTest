package com.example.enigma.task_2.model.entity;

import com.example.enigma.task_2.model.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "users")
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "task_status")
    private TaskStatus taskStatus;
    private LocalDate deadline;
    @ManyToMany(mappedBy = "tasks")
    private Set<User> users = new HashSet<>();

    public Task(String title, String description, TaskStatus taskStatus, LocalDate deadline) {
        this.title = title;
        this.description = description;
        this.taskStatus = taskStatus;
        this.deadline = deadline;
    }

    public Task(Long id, String title, String description, TaskStatus taskStatus, LocalDate deadline) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.taskStatus = taskStatus;
        this.deadline = deadline;
    }

    public Task(String title, String description, TaskStatus taskStatus, LocalDate deadline, Set<User> users) {
        this.title = title;
        this.description = description;
        this.taskStatus = taskStatus;
        this.deadline = deadline;
        this.users = users;
    }
}
