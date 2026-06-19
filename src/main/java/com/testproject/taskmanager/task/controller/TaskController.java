package com.testproject.taskmanager.task.controller;

import com.testproject.taskmanager.common.dto.ApiResponse;
import com.testproject.taskmanager.common.security.CustomUserDetails;
import com.testproject.taskmanager.task.dto.TaskRequest;
import com.testproject.taskmanager.task.model.Task;
import com.testproject.taskmanager.task.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Task>>> getMyTasks(Authentication authentication,
        @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return ResponseEntity.status(401).build();
        }
        Long userId = userDetails.getUserId();
        return ResponseEntity.ok(ApiResponse.success("Tasks fetched successfully", taskService.findByUserId(userId, pageable)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Task>> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest taskRequest, Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return ResponseEntity.status(401).build();
        }
        Long userId = userDetails.getUserId();
        Task task = taskService.updateTask(taskRequest, userId, id);
        return ResponseEntity.ok(ApiResponse.success("Task updated successfully", task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id, Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return ResponseEntity.status(401).build();
        }
        Long userId = userDetails.getUserId();
        taskService.deleteTask(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Task deleted successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Task>> createTask(@Valid @RequestBody TaskRequest taskRequest, Authentication authentication)
    {
        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return ResponseEntity.status(401).build();
        }
        Long userId = userDetails.getUserId();
        Task createdTask = taskService.createTask(taskRequest, userId);
        return ResponseEntity.ok(ApiResponse.success("Task created successfully", createdTask));
    }
}
