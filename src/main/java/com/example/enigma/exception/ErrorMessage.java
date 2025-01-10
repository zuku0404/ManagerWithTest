package com.example.enigma.exception;

public final class ErrorMessage {
    private ErrorMessage() {}

    public static final String USER_NOT_FOUND_BY_ID = "User wit id '%s' not found";
    public static final String USER_NOT_FOUND_BY_EMAIL = "User wit email '%s' not found";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String TASK_NOT_FOUND_BY_ID = "Task with id '%s' not found";
    public static final String TASK_NOT_FOUND_BY_TITLE = "Task with title '%s' not found";
    public static final String TITLE_ALREADY_EXISTS_WITH_TITLE = "Task with title '%s' already exists";
    public static final String USER_ALREADY_ATTACHED = "User with ID '%s' is already attached to the task";
    public static final String USER_NOT_ATTACHED = "User with ID '%s' is not attached to the task";
    public static final String LOGIN_EXIST = "Email already in use";
    public static final String CURRENT_PASSWORD_INVALID = "Invalid current password";
}
