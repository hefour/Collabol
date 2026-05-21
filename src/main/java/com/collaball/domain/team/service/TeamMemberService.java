package com.collaball.domain.team.service;

import com.collaball.common.api.code.ErrorCode;
import com.collaball.common.exception.BusinessException;
import com.collaball.domain.project.entity.Project;
import com.collaball.domain.project.repository.ProjectRepository;
import com.collaball.domain.team.dto.TeamMemberResponse;
import com.collaball.domain.team.dto.TeamMemberRoleUpdateRequest;
import com.collaball.domain.team.entity.TeamMember;
import com.collaball.domain.team.entity.TeamMemberRole;
import com.collaball.domain.team.repository.TeamMemberRepository;
import com.collaball.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public TeamMemberResponse joinByInviteCode(String inviteCode, User currentUser) {
        Project project = projectRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVITE_CODE_NOT_FOUND));
        if (teamMemberRepository.existsByProjectIdAndUserId(project.getId(), currentUser.getId())) {
            throw new BusinessException(ErrorCode.ALREADY_PROJECT_MEMBER);
        }
        TeamMember saved = teamMemberRepository.save(TeamMember.member(project, currentUser));
        return TeamMemberResponse.from(saved);
    }

    public List<TeamMemberResponse> getMembers(Long projectId, User currentUser) {
        validateMember(projectId, currentUser);
        return teamMemberRepository.findAllByProjectIdWithUser(projectId).stream()
                .map(TeamMemberResponse::from)
                .toList();
    }

    @Transactional
    public TeamMemberResponse updateMemberRole(Long projectId, Long targetUserId, TeamMemberRoleUpdateRequest request, User currentUser) {
        validateLeader(projectId, currentUser);
        if (currentUser.getId().equals(targetUserId)) {
            throw new BusinessException(ErrorCode.CANNOT_CHANGE_OWN_ROLE);
        }
        TeamMember teamMember = teamMemberRepository.findByProjectIdAndUserId(projectId, targetUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_MEMBER_NOT_FOUND));
        teamMember.updateRole(request.role());
        return TeamMemberResponse.from(teamMember);
    }

    @Transactional
    public void removeMember(Long projectId, Long targetUserId, User currentUser) {
        validateLeader(projectId, currentUser);
        if (currentUser.getId().equals(targetUserId)) {
            throw new BusinessException(ErrorCode.CANNOT_REMOVE_SELF);
        }
        TeamMember teamMember = teamMemberRepository.findByProjectIdAndUserId(projectId, targetUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_MEMBER_NOT_FOUND));
        teamMemberRepository.delete(teamMember);
    }

    private void validateLeader(Long projectId, User currentUser) {
        if (!projectRepository.existsById(projectId)) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
        }
        if (!teamMemberRepository.existsByProjectIdAndUserIdAndRole(projectId, currentUser.getId(), TeamMemberRole.LEADER)) {
            throw new BusinessException(ErrorCode.PROJECT_ACCESS_DENIED);
        }
    }

    private void validateMember(Long projectId, User currentUser) {
        if (!projectRepository.existsById(projectId)) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
        }
        if (!teamMemberRepository.existsByProjectIdAndUserId(projectId, currentUser.getId())) {
            throw new BusinessException(ErrorCode.PROJECT_ACCESS_DENIED);
        }
    }
}
