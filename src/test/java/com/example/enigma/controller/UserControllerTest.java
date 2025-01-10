package com.example.enigma.controller;

import com.example.enigma.configuration.JwtService;
import com.example.enigma.exception.user.UserNotFoundException;
import com.example.enigma.model.entity.Task;
import com.example.enigma.model.entity.User;
import com.example.enigma.model.user_dto.UserDto;
import com.example.enigma.model.user_dto.UserPasswordUpdateDto;
import com.example.enigma.model.user_dto.UserWithTaskIdsAndWithoutIdDto;
import com.example.enigma.model.user_dto.UserWithoutTaskDto;
import com.example.enigma.model.user_dto.mapper.AdminPasswordUpdateDto;
import com.example.enigma.model.user_dto.mapper.UserDtoMapper;
import com.example.enigma.sample.TaskUserSampleData;
import com.example.enigma.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    private List<Task> tasks;
    private List<User> users;

    @BeforeEach
    public void setup() {
        TaskUserSampleData.createDataSet();
        users = TaskUserSampleData.users;
        tasks = TaskUserSampleData.tasks;
    }

    @Nested
    @DisplayName("Tests for getDetailedUsers API")
    class GetDetailedUsers {
        @Test
        @WithMockUser(username = "user", authorities = {"GUEST"})
        void getUserById_ValidId_ShouldReturnUser() throws Exception {
            String name = "xxxx";
            String lastName = " xxxx";
            int page = 0;

            List<UserDto> expectedResult = UserDtoMapper.mapToUserDtos(users);
            given(userService.findAllDetailed(name, lastName, page)).willReturn(expectedResult);
            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/users/detailed")
                                    .param("firstName", name)
                                    .param("lastName", lastName)
                                    .param("page", Integer.toString(page))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            String expectedJson = objectMapper.writeValueAsString(expectedResult);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }

        @Test
        @WithMockUser(username = "user", authorities = {"GUEST"})
        void getBasicUsers_ShouldReturnFilteredUsers_WhenFiltersProvided() throws Exception {
            List<UserWithoutTaskDto> users = List.of(new UserWithoutTaskDto(1L, "John", "Doe", "john@example.com"));
            given(userService.findAllBasic("John", "Doe")).willReturn(users);

            mockMvc.perform(get("/api/users/basic")
                            .param("firstName", "John")
                            .param("lastName", "Doe"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(users)));
        }
    }

    @Nested
    @DisplayName("Tests for getBasicUsers API")
    class GetBasicUsers {
        @Test
        @WithMockUser(username = "user", authorities = {"GUEST"})
        void getBasicUsers_ShouldReturnExpectedUserList_WhenValidParametersAreGiven() throws Exception {
            String name = "xxxx";
            String lastName = " xxxx";

            List<UserWithoutTaskDto> expectedResult = UserDtoMapper.mapToUserWithoutTaskDtos(users);
            given(userService.findAllBasic(name, lastName)).willReturn(expectedResult);
            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/users/basic")
                                    .param("firstName", name)
                                    .param("lastName", lastName)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            String expectedJson = objectMapper.writeValueAsString(expectedResult);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }

        @Test
        @WithMockUser(username = "user", authorities = {"GUEST"})
        void getBasicUsers_ShouldReturnFilteredUsers_WhenFiltersProvided() throws Exception {
            List<UserWithoutTaskDto> users = List.of(new UserWithoutTaskDto(1L, "John", "Doe", "john@example.com"));
            given(userService.findAllBasic("John", "Doe")).willReturn(users);

            mockMvc.perform(get("/api/users/basic")
                            .param("firstName", "John")
                            .param("lastName", "Doe"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(users)));
        }
    }

    @Nested
    @DisplayName("Tests for getUserById API")
    class GetUserById {
        @Test
        @WithMockUser(username = "user", authorities = {"GUEST"})
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

        @Test
        @WithMockUser(username = "user", authorities = {"GUEST"})
        void getUser_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
            given(userService.findUserById(1L)).willThrow(new UserNotFoundException("User not found"));

            mockMvc.perform(get("/api/users/1"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Tests for getUserByEmail API")
    class GetUserByEmail {
        @Test
        @WithMockUser(username = "user", authorities = {"GUEST"})
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

        @Test
        @WithMockUser(username = "user", authorities = {"GUEST"})
        void getUserByEmail_ShouldReturnNotFound_WhenEmailDoesNotExist() throws Exception {
            given(userService.findUserByEmail("john@example.com")).willThrow(new UserNotFoundException("User not found"));

            mockMvc.perform(get("/api/users/emails/john@example.com").with(csrf()))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Tests for EditUserData API")
    class EditUserData {
        @Test
        @WithMockUser(roles = "ADMIN")
        void editUserData_ShouldUpdateUser_WhenAuthorized() throws Exception {
            User user = users.getFirst();
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserWithTaskIdsAndWithoutIdDto request = new UserWithTaskIdsAndWithoutIdDto("John", "Smith", "john@example.com", List.of());
            UserDto updatedUser = new UserDto(user.getId(), request.firstName(), request.lastName(), request.email(), user.getRole(), List.of());

            given(userService.updateUserData(user, request)).willReturn(updatedUser);

            MockHttpServletResponse response = mockMvc.perform(patch("/api/users")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            System.out.println("Response Status: " + response.getStatus());
            System.out.println("Response Headers: " + response.getHeaderNames());
            System.out.println("Response Content: " + response.getContentAsString());
            JSONAssert.assertEquals(objectMapper.writeValueAsString(updatedUser), response.getContentAsString(), JSONCompareMode.LENIENT);
        }

        @Test
        @WithMockUser(username = "user", authorities = {"GUEST"})
        void editUserData_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
            UserWithTaskIdsAndWithoutIdDto request = new UserWithTaskIdsAndWithoutIdDto("John", "Smith", "john@example.com", List.of());

            mockMvc.perform(patch("/api/users")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Tests for editUserPasswordByUser API")
    class EditUserPasswordByUser {
        @Test
        @WithMockUser(roles = "USER")
        void editUserPasswordByUser_ShouldChangePassword_WhenAuthorized() throws Exception {
            UserPasswordUpdateDto request = new UserPasswordUpdateDto("oldPass123", "newPass@123");
            willDoNothing().given(userService).editUserPasswordByUser(users.getFirst(), request);

            mockMvc.perform(post("/api/users/change-password")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Password changed successfully. New password: newPass@123"));
        }

        @Test
        @WithMockUser(username = "user", authorities = {"GUEST"})
        void editUserPasswordByUser_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
            UserPasswordUpdateDto request = new UserPasswordUpdateDto("oldPass123", "newPass@123");
            willDoNothing().given(userService).editUserPasswordByUser(users.getFirst(), request);

            mockMvc.perform(post("/api/users/change-password")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Tests for editUserPasswordByAdmin API")
    class EditUserPasswordByAdmin {
        @Test
        @WithMockUser(roles = "ADMIN")
        void editUserPasswordByAdmin_ShouldChangePassword_WhenAuthorized() throws Exception {
            AdminPasswordUpdateDto request = new AdminPasswordUpdateDto("dasda@wp.pl", "newAdminPass1@23");
            willDoNothing().given(userService).editUserPasswordByAdmin(request);
            mockMvc.perform(post("/api/users/change-password/admin")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Password changed successfully. New password: newAdminPass1@23"));
        }

        @Test
        @WithMockUser(roles = "USER")
        void editUserPasswordByAdmin_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
            AdminPasswordUpdateDto request = new AdminPasswordUpdateDto("dasda@wp.pl", "newAdminPass1@23");
            willDoNothing().given(userService).editUserPasswordByAdmin(request);
            mockMvc.perform(post("/api/users/change-password/admin")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Tests for removeUser API")
    class RemoveUser {
        @Test
        @WithMockUser(roles = "ADMIN")
        void deleteUser_ShouldReturnOkStatus() throws Exception {
            Long userToDeleteId = 1L;
            willDoNothing().given(userService).delete(userToDeleteId);
            MockHttpServletResponse response = mockMvc.perform(
                            delete("/api/users/{id}", userToDeleteId)
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        @WithMockUser(username = "user", authorities = {"GUEST"})
        void removeUser_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
            mockMvc.perform(delete("/api/users/1")
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }
}