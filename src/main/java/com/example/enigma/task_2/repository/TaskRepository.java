package com.example.enigma.task_2.repository;

import com.example.enigma.task_2.model.TaskStatus;
import com.example.enigma.task_2.model.entity.Task;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {

    @Query("select distinct t from Task t " +
            "left join fetch t.users u " +
            "where (:user_id is null or u.id = :user_id) " +
            "and (:status is null or t.taskStatus = :status)")
    List<Task> findByOptionalUserAndStatusTaskWithUser(@Param("user_id") Long userId, @Param("status") TaskStatus status, Pageable pageable);

    @Query("select distinct t from Task t " +
            "left join users u " +
            "where (:user_id is null or u.id = :user_id) " +
            "and (:status is null or t.taskStatus = :status)")
    List<Task> findByOptionalUserAndStatusTask(@Param("user_id") Long userId, @Param("status") TaskStatus status, Pageable pageable);

    Optional<Task> findByTitle(String title);

    @Query("select distinct t from Task t " +
            "left join t.users u " +
            "where u is null")
    List<Task> findUnsignedTasks(Pageable pageable);
}
