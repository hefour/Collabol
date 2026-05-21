package com.collaball.domain.evaluation.dto;

import com.collaball.domain.evaluation.entity.Evaluation;

import java.time.LocalDateTime;

public record EvaluationResponse(
        Long id,
        Long projectId,
        UserInfo reviewer,
        UserInfo reviewee,
        int presentationScore,
        int communicationScore,
        int collaborationScore,
        int sincerityScore,
        int planningScore,
        double averageScore,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public record UserInfo(Long userId, String name) {}

    public static EvaluationResponse from(Evaluation evaluation) {
        double avg = (evaluation.getPresentationScore()
                + evaluation.getCommunicationScore()
                + evaluation.getCollaborationScore()
                + evaluation.getSincerityScore()
                + evaluation.getPlanningScore()) / 5.0;

        return new EvaluationResponse(
                evaluation.getId(),
                evaluation.getProject().getId(),
                new UserInfo(evaluation.getReviewer().getId(), evaluation.getReviewer().getName()),
                new UserInfo(evaluation.getReviewee().getId(), evaluation.getReviewee().getName()),
                evaluation.getPresentationScore(),
                evaluation.getCommunicationScore(),
                evaluation.getCollaborationScore(),
                evaluation.getSincerityScore(),
                evaluation.getPlanningScore(),
                Math.round(avg * 10.0) / 10.0,
                evaluation.getComment(),
                evaluation.getCreatedAt(),
                evaluation.getUpdatedAt()
        );
    }
}
