package com.collaball.domain.task.controller;

import com.collaball.common.api.code.ResponseCode;
import com.collaball.common.api.response.ApiResponse;
import com.collaball.domain.task.dto.TaskCreateRequest;
import com.collaball.domain.task.dto.TaskResponse;
import com.collaball.domain.task.dto.TaskStatusUpdateRequest;
import com.collaball.domain.task.dto.TaskUpdateRequest;
import com.collaball.domain.task.service.TaskService;
import com.collaball.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long projectId,
            @Valid @RequestBody TaskCreateRequest request
    ) {
        TaskResponse response = taskService.createTask(projectId, request, currentUser);
        return ApiResponse.created(ResponseCode.TASK_CREATED, response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasks(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long projectId
    ) {
        List<TaskResponse> response = taskService.getTasks(projectId, currentUser);
        return ApiResponse.ok(ResponseCode.TASK_FOUND, response);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTask(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long projectId,
            @PathVariable Long taskId
    ) {
        TaskResponse response = taskService.getTask(projectId, taskId, currentUser);
        return ApiResponse.ok(ResponseCode.TASK_FOUND, response);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @Valid @RequestBody TaskUpdateRequest request
    ) {
        TaskResponse response = taskService.updateTask(projectId, taskId, request, currentUser);
        return ApiResponse.ok(ResponseCode.TASK_UPDATED, response);
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @Valid @RequestBody TaskStatusUpdateRequest request
    ) {
        TaskResponse response = taskService.updateTaskStatus(projectId, taskId, request, currentUser);
        return ApiResponse.ok(ResponseCode.TASK_UPDATED, response);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long projectId,
            @PathVariable Long taskId
    ) {
        taskService.deleteTask(projectId, taskId, currentUser);
        return ApiResponse.ok(ResponseCode.TASK_DELETED);
    }
}
