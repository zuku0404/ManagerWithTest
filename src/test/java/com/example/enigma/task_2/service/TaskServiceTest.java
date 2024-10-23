package com.example.enigma.task_2.service;

import com.example.enigma.task_2.model.entity.Task;
import com.example.enigma.task_2.model.entity.User;
import com.example.enigma.task_2.model.entity.task_dto.TaskDto;
import com.example.enigma.task_2.model.entity.task_dto.TaskWithoutIdDto;
import com.example.enigma.task_2.model.entity.task_dto.TaskWithoutUserDto;
import com.example.enigma.task_2.model.entity.task_dto.mapper.TaskDtoMapper;
import com.example.enigma.task_2.sample.TaskUserSampleData;
import com.example.enigma.task_2.exception.ErrorMessage;
import com.example.enigma.task_2.exception.task.TaskNotFoundException;
import com.example.enigma.task_2.exception.task.TitleAlreadyExistsException;
import com.example.enigma.task_2.exception.user.UserAttachedException;
import com.example.enigma.task_2.exception.user.UserNotFoundException;
import com.example.enigma.task_2.model.Action;
import com.example.enigma.task_2.model.SortDirection;
import com.example.enigma.task_2.model.TaskStatus;
import com.example.enigma.task_2.model.entity.user_dto.UserTaskActionRequest;
import com.example.enigma.task_2.repository.TaskRepository;
import com.example.enigma.task_2.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    TaskRepository taskRepository;

    @InjectMocks
    TaskService taskService;

    private List<Task> tasks;
    private List<User> users;
    private int pageSize;
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @BeforeEach
    public void setup() {
        pageSize = TaskService.PAGE_SIZE;
        users = TaskUserSampleData.createUsers();
        tasks = TaskUserSampleData.createTasks();
    }

    @Nested
    @DisplayName("Tests for findAllDetailed")
    class FindAllDetailed {
        @Test
        void shouldReturnAllTasksWhenNoFiltersAndSortingDisabled() {
            SortDirection sortDirection = SortDirection.ASC;
            boolean sort = false;
            Long userId = null;
            TaskStatus status = null;
            runFindAllDetailedTest(userId, status, sort, sortDirection);
        }

        @Test
        void shouldReturnAllTasksWhenNoFiltersAndSortingEnabledAscending() {
            SortDirection sortDirection = SortDirection.ASC;
            boolean sort = true;
            Long userId = null;
            TaskStatus status = null;
            runFindAllDetailedTest(userId, status, sort, sortDirection);
        }

        @Test
        void shouldReturnAllTasksWhenNoFiltersAndSortingEnabledDescending() {
            SortDirection sortDirection = SortDirection.DESC;
            boolean sort = true;
            Long userId = null;
            TaskStatus status = null;
            runFindAllDetailedTest(userId, status, sort, sortDirection);
        }

        @Test
        void shouldFilterTasksByUserWithoutSorting() {
            SortDirection sortDirection = SortDirection.ASC;
            boolean sort = false;
            Long userId = 1L;
            TaskStatus status = null;
            runFindAllDetailedTest(userId, status, sort, sortDirection);
        }

        @Test
        void shouldFilterTasksByUserWithSortingEnabledAscending() {
            SortDirection sortDirection = SortDirection.ASC;
            boolean sort = true;
            Long userId = 1L;
            TaskStatus status = null;
            runFindAllDetailedTest(userId, status, sort, sortDirection);
        }

        @Test
        void shouldFilterTasksByStatusWithoutSorting() {
            SortDirection sortDirection = SortDirection.ASC;
            boolean sort = false;
            Long userId = null;
            TaskStatus status = TaskStatus.TO_DO;
            runFindAllDetailedTest(userId, status, sort, sortDirection);
        }

        @Test
        void shouldFilterTasksByUserAndStatusWithoutSorting() {
            SortDirection sortDirection = SortDirection.ASC;
            boolean sort = false;
            Long userId = 1L;
            TaskStatus status = TaskStatus.TO_DO;
            runFindAllDetailedTest(userId, status, sort, sortDirection);
        }

        @Test
        void shouldFilterTasksByUserAndStatusWithSortingEnabledAscending() {
            SortDirection sortDirection = SortDirection.ASC;
            boolean sort = true;
            Long userId = 1L;
            TaskStatus status = TaskStatus.TO_DO;
            runFindAllDetailedTest(userId, status, sort, sortDirection);
        }

        private void runFindAllDetailedTest(Long userId, TaskStatus status, boolean sort, SortDirection sortDirection) {
            int pageNumber = 1;
            Sort sorted = Sort.by("id");
            if (sort) {
                sorted = (sortDirection == SortDirection.DESC) ?
                        Sort.by("deadline").descending() : Sort.by("deadline").ascending();
            }
            List<Task> expectedTasks;
            do {
                expectedTasks = tasks.stream()
                        .filter(task -> userId == null || task.getUsers().stream().map(User::getId).toList().contains(userId))
                        .filter(task -> status == null || task.getTaskStatus() == status)
                        .sorted(sort ? (sortDirection == SortDirection.ASC ?
                                Comparator.comparing(Task::getDeadline) : Comparator.comparing(Task::getDeadline).reversed()
                        ) : Comparator.comparing(Task::getId))
                        .skip((long) (pageNumber - 1) * pageSize)
                        .limit(pageSize)
                        .toList();
                List<TaskDto> expectedResult = TaskDtoMapper.mapToTaskDtos(expectedTasks);

                PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize, sorted);
                given(taskRepository.findByOptionalUserAndStatusTaskWithUser(userId, status, pageRequest)).willReturn(expectedTasks);
                List<TaskDto> result = taskService.findAllDetailed(userId, status, pageNumber, sort, sortDirection);
                logger.info("page number: {}", pageNumber);
                result.forEach(taskDto -> logger.info(taskDto.toString()));
                assertThat(result).isEqualTo(expectedResult);
                pageNumber++;
            } while (expectedTasks.size() == pageSize);
        }
    }

    @Nested
    @DisplayName("Tests for findAllBasic")
    class FindAllBasic {

        @Test
        void shouldReturnAllTasksWithoutFiltersInAscendingOrder() {
            Long userId = null;
            TaskStatus status = null;
            boolean sort = false;
            SortDirection sortDirection = SortDirection.ASC;
            runFindAllBasicTest(userId,status,sort,sortDirection);
        }

        @Test
        void shouldReturnAllTasksWithoutFiltersInDescendingOrder() {
            Long userId = null;
            TaskStatus status = null;
            boolean sort = false;
            SortDirection sortDirection = SortDirection.DESC;
            runFindAllBasicTest(userId,status,sort,sortDirection);
        }

        @Test
        void shouldReturnAllTasksWithSortingInAscendingOrder() {
            Long userId = null;
            TaskStatus status = null;
            boolean sort = true;
            SortDirection sortDirection = SortDirection.ASC;
            runFindAllBasicTest(userId,status,sort,sortDirection);
        }

        @Test
        void shouldReturnAllTasksWithSortingInDescendingOrder() {
            Long userId = null;
            TaskStatus status = null;
            boolean sort = true;
            SortDirection sortDirection = SortDirection.DESC;
            runFindAllBasicTest(userId,status,sort,sortDirection);
        }

        @Test
        void shouldReturnTasksForUserIdWithSortingInDescendingOrder() {
            Long userId = 3L;
            TaskStatus status = null;
            boolean sort = false;
            SortDirection sortDirection = SortDirection.DESC;
            runFindAllBasicTest(userId,status,sort,sortDirection);
        }

        @Test
        void shouldReturnTasksWithStatusToDoWithoutSortingInDescendingOrder() {
            Long userId = null;
            TaskStatus status = TaskStatus.TO_DO;
            boolean sort = false;
            SortDirection sortDirection = SortDirection.DESC;
            runFindAllBasicTest(userId,status,sort,sortDirection);
        }

        @Test
        void shouldReturnTasksForUserIdWithStatusToDoWithoutSortingInDescendingOrder() {
            Long userId = 1L;
            TaskStatus status = TaskStatus.TO_DO;
            boolean sort = false;
            SortDirection sortDirection = SortDirection.DESC;
            runFindAllBasicTest(userId,status,sort,sortDirection);
        }

        @Test
        void shouldReturnTasksForUserIdWithStatusToDoWithSortingInAscendingOrder() {
            Long userId = 1L;
            TaskStatus status = TaskStatus.TO_DO;
            boolean sort = true;
            SortDirection sortDirection = SortDirection.ASC;
            runFindAllBasicTest(userId,status,sort,sortDirection);
        }

        @Test
        void shouldReturnTasksForUserIdWithStatusToDoWithSortingInDescendingOrder() {
            Long userId = 1L;
            TaskStatus status = TaskStatus.TO_DO;
            boolean sort = true;
            SortDirection sortDirection = SortDirection.DESC;
            runFindAllBasicTest(userId,status,sort,sortDirection);
        }


        private void runFindAllBasicTest(Long userId, TaskStatus status, boolean sort, SortDirection sortDirection) {
            Sort sorted = Sort.by("id");
            if (sort) {
                sorted = (sortDirection == SortDirection.DESC) ?
                        Sort.by("deadline").descending() : Sort.by("deadline").ascending();
            }
            List<Task> expectedTasks = tasks.stream()
                    .filter(task -> userId == null || task.getUsers().stream().map(User::getId).toList().contains(userId))
                    .filter(task -> status == null || task.getTaskStatus() == status)
                    .sorted(sort ? (sortDirection == SortDirection.ASC ?
                            Comparator.comparing(Task::getDeadline) : Comparator.comparing(Task::getDeadline).reversed()
                    ) : Comparator.comparing(Task::getId))
                    .toList();
            List<TaskWithoutUserDto> expectedResult = TaskDtoMapper.mapToTaskWithoutUserDtos(expectedTasks);
            given(taskRepository.findByOptionalUserAndStatusTask(userId, status, PageRequest.of(0, Integer.MAX_VALUE, sorted))).willReturn(expectedTasks);
            List<TaskWithoutUserDto> result = taskService.findAllBasic(userId, status, sort, sortDirection);
            result.forEach(taskDto -> logger.info(taskDto.toString()));
            assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("Tests for findUnsigned")
    class FindUnsigned {

        @Test
        void shouldFindUnsignedTasksWithoutSortingInAscendingOrder() {
            boolean sort = false;
            SortDirection direction = SortDirection.ASC;
            runFindUnsignedTest(sort, direction);
        }

        @Test
        void shouldFindUnsignedTasksWithoutSortingInDescendingOrder() {
            boolean sort = false;
            SortDirection direction = SortDirection.DESC;
            runFindUnsignedTest(sort, direction);
        }

        @Test
        void shouldFindUnsignedTasksWithSortingInAscendingOrder() {
            boolean sort = true;
            SortDirection direction = SortDirection.ASC;
            runFindUnsignedTest(sort, direction);
        }

        @Test
        void shouldFindUnsignedTasksWithSortingInDescendingOrder() {
            boolean sort = true;
            SortDirection direction = SortDirection.DESC;
            runFindUnsignedTest(sort, direction);
        }

        private void runFindUnsignedTest(boolean sort, SortDirection sortDirection) {
            int pageNumber = 1;
            Sort sorted = Sort.by("id");
            if (sort) {
                sorted = (sortDirection == SortDirection.DESC) ?
                        Sort.by("deadline").descending() : Sort.by("deadline").ascending();
            }
            List<Task> expectedTasks;
            do {
                expectedTasks = tasks.stream()
                        .filter(task -> task.getUsers().isEmpty())
                        .sorted(sort ? (sortDirection == SortDirection.ASC ?
                                Comparator.comparing(Task::getDeadline) : Comparator.comparing(Task::getDeadline).reversed()
                        ) : Comparator.comparing(Task::getId))
                        .skip((long) (pageNumber - 1) * pageSize)
                        .limit(pageSize)
                        .toList();
                PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize, sorted);
                given(taskRepository.findUnsignedTasks(pageRequest)).willReturn(expectedTasks);
                List<TaskWithoutUserDto> expectedResult = TaskDtoMapper.mapToTaskWithoutUserDtos(expectedTasks);
                List<TaskWithoutUserDto> result = taskService.findUnsigned(pageNumber, sort,sortDirection);
                logger.info("page number: {}", pageNumber);
                result.forEach(taskDto -> logger.info(taskDto.toString()));
                assertThat(result).isEqualTo(expectedResult);
                pageNumber++;
            } while (expectedTasks.size() == pageSize);
        }
    }

    @Nested
    @DisplayName("Tests for findTaskById")
    class FindTaskById {
        @Test
        void shouldThrowTaskNotFoundExceptionWhenIdNotFound() {
            Long searchedId = 1L;
            given(taskRepository.findById(searchedId)).willReturn(Optional.empty());
            TaskNotFoundException exception = assertThrows(
                    TaskNotFoundException.class,
                    () -> taskService.findTaskById(searchedId)
            );
            assertThat(exception.getMessage())
                    .isEqualTo(String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, searchedId));
        }

        @Test
        void shouldReturnTaskDtoWhenIdExists() {
            Long searchedTaskId = 1L;
            Task task = tasks.stream()
                    .filter(t -> t.getId().equals(searchedTaskId))
                    .findFirst()
                    .orElseThrow();
            TaskDto expectedResult = TaskDtoMapper.mapToTaskDto(task);
            given(taskRepository.findById(searchedTaskId)).willReturn(Optional.of(task));
            TaskDto result = taskService.findTaskById(searchedTaskId);
            assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("Tests for findTaskByTitle")
    class FindTaskByTitle {
        @Test
        void shouldThrowTaskNotFoundExceptionWhenTitleNotFound() {
            String searchedTitle = "title";
            given(taskRepository.findByTitle(searchedTitle)).willReturn(Optional.empty());
            TaskNotFoundException exception = assertThrows(
                    TaskNotFoundException.class,
                    () -> taskService.findTaskByTitle(searchedTitle)
            );
            assertThat(exception.getMessage())
                    .isEqualTo(String.format(ErrorMessage.TASK_NOT_FOUND_BY_TITLE, searchedTitle));
        }

        @Test
        void shouldReturnTaskDtoWhenTitleExists() {
            Long searchedTaskId = 1L;
            String searchedTitle = "title";
            Task task = tasks.stream()
                    .filter(t -> t.getId().equals(searchedTaskId))
                    .findFirst()
                    .orElseThrow();
            TaskDto expectedResult = TaskDtoMapper.mapToTaskDto(task);
            given(taskRepository.findByTitle(searchedTitle)).willReturn(Optional.of(task));
            TaskDto result = taskService.findTaskByTitle(searchedTitle);
            assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("Tests for create")
    class CreateTask {
        @Test
        void shouldThrowTitleAlreadyExistsExceptionOnTaskCreation() {
            Long searchedTask = 1L;
            String title = "newTitle";
            Task task = tasks.stream()
                    .filter(t -> t.getId().equals(searchedTask))
                    .findFirst()
                    .orElseThrow();
            Task newTask = new Task(title, "newTask", TaskStatus.TO_DO, LocalDate.now());
            TaskWithoutIdDto taskWithoutIdDto = TaskDtoMapper.mapToTaskWithoutIdDto(newTask);
            given(taskRepository.findByTitle(title)).willReturn(Optional.of(task));
            TitleAlreadyExistsException exception = assertThrows(
                    TitleAlreadyExistsException.class,
                    () -> taskService.create(taskWithoutIdDto)
            );
            assertThat(exception.getMessage())
                    .isEqualTo(String.format(ErrorMessage.TITLE_ALREADY_EXISTS_WITH_TITLE, title));
        }

        @Test
        void shouldCreateTaskSuccessfullyWhenTitleDoesNotExist() {
            List<Long> userIds = List.of(1L);
            String title = "newTitle";
            List<User> userList = users.stream()
                    .filter(user -> userIds.contains(user.getId()))
                    .toList();
            Task newTask = new Task(title, "newTask", TaskStatus.TO_DO, LocalDate.now(), new HashSet<>(userList));
            TaskDto expectedResult = TaskDtoMapper.mapToTaskDto(newTask);
            TaskWithoutIdDto taskWithoutIdDto = TaskDtoMapper.mapToTaskWithoutIdDto(newTask);
            given(taskRepository.findByTitle(title)).willReturn(Optional.empty());
            given(userRepository.findAllById(userIds)).willReturn(userList);
            given(taskRepository.save(ArgumentMatchers.any(Task.class))).willReturn(newTask);
            TaskDto result = taskService.create(taskWithoutIdDto);
            assertThat(result)
                    .usingRecursiveComparison()
                    .ignoringFields("users")
                    .isEqualTo(expectedResult);
            assertThat(result.users()).containsExactlyInAnyOrderElementsOf(expectedResult.users());
        }
    }

    @Nested
    @DisplayName("Tests for update")
    class UpdateTask {
        @Test
        void shouldThrowExceptionWhenTaskNotFoundOnUpdate() {
            Long taskIdToUpdateUser = 1L;
            Task task = tasks.stream()
                    .filter(t -> t.getId().equals(taskIdToUpdateUser))
                    .findFirst()
                    .orElseThrow();
            given(taskRepository.findById(taskIdToUpdateUser)).willReturn(Optional.empty());
            TaskWithoutIdDto taskDto = TaskDtoMapper.mapToTaskWithoutIdDto(task);
            TaskNotFoundException exception = assertThrows(
                    TaskNotFoundException.class,
                    () -> taskService.update(taskIdToUpdateUser, taskDto)
            );
            assertThat(exception.getMessage())
                    .isEqualTo(String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, taskIdToUpdateUser));
        }

        @Test
        void shouldThrowTitleAlreadyExistsExceptionOnUpdate() {
            Long taskIdToUpdateUser = 1L;
            Long secondTaskId = 2L;
            String newTitle = "newTitle";
            Task task = tasks.stream()
                    .filter(t -> t.getId().equals(taskIdToUpdateUser))
                    .findFirst()
                    .orElseThrow();
            Task taskUpdated = new Task(task.getId(), newTitle, task.getDescription(), task.getTaskStatus(), task.getDeadline(), task.getUsers());
            Task secondTask = tasks.stream()
                    .filter(t -> t.getId().equals(secondTaskId))
                    .findFirst()
                    .orElseThrow();
            TaskWithoutIdDto taskDto = TaskDtoMapper.mapToTaskWithoutIdDto(taskUpdated);
            given(taskRepository.findById(taskIdToUpdateUser)).willReturn(Optional.of(task));
            given(taskRepository.findByTitle(newTitle)).willReturn(Optional.of(secondTask));
            TitleAlreadyExistsException exception = assertThrows(
                    TitleAlreadyExistsException.class,
                    () -> taskService.update(taskIdToUpdateUser, taskDto)
            );
            assertThat(exception.getMessage())
                    .isEqualTo(String.format(ErrorMessage.TITLE_ALREADY_EXISTS_WITH_TITLE, newTitle));
        }

        @Test
        void shouldUpdateTaskSuccessfullyWhenTitleDoesNotExist() {
            Long taskIdToUpdateUser = 1L;
            String newTitle = "newTitle";
            Task task = tasks.stream()
                    .filter(t -> t.getId().equals(taskIdToUpdateUser))
                    .findFirst()
                    .orElseThrow();
            Task taskUpdated = new Task(task.getId(), newTitle, task.getDescription(), task.getTaskStatus(), task.getDeadline(), task.getUsers());
            TaskWithoutIdDto taskDto = TaskDtoMapper.mapToTaskWithoutIdDto(taskUpdated);
            TaskDto expectedResult = TaskDtoMapper.mapToTaskDto(taskUpdated);
            given(taskRepository.findById(taskIdToUpdateUser)).willReturn(Optional.of(task));
            given(taskRepository.findByTitle(newTitle)).willReturn(Optional.empty());
            given(taskRepository.save(ArgumentMatchers.any(Task.class))).willReturn(taskUpdated);
            TaskDto result = taskService.update(taskIdToUpdateUser, taskDto);
            assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("Tests for changeStatus")
    class ChangeStatus {
        @Test
        void shouldThrowExceptionWhenTaskNotFoundOnChangeStatus() {
            Long taskIdToChangeStatus = 1L;
            TaskStatus newStatus = TaskStatus.IN_PROGRESS;
            given(taskRepository.findById(taskIdToChangeStatus)).willReturn(Optional.empty());
            TaskNotFoundException exception = assertThrows(
                    TaskNotFoundException.class,
                    () -> taskService.changeStatus(taskIdToChangeStatus, newStatus)
            );
            assertThat(exception.getMessage())
                    .isEqualTo(String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, taskIdToChangeStatus));
        }

        @Test
        void shouldChangeTaskStatusSuccessfully() {
            Long taskIdToChangeStatus = 1L;
            TaskStatus newStatus = TaskStatus.IN_PROGRESS;
            Task task = tasks.stream()
                    .filter(t -> t.getId().equals(taskIdToChangeStatus))
                    .findFirst()
                    .orElseThrow();
            Task taskUpdated = new Task(task.getId(), task.getTitle(), task.getDescription(), newStatus, task.getDeadline(), task.getUsers());
            given(taskRepository.findById(taskIdToChangeStatus)).willReturn(Optional.of(task));
            given(taskRepository.save(ArgumentMatchers.any(Task.class))).willReturn(taskUpdated);
            TaskDto expectedResult = TaskDtoMapper.mapToTaskDto(taskUpdated);
            TaskDto result = taskService.changeStatus(taskIdToChangeStatus, newStatus);
            assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("Tests for modifyUserAssignmentToTask")
    class ModifyUserAssignmentToTask {
        @Test
        void shouldThrowExceptionWhenTaskNotFoundOnAddUser() {
            Long taskIdToAddUser = 1L;
            Long userId = 1L;
            Action action = Action.ADD;
            UserTaskActionRequest userTaskActionRequest = new UserTaskActionRequest(userId, action);
            given(taskRepository.findById(taskIdToAddUser)).willReturn(Optional.empty());
            TaskNotFoundException exception = assertThrows(
                    TaskNotFoundException.class,
                    () -> taskService.modifyUserAssignmentToTask(taskIdToAddUser, userTaskActionRequest)
            );
            assertThat(exception.getMessage())
                    .isEqualTo(String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, taskIdToAddUser));
        }

        @Test
        void shouldThrowExceptionWhenUserNotFoundOnAddUser() {
            Long taskIdToAddUser = 1L;
            Long userId = 20L;
            Action action = Action.ADD;
            UserTaskActionRequest userTaskActionRequest = new UserTaskActionRequest(userId, action);
            Task task = tasks.stream()
                    .filter(t -> t.getId().equals(taskIdToAddUser))
                    .findFirst()
                    .orElseThrow();
            given(taskRepository.findById(taskIdToAddUser)).willReturn(Optional.of(task));
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            UserNotFoundException exception = assertThrows(
                    UserNotFoundException.class,
                    () -> taskService.modifyUserAssignmentToTask(taskIdToAddUser, userTaskActionRequest));
            assertThat(exception.getMessage())
                    .isEqualTo(String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, userId));
        }

        @Test
        void shouldThrowExceptionWhenUserAlreadyAttachedToTask() {
            Long taskIdToAddUser = 1L;
            Long userId = 1L;
            Action action = Action.ADD;
            User user = users.stream()
                    .filter(u -> u.getId().equals(userId))
                    .findFirst()
                    .orElseThrow();
            UserTaskActionRequest userTaskActionRequest = new UserTaskActionRequest(userId, action);
            Task task = tasks.stream()
                    .filter(t -> t.getId().equals(taskIdToAddUser))
                    .findFirst()
                    .orElseThrow();
            given(taskRepository.findById(taskIdToAddUser)).willReturn(Optional.of(task));
            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            UserAttachedException exception = assertThrows(
                    UserAttachedException.class,
                    () -> taskService.modifyUserAssignmentToTask(taskIdToAddUser, userTaskActionRequest));
            assertThat(exception.getMessage())
                    .isEqualTo(String.format(ErrorMessage.USER_ALREADY_ATTACHED, userId));
        }

        @Test
        void shouldAddUserToTaskSuccessfully() {
            Long taskIdToAddUser = 1L;
            Long userId = 5L;
            Action action = Action.ADD;
            UserTaskActionRequest userTaskActionRequest = new UserTaskActionRequest(userId, action);
            Task task = tasks.stream()
                    .filter(t -> t.getId().equals(taskIdToAddUser))
                    .findFirst()
                    .orElseThrow();
            User user = users.stream()
                    .filter(u -> u.getId().equals(userId))
                    .findFirst()
                    .orElseThrow();
            Set<User> updatedListOfUser = new HashSet<>(task.getUsers());
            updatedListOfUser.add(user);
            Task taskUpdated = new Task(task.getId(), task.getTitle(), task.getDescription(), task.getTaskStatus(), task.getDeadline(), updatedListOfUser);

            given(taskRepository.findById(taskIdToAddUser)).willReturn(Optional.of(task));
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(taskRepository.save(ArgumentMatchers.any(Task.class))).willReturn(taskUpdated);
            TaskDto expectedResult = TaskDtoMapper.mapToTaskDto(taskUpdated);
            TaskDto result = taskService.modifyUserAssignmentToTask(taskIdToAddUser, userTaskActionRequest);
            assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void shouldThrowExceptionWhenUserNotFoundOnRemoveUser() {
            Long taskIdToRemoveUser = 1L;
            Long userId = 20L;
            Action action = Action.REMOVE;
            UserTaskActionRequest userTaskActionRequest = new UserTaskActionRequest(userId, action);
            Task task = tasks.stream()
                    .filter(t -> t.getId().equals(taskIdToRemoveUser))
                    .findFirst()
                    .orElseThrow();
            given(taskRepository.findById(taskIdToRemoveUser)).willReturn(Optional.of(task));
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            UserNotFoundException exception = assertThrows(
                    UserNotFoundException.class,
                    () -> taskService.modifyUserAssignmentToTask(taskIdToRemoveUser, userTaskActionRequest));
            assertThat(exception.getMessage())
                    .isEqualTo(String.format(ErrorMessage.USER_NOT_FOUND_BY_ID, userId));
        }

        @Test
        void shouldThrowExceptionWhenUserNotAttachedToTaskOnRemoveUser() {
            Long taskIdToRemoveUser = 1L;
            Long userId = 3L;
            Action action = Action.REMOVE;
            User user = users.stream()
                    .filter(u -> u.getId().equals(userId))
                    .findFirst()
                    .orElseThrow();
            UserTaskActionRequest userTaskActionRequest = new UserTaskActionRequest(userId, action);
            Task task = tasks.stream()
                    .filter(t -> t.getId().equals(taskIdToRemoveUser))
                    .findFirst()
                    .orElseThrow();
            given(taskRepository.findById(taskIdToRemoveUser)).willReturn(Optional.of(task));
            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            UserAttachedException exception = assertThrows(
                    UserAttachedException.class,
                    () -> taskService.modifyUserAssignmentToTask(taskIdToRemoveUser, userTaskActionRequest));
            assertThat(exception.getMessage())
                    .isEqualTo(String.format(ErrorMessage.USER_NOT_ATTACHED, userId));
        }

        @Test
        void shouldRemoveUserFromTaskSuccessfully() {
            Long taskIdToRemoveUser = 1L;
            Long userId = 1L;
            Action action = Action.REMOVE;
            UserTaskActionRequest userTaskActionRequest = new UserTaskActionRequest(userId, action);
            Task task = tasks.stream()
                    .filter(t -> t.getId().equals(taskIdToRemoveUser))
                    .findFirst()
                    .orElseThrow();
            User user = users.stream()
                    .filter(u -> u.getId().equals(userId))
                    .findFirst()
                    .orElseThrow();
            Set<User> updatedListOfUser = new HashSet<>(task.getUsers());
            updatedListOfUser.add(user);
            Task taskUpdated = new Task(task.getId(), task.getTitle(), task.getDescription(), task.getTaskStatus(), task.getDeadline(), updatedListOfUser);

            given(taskRepository.findById(taskIdToRemoveUser)).willReturn(Optional.of(task));
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(taskRepository.save(ArgumentMatchers.any(Task.class))).willReturn(taskUpdated);
            TaskDto expectedResult = TaskDtoMapper.mapToTaskDto(taskUpdated);
            TaskDto result = taskService.modifyUserAssignmentToTask(taskIdToRemoveUser, userTaskActionRequest);
            assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("Tests for delete")
    class DeleteTask {
        @Test
        void shouldDeleteTaskWhenTaskExists() {
            Long taskToDeleteId = 1L;
            Task task = tasks.stream()
                    .filter(t -> t.getId().equals(taskToDeleteId))
                    .findFirst()
                    .orElseThrow();
            given(taskRepository.findById(taskToDeleteId)).willReturn(Optional.of(task));
            willDoNothing().given(taskRepository).deleteById(taskToDeleteId);
            taskService.delete(taskToDeleteId);
            verify(taskRepository, times(1)).deleteById(taskToDeleteId);
        }

        @Test
        void shouldThrowExceptionWhenTaskNotFound() {
            Long taskToDeleteId = 1L;
            given(taskRepository.findById(taskToDeleteId)).willReturn(Optional.empty());
            TaskNotFoundException exception = assertThrows(
                    TaskNotFoundException.class,
                    () -> taskService.delete(taskToDeleteId)
            );
            assertThat(exception.getMessage())
                    .isEqualTo(String.format(ErrorMessage.TASK_NOT_FOUND_BY_ID, taskToDeleteId));
        }
    }
}
