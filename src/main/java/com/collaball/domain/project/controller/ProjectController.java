package com.collaball.domain.project.controller;

import com.collaball.common.api.code.ResponseCode;
import com.collaball.common.api.response.ApiResponse;
import com.collaball.domain.project.dto.InviteCodeResponse;
import com.collaball.domain.project.dto.ProjectCreateRequest;
import com.collaball.domain.project.dto.ProjectResponse;
import com.collaball.domain.project.dto.ProjectUpdateRequest;
import com.collaball.domain.project.service.ProjectService;
import com.collaball.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody ProjectCreateRequest request
    ) {
        ProjectResponse response = projectService.createProject(request, currentUser);
        return ApiResponse.created(ResponseCode.PROJECT_CREATED, response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getMyProjects(
            @AuthenticationPrincipal User currentUser
    ) {
        List<ProjectResponse> response = projectService.getParticipatingProjects(currentUser);
        return ApiResponse.ok(ResponseCode.PROJECT_FOUND, response);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProject(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long projectId
    ) {
        ProjectResponse response = projectService.getProject(projectId, currentUser);
        return ApiResponse.ok(ResponseCode.PROJECT_FOUND, response);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectUpdateRequest request
    ) {
        ProjectResponse response = projectService.updateProject(projectId, request, currentUser);
        return ApiResponse.ok(ResponseCode.PROJECT_UPDATED, response);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long projectId
    ) {
        projectService.deleteProject(projectId, currentUser);
        return ApiResponse.ok(ResponseCode.PROJECT_DELETED);
    }

    @GetMapping("/{projectId}/invite-code")
    public ResponseEntity<ApiResponse<InviteCodeResponse>> getInviteCode(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long projectId
    ) {
        String inviteCode = projectService.getInviteCode(projectId, currentUser);
        return ApiResponse.ok(ResponseCode.INVITE_CODE_FOUND, new InviteCodeResponse(inviteCode));
    }

    @PostMapping("/{projectId}/invite-code/regenerate")
    public ResponseEntity<ApiResponse<InviteCodeResponse>> regenerateInviteCode(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long projectId
    ) {
        String inviteCode = projectService.regenerateInviteCode(projectId, currentUser);
        return ApiResponse.ok(ResponseCode.INVITE_CODE_REGENERATED, new InviteCodeResponse(inviteCode));
    }
}
