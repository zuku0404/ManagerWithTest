package com.example.enigma.controller;

import com.example.enigma.model.entity.User;
import com.example.enigma.model.user_dto.UserDto;
import com.example.enigma.model.user_dto.UserPasswordUpdateDto;
import com.example.enigma.model.user_dto.UserWithTaskIdsAndWithoutIdDto;
import com.example.enigma.model.user_dto.UserWithoutTaskDto;
import com.example.enigma.model.user_dto.mapper.AdminPasswordUpdateDto;
import com.example.enigma.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/detailed")
    public List<UserDto> getDetailedUsers(
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "lastName", required = false) String lastName,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page) {
        return userService.findAllDetailed(firstName, lastName, page);
    }

    @GetMapping("/basic")
    public List<UserWithoutTaskDto> getBasicUsers(
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "lastName", required = false) String lastName) {
        return userService.findAllBasic(firstName, lastName);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable("id") Long id) {
        return userService.findUserById(id);
    }

    @GetMapping("/current-user")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public UserDto getCurrentUser(@AuthenticationPrincipal User user) {
        return userService.findCurrentUser(user);
    }

    @GetMapping("/emails/{email}")
    public UserDto getUserByEmail(@PathVariable("email") String email) {
        return userService.findUserByEmail(email);
    }

    @PatchMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public UserDto editUserData(@AuthenticationPrincipal User user,
                                @RequestBody UserWithTaskIdsAndWithoutIdDto request) {
        return userService.updateUserData(user, request);
    }

    @PatchMapping("/change-password")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<String> editUserPasswordByUser(@AuthenticationPrincipal User user,
                                                         @RequestBody @Valid UserPasswordUpdateDto request) {
        userService.editUserPasswordByUser(user, request);
        return ResponseEntity.ok("Password changed successfully. New password: " + request.newPassword());
    }

    @PatchMapping("/change-password/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> editUserPasswordByAdmin(
            @RequestBody @Valid AdminPasswordUpdateDto request) {
        userService.editUserPasswordByAdmin(request);
        return ResponseEntity.ok("Password changed successfully. New password: " + request.newPassword());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> removeUser(@PathVariable("id") Long id) {
        userService.delete(id);
        return ResponseEntity.ok("User removed successfully");
    }
}
