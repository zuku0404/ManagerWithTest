package com.example.enigma.controller;

import com.example.enigma.exception.ErrorMessage;
import com.example.enigma.exception.user.UserNotFoundException;
import com.example.enigma.model.entity.Task;
import com.example.enigma.model.entity.User;
import com.example.enigma.model.entity.user_dto.UserDto;
import com.example.enigma.model.entity.user_dto.UserWithTaskIdsAndWithoutIdDto;
import com.example.enigma.model.entity.user_dto.UserWithoutIdAndTasksDto;
import com.example.enigma.model.entity.user_dto.UserWithoutTaskDto;
import com.example.enigma.model.entity.user_dto.mapper.UserDtoMapper;
import com.example.enigma.sample.TaskUserSampleData;
import com.example.enigma.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private List<Task> tasks;
    private List<User> users;

    @BeforeEach
    public void setup() {
        users = TaskUserSampleData.createUsers();
        tasks = TaskUserSampleData.createTasks();
    }

    @Nested
    @DisplayName("Tests for getDetailedUsers API")
    class GetDetailedUsers {
        @Test
        void getUserById_ValidId_ShouldReturnUser() throws Exception {
            String name = "xxxx";
            String lastName = " xxxx";
            int page = 0;

            List<UserDto> expectedResult = UserDtoMapper.mapToUserDtos(users);
            given(userService.findAllDetailed(name, lastName, page)).willReturn(expectedResult);
            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/users/detailed")
                                    .param("name", name)
                                    .param("last_name", lastName)
                                    .param("page", Integer.toString(page))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            String expectedJson = objectMapper.writeValueAsString(expectedResult);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }
    }

    @Nested
    @DisplayName("Tests for getBasicUsers API")
    class GetBasicUsers {
        @Test
        void getBasicUsers_ShouldReturnExpectedUserList_WhenValidParametersAreGiven() throws Exception {
            String name = "xxxx";
            String lastName = " xxxx";

            List<UserWithoutTaskDto> expectedResult = UserDtoMapper.mapToUserWithoutTaskDtos(users);
            given(userService.findAllBasic(name, lastName)).willReturn(expectedResult);
            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/users/basic")
                                    .param("name", name)
                                    .param("last_name", lastName)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            String expectedJson = objectMapper.writeValueAsString(expectedResult);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }
    }

    @Nested
    @DisplayName("Tests for getUserById API")
    class GetUserById {
        @Test
        void getUserById_ValidId_ShouldReturnUser() throws Exception {
            User user = users.getFirst();
            Long userId = user.getId();
            UserDto expectedResult = UserDtoMapper.mapToUserDto(user);
            given(userService.findUserById(userId)).willReturn(expectedResult);
            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/users/{id}", userId)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            String expectedJson = objectMapper.writeValueAsString(expectedResult);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }
    }

    @Nested
    @DisplayName("Tests for getUserByEmail API")
    class GetUserByEmail {
        @Test
        void getUserByEmail_ValidId_ShouldReturnUser() throws Exception {
            User user = users.getFirst();
            String email = user.getEmail();
            UserDto expectedResult = UserDtoMapper.mapToUserDto(user);
            given(userService.findUserByEmail(email)).willReturn(expectedResult);
            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/users/emails/{email}", email)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            String expectedJson = objectMapper.writeValueAsString(expectedResult);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }
    }

    @Nested
    @DisplayName("Tests for createUser API")
    class CreateUser {
        @Test
        void createUser_ShouldReturnCreatedUser() throws Exception {
            Long id = users.stream()
                    .map(User::getId)
                    .max(Long::compareTo)
                    .orElseThrow(() -> new UserNotFoundException("id not found"));
            User user = new User(id + 1, "newName", "newLastName", "newEmail@example.com");

            UserWithoutIdAndTasksDto userWithoutIdAndTasksDto = UserDtoMapper.mapToUserWithoutIdAndTasksDto(user);
            UserWithoutTaskDto expectedResult = UserDtoMapper.mapToUserWithoutTaskDto(user);

            given(userService.create(userWithoutIdAndTasksDto)).willReturn(expectedResult);
            MockHttpServletResponse response = mockMvc.perform(
                            post("/api/users")
                                    .content(objectMapper.writeValueAsString(userWithoutIdAndTasksDto))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            String expectedJson = objectMapper.writeValueAsString(expectedResult);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }
    }

    @Nested
    @DisplayName("Tests for updateUser API")
    class UpdateUser {
        @Test
        void updateUserWithTask_ShouldReturnUpdatedUser() throws Exception {
            Long updatedUserId = 1L;
            List<Long> updatedTaskIds = List.of(1L, 2L, 3L);
            Set<Task> taskList = tasks.stream()
                    .filter(task -> updatedTaskIds.contains(task.getId()))
                    .collect(Collectors.toSet());
            User user = users.stream()
                    .filter(user1 -> user1.getId().equals(updatedUserId))
                    .findFirst()
                    .orElseThrow(() -> new UserNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, updatedUserId)));
            user.setName("updateName");
            user.setLastName("updateLastName");
            user.setEmail("update@example.com");
            user.setTasks(taskList);

            UserWithTaskIdsAndWithoutIdDto userWithTaskIdsAndWithoutIdDto = UserDtoMapper.mapToUserWithTaskIdsAndWithoutIdDto(user);
            UserDto expectedResult = UserDtoMapper.mapToUserDto(user);

            given(userService.update(updatedUserId, userWithTaskIdsAndWithoutIdDto)).willReturn(expectedResult);
            MockHttpServletResponse response = mockMvc.perform(
                            put("/api/users/{id}", updatedUserId)
                                    .content(objectMapper.writeValueAsString(userWithTaskIdsAndWithoutIdDto))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            String expectedJson = objectMapper.writeValueAsString(expectedResult);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }

        @Test
        void updateUserWithoutTasks_ShouldReturnUpdatedUser() throws Exception {
            Long updatedUserId = 1L;
            User user = users.stream()
                    .filter(user1 -> user1.getId().equals(updatedUserId))
                    .findFirst()
                    .orElseThrow(() -> new UserNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, updatedUserId)));
            user.setName("updateName");
            user.setLastName("updateLastName");
            user.setEmail("update@example.com");
            user.setTasks(Set.of());

            UserWithTaskIdsAndWithoutIdDto userWithTaskIdsAndWithoutIdDto = UserDtoMapper.mapToUserWithTaskIdsAndWithoutIdDto(user);
            UserDto expectedResult = UserDtoMapper.mapToUserDto(user);

            given(userService.update(updatedUserId, userWithTaskIdsAndWithoutIdDto)).willReturn(expectedResult);
            MockHttpServletResponse response = mockMvc.perform(
                            put("/api/users/{id}", updatedUserId)
                                    .content(objectMapper.writeValueAsString(userWithTaskIdsAndWithoutIdDto))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            String expectedJson = objectMapper.writeValueAsString(expectedResult);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }

        @Test
        void updateUserWithoutTasks_UserNotFound_ShouldReturnNotFoundStatus() throws Exception {
            Long updatedTaskId = 123L;
            Long searchedUser = 1L;
            User user = users.stream()
                    .filter(user1 -> user1.getId().equals(searchedUser))
                    .findFirst()
                    .orElseThrow(() -> new UserNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, searchedUser)));
            user.setName("updateName");
            user.setLastName("updateLastName");
            user.setEmail("update@example.com");
            user.setTasks(Set.of());

            UserWithTaskIdsAndWithoutIdDto userWithTaskIdsAndWithoutIdDto = UserDtoMapper.mapToUserWithTaskIdsAndWithoutIdDto(user);

            given(userService.update(updatedTaskId, userWithTaskIdsAndWithoutIdDto)).willThrow(new UserNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, updatedTaskId)));
            MockHttpServletResponse response = mockMvc.perform(
                            put("/api/users/{id}", updatedTaskId)
                                    .content(objectMapper.writeValueAsString(userWithTaskIdsAndWithoutIdDto))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
            String expectedErrorMessage = String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, updatedTaskId);
            assertThat(response.getContentAsString()).contains(expectedErrorMessage);
        }
    }

    @Nested
    @DisplayName("Tests for removeUser API")
    class RemoveUser {
        @Test
        void deleteUser_ShouldReturnOkStatus() throws Exception {
            Long userToDeleteId = 1L;
            willDoNothing().given(userService).delete(userToDeleteId);
            MockHttpServletResponse response = mockMvc.perform(
                            delete("/api/users/{id}", userToDeleteId)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        }
    }
}