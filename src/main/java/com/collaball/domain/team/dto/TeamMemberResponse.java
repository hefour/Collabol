package com.collaball.domain.team.dto;

import com.collaball.domain.team.entity.TeamMember;

public record TeamMemberResponse(
        Long userId,
        String name,
        String email,
        String department,
        String role
) {
    public static TeamMemberResponse from(TeamMember teamMember) {
        return new TeamMemberResponse(
                teamMember.getUser().getId(),
                teamMember.getUser().getName(),
                teamMember.getUser().getEmail(),
                teamMember.getUser().getDepartment().name(),
                teamMember.getRole().name()
        );
    }
}
