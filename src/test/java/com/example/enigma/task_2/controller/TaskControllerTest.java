package com.example.enigma.task_2.controller;

import com.example.enigma.task_2.model.entity.Task;
import com.example.enigma.task_2.model.entity.User;
import com.example.enigma.task_2.model.entity.task_dto.TaskDto;
import com.example.enigma.task_2.model.entity.task_dto.TaskWithoutIdDto;
import com.example.enigma.task_2.model.entity.task_dto.TaskWithoutUserDto;
import com.example.enigma.task_2.model.entity.task_dto.mapper.TaskDtoMapper;
import com.example.enigma.task_2.model.entity.user_dto.UserWithoutTaskDto;
import com.example.enigma.task_2.model.entity.user_dto.mapper.UserDtoMapper;
import com.example.enigma.task_2.sample.TaskUserSampleData;
import com.example.enigma.task_2.exception.ErrorMessage;
import com.example.enigma.task_2.exception.task.TaskNotFoundException;
import com.example.enigma.task_2.exception.user.UserNotFoundException;
import com.example.enigma.task_2.model.Action;
import com.example.enigma.task_2.model.SortDirection;
import com.example.enigma.task_2.model.TaskStatus;
import com.example.enigma.task_2.model.entity.user_dto.UserTaskActionRequest;
import com.example.enigma.task_2.service.TaskService;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private List<Task> tasks;
    private List<User> users;

    @BeforeEach
    public void setup() {
        users = TaskUserSampleData.createUsers();
        tasks = TaskUserSampleData.createTasks();
    }

    @Nested
    @DisplayName("Tests for getDetailedTasks API")
    class GetDetailedTasks {
        private final int page = 0;
        private final boolean sort = false;
        private final SortDirection sortDirection = SortDirection.ASC;

        @Test
        void getAllDetailedTasks_ShouldReturnAllTasks() throws Exception {
            List<TaskDto> expectedResult = TaskDtoMapper.mapToTaskDtos(tasks);
            given(taskService.findAllDetailed(null, null, page, sort, sortDirection)).willReturn(expectedResult);
            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/tasks/detailed")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();
            String expectedJson = objectMapper.writeValueAsString(expectedResult);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }

        @Test
        void getDetailedTasksByStatus_ShouldReturnFilteredTasksByStatus() throws Exception {
            TaskStatus searchedTaskStatus = TaskStatus.TO_DO;
            List<TaskDto> expectedResult = tasks.stream()
                    .filter(task -> task.getTaskStatus() == searchedTaskStatus)
                    .map(TaskDtoMapper::mapToTaskDto)
                    .toList();
            given(taskService.findAllDetailed(null, searchedTaskStatus, page, sort, sortDirection)).willReturn(expectedResult);
            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/tasks/detailed")
                                    .param("status", searchedTaskStatus.name())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();
            String expectedJson = objectMapper.writeValueAsString(expectedResult);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }

        @Test
        void getDetailedTasksByUserIdAndStatus_ShouldReturnFilteredTasksByUserIdAndStatus() throws Exception {
            TaskStatus searchedTaskStatus = TaskStatus.IN_PROGRESS;
            Long searchedUserId = 1L;
            List<TaskDto> expectedResult = tasks.stream()
                    .filter(task -> task.getTaskStatus() == searchedTaskStatus)
                    .filter(task -> task.getUsers().stream()
                            .anyMatch(user -> user.getId().equals(searchedUserId)))
                    .map(TaskDtoMapper::mapToTaskDto)
                    .toList();
            given(taskService.findAllDetailed(searchedUserId, searchedTaskStatus, page, sort, sortDirection)).willReturn(expectedResult);
            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/tasks/detailed")
                                    .param("user_id", searchedUserId.toString())
                                    .param("status", searchedTaskStatus.name())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();
            String expectedJson = objectMapper.writeValueAsString(expectedResult);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }

        @Test
        void getDetailedTasksByUserId_ShouldReturnFilteredTasksByUserId() throws Exception {
            Long searchedUserId = 1L;
            List<TaskDto> expectedResult = tasks.stream()
                    .filter(task -> task.getUsers().stream()
                            .anyMatch(user -> user.getId().equals(searchedUserId)))
                    .map(TaskDtoMapper::mapToTaskDto)
                    .toList();
            given(taskService.findAllDetailed(searchedUserId, null, page, sort, sortDirection)).willReturn(expectedResult);
            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/tasks/detailed")
                                    .param("user_id", searchedUserId.toString())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();
            String expectedJson = objectMapper.writeValueAsString(expectedResult);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }

        @Test
        void getDetailedTasksByInvalidUserId_ShouldReturnNotFound() throws Exception {
            Long invalidUserId = 999L;
            given(taskService.findAllDetailed(invalidUserId, null, page, sort, sortDirection)).willThrow(new TaskNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, invalidUserId)));
            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/tasks/detailed")
                                    .param("user_id", invalidUserId.toString())
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
            String expectedErrorMessage = String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, invalidUserId);
            assertThat(response.getContentAsString()).contains(expectedErrorMessage);
        }

        @Test
        void getDetailedTasksNoMatches_ShouldReturnNotFound() throws Exception {
            Long invalidUserId = 999L;
            TaskStatus searchedTaskStatus = TaskStatus.IN_PROGRESS;
            given(taskService.findAllDetailed(invalidUserId, searchedTaskStatus, page, sort, sortDirection)).willThrow(new TaskNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, invalidUserId)));
            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/tasks/detailed")
                                    .param("user_id", invalidUserId.toString())
                                    .param("status", searchedTaskStatus.name())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
            String expectedErrorMessage = String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, invalidUserId);
            assertThat(response.getContentAsString()).contains(expectedErrorMessage);
        }
    }

    @Nested
    @DisplayName("Tests for getBasicTasks API")
    class GetBasicTasks {
        private final boolean sort = false;
        private final SortDirection sortDirection = SortDirection.ASC;

        @Test
        void getAllBasicTasks_ShouldReturnAllTasks() throws Exception {
            List<TaskWithoutUserDto> expectedResult = TaskDtoMapper.mapToTaskWithoutUserDtos(tasks);
            given(taskService.findAllBasic(null, null, sort, sortDirection)).willReturn(expectedResult);
            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/tasks/basic")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();
            String expectedJson = objectMapper.writeValueAsString(expectedResult);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }

        @Test
        void getBasicTasksByStatus_ShouldReturnFilteredTasksByStatus() throws Exception {
            TaskStatus searchedTaskStatus = TaskStatus.TO_DO;
            List<TaskWithoutUserDto> expectedResult = tasks.stream()
                    .filter(task -> task.getTaskStatus() == searchedTaskStatus)
                    .map(TaskDtoMapper::mapToTaskWithoutUserDto)
                    .toList();
            given(taskService.findAllBasic(null, searchedTaskStatus, sort, sortDirection)).willReturn(expectedResult);
            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/tasks/basic")
                                    .param("status", searchedTaskStatus.name())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();
            String expectedJson = objectMapper.writeValueAsString(expectedResult);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }

        @Test
        void getBasicTasksByUserIdAndStatus_ShouldReturnFilteredTasksByUserIdAndStatus() throws Exception {
            TaskStatus searchedTaskStatus = TaskStatus.IN_PROGRESS;
            Long searchedUserId = 1L;
            List<TaskWithoutUserDto> expectedResult = tasks.stream()
                    .filter(task -> task.getTaskStatus() == searchedTaskStatus)
                    .filter(task -> task.getUsers().stream()
                            .anyMatch(user -> user.getId().equals(searchedUserId)))
                    .map(TaskDtoMapper::mapToTaskWithoutUserDto)
                    .toList();
            given(taskService.findAllBasic(searchedUserId, searchedTaskStatus, sort, sortDirection)).willReturn(expectedResult);
            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/tasks/basic")
                                    .param("user_id", searchedUserId.toString())
                                    .param("status", searchedTaskStatus.name())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();
            String expectedJson = objectMapper.writeValueAsString(expectedResult);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }

        @Test
        void getBasicTasksByUserId_ShouldReturnFilteredTasksByUserId() throws Exception {
            Long searchedUserId = 1L;
            List<TaskWithoutUserDto> expectedResult = tasks.stream()
                    .filter(task -> task.getUsers().stream()
                            .anyMatch(user -> user.getId().equals(searchedUserId)))
                    .map(TaskDtoMapper::mapToTaskWithoutUserDto)
                    .toList();
            given(taskService.findAllBasic(searchedUserId, null, sort, sortDirection)).willReturn(expectedResult);
            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/tasks/basic")
                                    .param("user_id", searchedUserId.toString())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();
            String expectedJson = objectMapper.writeValueAsString(expectedResult);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }

        @Test
        void getBasicTasksByInvalidUserId_ShouldReturnNotFound() throws Exception {
            Long invalidUserId = 999L;
            given(taskService.findAllBasic(invalidUserId, null, sort, sortDirection)).willThrow(new TaskNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, invalidUserId)));
            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/tasks/basic")
                                    .param("user_id", invalidUserId.toString())
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
            String expectedErrorMessage = String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, invalidUserId);
            assertThat(response.getContentAsString()).contains(expectedErrorMessage);
        }

        @Test
        void getBasicTasksNoMatches_ShouldReturnNotFound() throws Exception {
            Long invalidUserId = 999L;
            TaskStatus searchedTaskStatus = TaskStatus.IN_PROGRESS;
            given(taskService.findAllBasic(invalidUserId, searchedTaskStatus, sort, sortDirection)).willThrow(new TaskNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, invalidUserId)));
            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/tasks/basic")
                                    .param("user_id", invalidUserId.toString())
                                    .param("status", searchedTaskStatus.name())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
            String expectedErrorMessage = String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, invalidUserId);
            assertThat(response.getContentAsString()).contains(expectedErrorMessage);
        }
    }

    @Nested
    @DisplayName("Tests for getUnsignedTasks API")
    class GetUnsignedTasks {
        private final int page = 0;
        private final boolean sort = false;
        private final SortDirection sortDirection = SortDirection.ASC;

        @Test
        void getUnsignedTasks_ValidId_ShouldReturnTask() throws Exception {
            List<TaskWithoutUserDto> taskWithoutUserDtos = TaskDtoMapper.mapToTaskWithoutUserDtos(
                    tasks.stream()
                            .filter(t -> t.getUsers() == null || t.getUsers().isEmpty())
                            .toList());
            given(taskService.findUnsigned(page, sort, sortDirection)).willReturn(taskWithoutUserDtos);
            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/tasks/unsigned")
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            String expectedJson = objectMapper.writeValueAsString(taskWithoutUserDtos);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }
    }

    @Nested
    @DisplayName("Tests for getTaskById API")
    class GetTaskById {

        @Test
        void getTaskById_ValidId_ShouldReturnTask() throws Exception {
            Long taskId = 1L;
            TaskDto task = TaskDtoMapper.mapToTaskDto(
                    tasks.stream()
                            .filter(t -> t.getId().equals(taskId))
                            .findFirst()
                            .orElseThrow(() -> new TaskNotFoundException(String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, taskId))));
            given(taskService.findTaskById(taskId)).willReturn(task);
            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/tasks/{id}", taskId)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            String expectedJson = objectMapper.writeValueAsString(task);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }

        @Test
        void getTaskById_InvalidId_ShouldReturnNotFound() throws Exception {
            Long taskId = 123L;
            given(taskService.findTaskById(taskId)).willThrow(new TaskNotFoundException(String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, taskId)));
            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/tasks/{id}", taskId)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
            String expectedErrorMessage = String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, taskId);
            assertThat(response.getContentAsString()).contains(expectedErrorMessage);
        }
    }

    @Nested
    @DisplayName("Tests for getTaskByTitle API")
    class GetTaskByTitle {
        @Test
        void getTaskByTitle_ValidId_ShouldReturnTask() throws Exception {
            String searchedTitle = "Task 5";
            TaskDto task = TaskDtoMapper.mapToTaskDto(
                    tasks.stream()
                            .filter(t -> t.getTitle().equals(searchedTitle))
                            .findFirst()
                            .orElseThrow(() -> new TaskNotFoundException(String.format(ErrorMessage.TASK_NOT_FOUND_BY_TITLE, searchedTitle))));
            given(taskService.findTaskByTitle(searchedTitle)).willReturn(task);
            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/tasks/titles/{title}", searchedTitle)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            String expectedJson = objectMapper.writeValueAsString(task);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }

        @Test
        void getTaskByTitle_InvalidId_ShouldReturnNotFound() throws Exception {
            String searchedTitle = "not_exist";
            given(taskService.findTaskByTitle(searchedTitle)).willThrow(new TaskNotFoundException(String.format(ErrorMessage.TASK_NOT_FOUND_BY_TITLE, searchedTitle)));
            MockHttpServletResponse response = mockMvc.perform(
                            get("/api/tasks/titles/{title}", searchedTitle)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
            String expectedErrorMessage = String.format(ErrorMessage.TASK_NOT_FOUND_BY_TITLE, searchedTitle);
            assertThat(response.getContentAsString()).contains(expectedErrorMessage);
        }
    }

    @Nested
    @DisplayName("Tests for createTask API")
    class CreateTask {
        @Test
        void createTaskWithoutUser_ShouldReturnCreatedTask() throws Exception {
            TaskWithoutIdDto newTask = new TaskWithoutIdDto("New Task", "This is new task", TaskStatus.TO_DO,
                    LocalDate.now().plusDays(1), new ArrayList<>());
            Long id = tasks.stream()
                    .map(Task::getId)
                    .max(Long::compareTo)
                    .orElseThrow(() -> new TaskNotFoundException("id not found"));
            TaskDto expectedResult = new TaskDto(id + 1, "New Task", "This is new task", TaskStatus.TO_DO,
                    LocalDate.now().plusDays(1), new ArrayList<>());

            given(taskService.create(newTask)).willReturn(expectedResult);
            MockHttpServletResponse response = mockMvc.perform(
                            post("/api/tasks")
                                    .content(objectMapper.writeValueAsString(newTask))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            String expectedJson = objectMapper.writeValueAsString(expectedResult);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }

        @Test
        void createTaskWithUsers_ShouldReturnCreatedTask() throws Exception {
            TaskWithoutIdDto newTask = new TaskWithoutIdDto("New Task", "This is new task", TaskStatus.TO_DO,
                    LocalDate.now().plusDays(1), List.of(1L, 2L));

            UserWithoutTaskDto user1 = UserDtoMapper.mapToUserWithoutTaskDto(users.get(0));
            UserWithoutTaskDto user2 = UserDtoMapper.mapToUserWithoutTaskDto(users.get(1));

            TaskDto expectedResult = new TaskDto(11L, "New Task", "This is new task", TaskStatus.TO_DO,
                    LocalDate.now().plusDays(1), List.of(user1, user2));

            given(taskService.create(newTask)).willReturn(expectedResult);

            MockHttpServletResponse response = mockMvc.perform(
                            post("/api/tasks")
                                    .content(objectMapper.writeValueAsString(newTask))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            String expectedJson = objectMapper.writeValueAsString(expectedResult);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }
    }

    @Nested
    @DisplayName("Tests for updateTask API")
    class UpdateTask {
        @Test
        void updateTaskWithUsers_ShouldReturnUpdatedTask() throws Exception {
            Long updatedTaskId = 1L;
            Task task = tasks.stream()
                    .filter(task1 -> task1.getId().equals(updatedTaskId))
                    .findFirst()
                    .orElseThrow(() -> new TaskNotFoundException(String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, updatedTaskId)));
            task.setTitle("Updated Task");
            task.setDescription("This is updated task");
            task.setTaskStatus(TaskStatus.IN_PROGRESS);
            task.setDeadline(LocalDate.now().plusDays(10));
            task.setUsers(Set.of(users.get(0), users.get(1)));

            TaskWithoutIdDto taskWithoutIdDto = TaskDtoMapper.mapToTaskWithoutIdDto(task);
            TaskDto expectedResult = TaskDtoMapper.mapToTaskDto(task);

            given(taskService.update(updatedTaskId, taskWithoutIdDto)).willReturn(expectedResult);
            MockHttpServletResponse response = mockMvc.perform(
                            put("/api/tasks/{id}", updatedTaskId)
                                    .content(objectMapper.writeValueAsString(taskWithoutIdDto))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            String expectedJson = objectMapper.writeValueAsString(expectedResult);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }


        @Test
        void updateTaskWithoutUsers_ShouldReturnUpdatedTask() throws Exception {
            Long updatedTaskId = 1L;
            Task task = tasks.stream()
                    .filter(task1 -> task1.getId().equals(updatedTaskId))
                    .findFirst()
                    .orElseThrow(() -> new TaskNotFoundException(String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, updatedTaskId)));
            task.setTitle("Updated Task");
            task.setDescription("This is updated task");
            task.setTaskStatus(TaskStatus.IN_PROGRESS);
            task.setDeadline(LocalDate.now().plusDays(10));
            task.setUsers(Set.of());

            TaskWithoutIdDto taskWithoutIdDto = TaskDtoMapper.mapToTaskWithoutIdDto(task);
            TaskDto expectedResult = TaskDtoMapper.mapToTaskDto(task);

            given(taskService.update(updatedTaskId, taskWithoutIdDto)).willReturn(expectedResult);
            MockHttpServletResponse response = mockMvc.perform(
                            put("/api/tasks/{id}", updatedTaskId)
                                    .content(objectMapper.writeValueAsString(taskWithoutIdDto))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            String expectedJson = objectMapper.writeValueAsString(expectedResult);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }

        @Test
        void updateTaskWithoutUsers_TaskNotFound_ShouldReturnNotFoundStatus() throws Exception {
            Long updatedTaskId = 123L;
            Long taskToUpdateId = 1L;
            Task task = tasks.stream()
                    .filter(task1 -> task1.getId().equals(taskToUpdateId))
                    .findFirst()
                    .orElseThrow(() -> new TaskNotFoundException(String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, taskToUpdateId)));
            task.setTitle("Updated Task");
            task.setDescription("This is updated task");
            task.setTaskStatus(TaskStatus.IN_PROGRESS);
            task.setDeadline(LocalDate.now().plusDays(10));
            task.setUsers(Set.of());

            TaskWithoutIdDto taskWithoutIdDto = TaskDtoMapper.mapToTaskWithoutIdDto(task);
            given(taskService.update(updatedTaskId, taskWithoutIdDto)).willThrow(new TaskNotFoundException(String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, updatedTaskId)));
            MockHttpServletResponse response = mockMvc.perform(
                            put("/api/tasks/{id}", updatedTaskId)
                                    .content(objectMapper.writeValueAsString(taskWithoutIdDto))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
            String expectedErrorMessage = String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, updatedTaskId);
            assertThat(response.getContentAsString()).contains(expectedErrorMessage);
        }
    }

    @Nested
    @DisplayName("Tests for changeTaskStatus API")
    class ChangeTaskStatus {
        @Test
        void changeTaskStatus_ShouldReturnUpdatedTask_WhenTaskExists() throws Exception {
            Long updatedTaskId = 1L;
            TaskStatus newStatus = TaskStatus.IN_PROGRESS;
            Task task = tasks.stream()
                    .filter(task1 -> task1.getId().equals(updatedTaskId))
                    .findFirst()
                    .orElseThrow(() -> new TaskNotFoundException(String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, updatedTaskId)));
            task.setTaskStatus(newStatus);
            TaskDto expectedResult = TaskDtoMapper.mapToTaskDto(task);

            given(taskService.changeStatus(updatedTaskId, newStatus)).willReturn(expectedResult);
            MockHttpServletResponse response = mockMvc.perform(
                            patch("/api/tasks/{id}/status", updatedTaskId)
                                    .content(objectMapper.writeValueAsString(newStatus))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            String expectedJson = objectMapper.writeValueAsString(expectedResult);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }

        @Test
        void changeTaskStatus_ShouldReturnNotFound_WhenTaskDoesNotExist() throws Exception {
            Long updatedTaskId = 123L;
            TaskStatus newStatus = TaskStatus.IN_PROGRESS;

            given(taskService.changeStatus(updatedTaskId, newStatus)).willThrow(new TaskNotFoundException(String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, updatedTaskId)));
            MockHttpServletResponse response = mockMvc.perform(
                            patch("/api/tasks/{id}/status", updatedTaskId)
                                    .content(objectMapper.writeValueAsString(newStatus))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
            String expectedErrorMessage = String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, updatedTaskId);
            assertThat(response.getContentAsString()).contains(expectedErrorMessage);
        }
    }

    @Nested
    @DisplayName("Tests for modifyUserAssignmentToTask API")
    class ModifyUserAssignmentToTask {
        @Test
        void modifyUserAssignmentToTask_ToAttachUser_ShouldReturnUpdatedTask() throws Exception {
            Long updatedTaskId = 1L;
            Long attachedUserId = 2L;
            Action action = Action.ADD;
            UserTaskActionRequest userTaskActionRequest = new UserTaskActionRequest(attachedUserId, action);
            Task task = tasks.stream()
                    .filter(task1 -> task1.getId().equals(updatedTaskId))
                    .findFirst()
                    .orElseThrow(() -> new TaskNotFoundException(String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, updatedTaskId)));
            task.getUsers().add(users.stream()
                    .filter(user -> user.getId().equals(attachedUserId))
                    .findFirst()
                    .orElseThrow(() -> new UserNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, attachedUserId))));
            TaskDto expectedResult = TaskDtoMapper.mapToTaskDto(task);

            given(taskService.modifyUserAssignmentToTask(updatedTaskId, userTaskActionRequest)).willReturn(expectedResult);
            MockHttpServletResponse response = mockMvc.perform(
                            patch("/api/tasks/{id}/users", updatedTaskId)
                                    .content(objectMapper.writeValueAsString(userTaskActionRequest))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            String expectedJson = objectMapper.writeValueAsString(expectedResult);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }

        @Test
        void modifyUserAssignmentToTask_ToDetachUser_ShouldReturnUpdatedTask() throws Exception {
            Long updatedTaskId = 1L;
            Long attachedUserId = 1L;
            Action action = Action.REMOVE;
            UserTaskActionRequest userTaskActionRequest = new UserTaskActionRequest(attachedUserId, action);
            Task task = tasks.stream()
                    .filter(task1 -> task1.getId().equals(updatedTaskId))
                    .findFirst()
                    .orElseThrow(() -> new TaskNotFoundException(String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, updatedTaskId)));
            task.getUsers().add(users.stream()
                    .filter(user -> user.getId().equals(attachedUserId))
                    .findFirst()
                    .orElseThrow(() -> new UserNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, attachedUserId))));
            TaskDto expectedResult = TaskDtoMapper.mapToTaskDto(task);

            given(taskService.modifyUserAssignmentToTask(updatedTaskId, userTaskActionRequest)).willReturn(expectedResult);
            MockHttpServletResponse response = mockMvc.perform(
                            patch("/api/tasks/{id}/users", updatedTaskId)
                                    .content(objectMapper.writeValueAsString(userTaskActionRequest))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            String expectedJson = objectMapper.writeValueAsString(expectedResult);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo(expectedJson);
        }
    }

    @Nested
    @DisplayName("Tests for removeTask API")
    class RemoveTask {
        @Test
        void deleteTask_ShouldReturnOkStatus() throws Exception {
            Long taskToDeleteId = 1L;
            willDoNothing().given(taskService).delete(taskToDeleteId);
            MockHttpServletResponse response = mockMvc.perform(
                            delete("/api/tasks/{id}", taskToDeleteId)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        }
    }
}