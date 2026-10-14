package com.taskmanager.backend.task;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody CreateTaskRequest request) {
        Task task = taskRepository.save(new Task(
                request.title(),
                request.description() == null ? "" : request.description(),
                request.status(),
                userId));
        return ResponseEntity.ok(Map.of("message", "Task created successfully", "task", task));
    }

    @GetMapping
    public List<Task> getAll(@AuthenticationPrincipal String userId) {
        return taskRepository.findByUserId(userId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(
            @AuthenticationPrincipal String userId,
            @PathVariable String id,
            @RequestBody UpdateTaskRequest request) {
        Task task = taskRepository.findByIdAndUserId(id, userId).orElse(null);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Task not found"));
        }

        if (request.title() != null) {
            task.setTitle(request.title());
        }
        if (request.description() != null) {
            task.setDescription(request.description());
        }
        if (request.status() != null) {
            task.setStatus(request.status());
        }
        task.setUpdatedAt(Instant.now());
        task = taskRepository.save(task);

        return ResponseEntity.ok(Map.of("message", "Task updated successfully", "task", task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(
            @AuthenticationPrincipal String userId,
            @PathVariable String id) {
        if (taskRepository.findByIdAndUserId(id, userId).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Task not found"));
        }
        taskRepository.deleteByIdAndUserId(id, userId);
        return ResponseEntity.ok(Map.of("message", "Task deleted successfully"));
    }

    public record CreateTaskRequest(
            @NotBlank String title,
            String description,
            String status) {
    }

    public record UpdateTaskRequest(String title, String description, String status) {
    }
}
