package com.collaball.domain.team.controller;

import com.collaball.common.api.code.ResponseCode;
import com.collaball.common.api.response.ApiResponse;
import com.collaball.domain.team.dto.TeamMemberResponse;
import com.collaball.domain.team.dto.TeamMemberRoleUpdateRequest;
import com.collaball.domain.team.service.TeamMemberService;
import com.collaball.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
@RequiredArgsConstructor
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TeamMemberResponse>>> getMembers(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long projectId
    ) {
        List<TeamMemberResponse> response = teamMemberService.getMembers(projectId, currentUser);
        return ApiResponse.ok(ResponseCode.TEAM_MEMBER_FOUND, response);
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<ApiResponse<TeamMemberResponse>> updateMemberRole(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @Valid @RequestBody TeamMemberRoleUpdateRequest request
    ) {
        TeamMemberResponse response = teamMemberService.updateMemberRole(projectId, userId, request, currentUser);
        return ApiResponse.ok(ResponseCode.TEAM_MEMBER_ROLE_UPDATED, response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long projectId,
            @PathVariable Long userId
    ) {
        teamMemberService.removeMember(projectId, userId, currentUser);
        return ApiResponse.ok(ResponseCode.TEAM_MEMBER_REMOVED);
    }
}
