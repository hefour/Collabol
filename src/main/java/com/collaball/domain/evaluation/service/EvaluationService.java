package com.collaball.domain.evaluation.service;

import com.collaball.common.api.code.ErrorCode;
import com.collaball.common.exception.BusinessException;
import com.collaball.domain.evaluation.dto.EvaluationCreateRequest;
import com.collaball.domain.evaluation.dto.EvaluationResponse;
import com.collaball.domain.evaluation.dto.EvaluationUpdateRequest;
import com.collaball.domain.evaluation.entity.Evaluation;
import com.collaball.domain.evaluation.repository.EvaluationRepository;
import com.collaball.domain.project.entity.Project;
import com.collaball.domain.project.repository.ProjectRepository;
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
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public EvaluationResponse createEvaluation(Long projectId, EvaluationCreateRequest request, User currentUser) {
        Project project = getProject(projectId);
        validateMember(projectId, currentUser.getId());

        if (currentUser.getId().equals(request.revieweeId())) {
            throw new BusinessException(ErrorCode.SELF_EVALUATION_NOT_ALLOWED);
        }

        validateMember(projectId, request.revieweeId());

        if (evaluationRepository.existsByReviewerIdAndRevieweeIdAndProjectId(
                currentUser.getId(), request.revieweeId(), projectId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EVALUATION);
        }

        User reviewee = userRepository.findById(request.revieweeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Evaluation evaluation = Evaluation.create(
                currentUser, reviewee, project,
                request.presentationScore(),
                request.communicationScore(),
                request.collaborationScore(),
                request.sincerityScore(),
                request.planningScore(),
                request.comment()
        );

        return EvaluationResponse.from(evaluationRepository.save(evaluation));
    }

    public List<EvaluationResponse> getMyEvaluations(Long projectId, User currentUser) {
        getProject(projectId);
        validateMember(projectId, currentUser.getId());
        return evaluationRepository.findByReviewerIdAndProjectId(currentUser.getId(), projectId)
                .stream()
                .map(EvaluationResponse::from)
                .toList();
    }

    public List<EvaluationResponse> getReceivedEvaluations(Long projectId, User currentUser) {
        getProject(projectId);
        validateMember(projectId, currentUser.getId());
        return evaluationRepository.findByRevieweeIdAndProjectId(currentUser.getId(), projectId)
                .stream()
                .map(EvaluationResponse::from)
                .toList();
    }

    @Transactional
    public EvaluationResponse updateEvaluation(Long projectId, Long evaluationId,
                                               EvaluationUpdateRequest request, User currentUser) {
        getProject(projectId);
        Evaluation evaluation = getEvaluationInProject(evaluationId, projectId);
        validateOwner(evaluation, currentUser.getId());

        evaluation.update(
                request.presentationScore(),
                request.communicationScore(),
                request.collaborationScore(),
                request.sincerityScore(),
                request.planningScore(),
                request.comment()
        );

        return EvaluationResponse.from(evaluation);
    }

    @Transactional
    public void deleteEvaluation(Long projectId, Long evaluationId, User currentUser) {
        getProject(projectId);
        Evaluation evaluation = getEvaluationInProject(evaluationId, projectId);
        validateOwner(evaluation, currentUser.getId());
        evaluationRepository.delete(evaluation);
    }

    private Project getProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));
    }

    private void validateMember(Long projectId, Long userId) {
        if (!teamMemberRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new BusinessException(ErrorCode.PROJECT_ACCESS_DENIED);
        }
    }

    private Evaluation getEvaluationInProject(Long evaluationId, Long projectId) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EVALUATION_NOT_FOUND));
        if (!evaluation.getProject().getId().equals(projectId)) {
            throw new BusinessException(ErrorCode.EVALUATION_NOT_FOUND);
        }
        return evaluation;
    }

    private void validateOwner(Evaluation evaluation, Long userId) {
        if (!evaluation.getReviewer().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }
}
