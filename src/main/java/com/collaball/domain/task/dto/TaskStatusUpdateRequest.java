package com.collaball.domain.task.dto;

import com.collaball.domain.task.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record TaskStatusUpdateRequest(
        @NotNull(message = "Status is required.")
        TaskStatus status
) {
}
