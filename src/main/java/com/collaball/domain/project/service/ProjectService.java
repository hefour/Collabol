package com.collaball.domain.project.service;

import com.collaball.common.api.code.ErrorCode;
import com.collaball.common.exception.BusinessException;
import com.collaball.domain.project.dto.ProjectCreateRequest;
import com.collaball.domain.project.dto.ProjectResponse;
import com.collaball.domain.project.dto.ProjectUpdateRequest;
import com.collaball.domain.project.entity.Project;
import com.collaball.domain.project.repository.ProjectRepository;
import com.collaball.domain.task.repository.TaskAssignRepository;
import com.collaball.domain.task.repository.TaskRepository;
import com.collaball.domain.team.entity.TeamMember;
import com.collaball.domain.team.repository.TeamMemberRepository;
import com.collaball.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TaskRepository taskRepository;
    private final TaskAssignRepository taskAssignRepository;

    @Transactional
    public ProjectResponse createProject(ProjectCreateRequest request, User currentUser) {
        Project project = Project.create(request.name(), request.description(), currentUser);
        Project savedProject = projectRepository.save(project);
        teamMemberRepository.save(TeamMember.leader(savedProject, currentUser));
        return ProjectResponse.from(savedProject, currentUser.getId());
    }

    public List<ProjectResponse> getParticipatingProjects(User currentUser) {
        return teamMemberRepository.findParticipatingProjects(currentUser.getId()).stream()
                .map(project -> ProjectResponse.from(project, currentUser.getId()))
                .toList();
    }

    public ProjectResponse getProject(Long projectId, User currentUser) {
        Project project = getProjectWithViewPermission(projectId, currentUser);
        return ProjectResponse.from(project, currentUser.getId());
    }

    @Transactional
    public ProjectResponse updateProject(Long projectId, ProjectUpdateRequest request, User currentUser) {
        Project project = getProjectWithEditPermission(projectId, currentUser);
        project.update(request.name(), request.description());
        return ProjectResponse.from(project, currentUser.getId());
    }

    @Transactional
    public void deleteProject(Long projectId, User currentUser) {
        Project project = getProjectWithEditPermission(projectId, currentUser);
        List<Long> taskIds = taskRepository.findByProjectId(projectId).stream()
                .map(task -> task.getId())
                .toList();
        if (!taskIds.isEmpty()) {
            taskAssignRepository.deleteByTaskIdIn(taskIds);
            taskRepository.deleteAllById(taskIds);
        }
        teamMemberRepository.deleteByProjectId(projectId);
        projectRepository.delete(project);
    }

    public String getInviteCode(Long projectId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));
        if (!teamMemberRepository.existsByProjectIdAndUserIdAndRole(projectId, currentUser.getId(), com.collaball.domain.team.entity.TeamMemberRole.LEADER)) {
            throw new BusinessException(ErrorCode.PROJECT_ACCESS_DENIED);
        }
        return project.getInviteCode();
    }

    @Transactional
    public String regenerateInviteCode(Long projectId, User currentUser) {
        Project project = getProjectWithEditPermission(projectId, currentUser);
        project.regenerateInviteCode();
        return project.getInviteCode();
    }

    private Project getProjectWithViewPermission(Long projectId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        if (!teamMemberRepository.existsByProjectIdAndUserId(projectId, currentUser.getId())) {
            throw new BusinessException(ErrorCode.PROJECT_ACCESS_DENIED);
        }

        return project;
    }

    private Project getProjectWithEditPermission(Long projectId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        if (!project.isOwnedBy(currentUser.getId())) {
            throw new BusinessException(ErrorCode.PROJECT_ACCESS_DENIED);
        }

        return project;
    }
}
