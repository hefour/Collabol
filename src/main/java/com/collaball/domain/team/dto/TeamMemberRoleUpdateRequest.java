package com.collaball.domain.team.dto;

import com.collaball.domain.team.entity.TeamMemberRole;
import jakarta.validation.constraints.NotNull;

public record TeamMemberRoleUpdateRequest(
        @NotNull TeamMemberRole role
) {
}
