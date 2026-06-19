package com.testproject.taskmanager.auth.exception;

public class UserAccessDeniedException extends RuntimeException {
    public UserAccessDeniedException() {
        super("User access denied");
    }
}
