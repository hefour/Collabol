package com.collaball.domain.task.service;

import com.collaball.common.api.code.ErrorCode;
import com.collaball.common.exception.BusinessException;
import com.collaball.domain.project.entity.Project;
import com.collaball.domain.project.repository.ProjectRepository;
import com.collaball.domain.task.dto.TaskCreateRequest;
import com.collaball.domain.task.dto.TaskResponse;
import com.collaball.domain.task.dto.TaskStatusUpdateRequest;
import com.collaball.domain.task.dto.TaskUpdateRequest;
import com.collaball.domain.task.entity.Task;
import com.collaball.domain.task.entity.TaskAssign;
import com.collaball.domain.task.repository.TaskAssignRepository;
import com.collaball.domain.task.repository.TaskRepository;
import com.collaball.domain.team.entity.TeamMemberRole;
import com.collaball.domain.team.repository.TeamMemberRepository;
import com.collaball.domain.user.entity.User;
import com.collaball.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskAssignRepository taskAssignRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public TaskResponse createTask(Long projectId, TaskCreateRequest request, User currentUser) {
        validateLeaderPermission(projectId, currentUser);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));
        Task task = Task.create(project, request.title(), request.description());
        taskRepository.save(task);
        List<User> assignees = saveAssignees(task, projectId, request.assigneeIds());
        return TaskResponse.of(task, assignees);
    }

    public List<TaskResponse> getTasks(Long projectId, User currentUser) {
        validateMemberPermission(projectId, currentUser);
        return taskRepository.findByProjectIdOrderByCreatedAtDesc(projectId).stream()
                .map(task -> TaskResponse.of(task, taskAssignRepository.findUsersByTaskId(task.getId())))
                .toList();
    }

    public TaskResponse getTask(Long projectId, Long taskId, User currentUser) {
        validateMemberPermission(projectId, currentUser);
        Task task = getTaskInProject(taskId, projectId);
        List<User> assignees = taskAssignRepository.findUsersByTaskId(taskId);
        return TaskResponse.of(task, assignees);
    }

    @Transactional
    public TaskResponse updateTask(Long projectId, Long taskId, TaskUpdateRequest request, User currentUser) {
        validateLeaderPermission(projectId, currentUser);
        Task task = getTaskInProject(taskId, projectId);
        task.update(request.title(), request.description());
        taskAssignRepository.deleteByTaskId(taskId);
        List<User> assignees = saveAssignees(task, projectId, request.assigneeIds());
        return TaskResponse.of(task, assignees);
    }

    @Transactional
    public TaskResponse updateTaskStatus(Long projectId, Long taskId, TaskStatusUpdateRequest request, User currentUser) {
        validateMemberPermission(projectId, currentUser);
        Task task = getTaskInProject(taskId, projectId);
        task.updateStatus(request.status());
        List<User> assignees = taskAssignRepository.findUsersByTaskId(taskId);
        return TaskResponse.of(task, assignees);
    }

    @Transactional
    public void deleteTask(Long projectId, Long taskId, User currentUser) {
        validateLeaderPermission(projectId, currentUser);
        Task task = getTaskInProject(taskId, projectId);
        taskAssignRepository.deleteByTaskId(taskId);
        taskRepository.delete(task);
    }

    private void validateLeaderPermission(Long projectId, User currentUser) {
        if (!projectRepository.existsById(projectId)) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
        }
        if (!teamMemberRepository.existsByProjectIdAndUserIdAndRole(projectId, currentUser.getId(), TeamMemberRole.LEADER)) {
            throw new BusinessException(ErrorCode.PROJECT_ACCESS_DENIED);
        }
    }

    private void validateMemberPermission(Long projectId, User currentUser) {
        if (!projectRepository.existsById(projectId)) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
        }
        if (!teamMemberRepository.existsByProjectIdAndUserId(projectId, currentUser.getId())) {
            throw new BusinessException(ErrorCode.PROJECT_ACCESS_DENIED);
        }
    }

    private Task getTaskInProject(Long taskId, Long projectId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TASK_NOT_FOUND));
        if (!task.getProject().getId().equals(projectId)) {
            throw new BusinessException(ErrorCode.TASK_NOT_FOUND);
        }
        return task;
    }

    private List<User> saveAssignees(Task task, Long projectId, List<Long> assigneeIds) {
        if (assigneeIds == null || assigneeIds.isEmpty()) {
            return List.of();
        }
        return assigneeIds.stream()
                .map(userId -> {
                    if (!teamMemberRepository.existsByProjectIdAndUserId(projectId, userId)) {
                        throw new BusinessException(ErrorCode.ASSIGNEE_NOT_PROJECT_MEMBER);
                    }
                    return userRepository.findById(userId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
                })
                .peek(user -> taskAssignRepository.save(TaskAssign.of(task, user)))
                .toList();
    }
}
