package com.collaball.domain.task.dto;

import com.collaball.domain.task.entity.Task;
import com.collaball.domain.task.entity.TaskStatus;
import com.collaball.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public record TaskResponse(
        Long id,
        Long projectId,
        String title,
        String description,
        TaskStatus status,
        String statusLabel,
        List<AssigneeInfo> assignees,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public record AssigneeInfo(Long userId, String name) {
        public static AssigneeInfo from(User user) {
            return new AssigneeInfo(user.getId(), user.getName());
        }
    }

    public static TaskResponse of(Task task, List<User> assignees) {
        return new TaskResponse(
                task.getId(),
                task.getProject().getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getStatus().getLabel(),
                assignees.stream().map(AssigneeInfo::from).toList(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
