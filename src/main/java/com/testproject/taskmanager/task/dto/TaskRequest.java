package com.testproject.taskmanager.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskRequest {
    @NotBlank(message = "title is required")
    @Size(min = 3, message = "Title must be at least 3 characters")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    private boolean completed;
}
