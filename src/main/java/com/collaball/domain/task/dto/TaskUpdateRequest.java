package com.collaball.domain.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TaskUpdateRequest(
        @NotBlank(message = "Task title is required.")
        @Size(max = 100, message = "Task title must be 100 characters or less.")
        String title,

        @Size(max = 1000, message = "Task description must be 1000 characters or less.")
        String description,

        List<Long> assigneeIds
) {
}
