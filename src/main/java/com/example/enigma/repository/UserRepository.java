package com.example.enigma.repository;

import com.example.enigma.model.entity.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select distinct u from User u " +
            "left join fetch u.tasks " +
            "where (:firstName is null or u.firstName = :firstName) " +
            "and (:lastName is null or u.lastName = :lastName)")
    List<User> findByOptionalNameAndLastNameWithTasks(@Param("firstName") String firstName, @Param("lastName") String lastName, PageRequest pageRequest);

    @Query("select distinct u from User u " +
            "where (:firstName is null or u.firstName = :firstName) " +
            "and (:lastName is null or u.lastName = :lastName)")
    List<User> findByOptionalNameAndLastName(@Param("firstName") String firstName, @Param("lastName") String lastName);

    Optional<User> findByEmail(String email);

    @Query("select u from User u " +
            "left join fetch u.tasks " +
            "where u.id = :id")
    User findUserWithTasksById(Long id);
}
