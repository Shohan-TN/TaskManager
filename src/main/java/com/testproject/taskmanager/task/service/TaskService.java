package com.testproject.taskmanager.task.service;

import com.testproject.taskmanager.auth.exception.UserAccessDeniedException;
import com.testproject.taskmanager.auth.exception.UserNotFoundException;
import com.testproject.taskmanager.auth.model.User;
import com.testproject.taskmanager.auth.repository.UserRepository;
import com.testproject.taskmanager.task.dto.TaskRequest;
import com.testproject.taskmanager.task.exception.TaskNotFoundException;
import com.testproject.taskmanager.task.model.Task;
import com.testproject.taskmanager.task.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    public TaskService(TaskRepository taskRepository,  UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Page<Task> findByUserId(Long userId, Pageable pageable) {
        return taskRepository.findByUserId(userId, pageable);
    }

    public Task createTask(TaskRequest taskRequest, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                UserNotFoundException::new
        );
        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setCompleted(taskRequest.isCompleted());
        task.setUserId(user.getId());
        return taskRepository.save(task);
    }

    public Task updateTask(TaskRequest taskRequest, Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                TaskNotFoundException::new
        );

        if (!task.getUserId().equals(userId)){
            throw new UserAccessDeniedException();
        }
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setCompleted(taskRequest.isCompleted());
        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                TaskNotFoundException::new
        );
        if (!task.getUserId().equals(userId)){
            throw new UserAccessDeniedException();
        }
        taskRepository.delete(task);
    }
}
