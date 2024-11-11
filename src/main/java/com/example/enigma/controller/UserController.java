package com.example.enigma.controller;

import com.example.enigma.model.entity.user_dto.UserDto;
import com.example.enigma.model.entity.user_dto.UserWithTaskIdsAndWithoutIdDto;
import com.example.enigma.model.entity.user_dto.UserWithoutIdAndTasksDto;
import com.example.enigma.model.entity.user_dto.UserWithoutTaskDto;
import com.example.enigma.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/detailed")
    public List<UserDto> getDetailedUsers(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "last_name", required = false) String lastName,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page) {
        return userService.findAllDetailed(name, lastName, page);
    }

    @GetMapping("/basic")
    public List<UserWithoutTaskDto> getBasicUsers(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "last_name", required = false) String lastName) {
        return userService.findAllBasic(name, lastName);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable("id") Long id) {
        return userService.findUserById(id);
    }

    @GetMapping("/emails/{email}")
    public UserDto getUserByEmail(@PathVariable("email") String email) {
        return userService.findUserByEmail(email);
    }

    @PostMapping
    public UserWithoutTaskDto createUser(@RequestBody UserWithoutIdAndTasksDto user) {
        return userService.create(user);
    }

    @PutMapping("/{id}")
    public UserDto editUser(@PathVariable("id") Long id, @RequestBody UserWithTaskIdsAndWithoutIdDto user) {
        return userService.update(id, user);
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable("id") Long id) {
        userService.delete(id);
    }
}
