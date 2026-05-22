package com.collaball.domain.evaluation.entity;

import com.collaball.domain.project.entity.Project;
import com.collaball.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "peer_review",
        uniqueConstraints = @UniqueConstraint(columnNames = {"reviewer_id", "reviewee_id", "project_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewee_id", nullable = false)
    private User reviewee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private int presentationScore;

    @Column(nullable = false)
    private int communicationScore;

    @Column(nullable = false)
    private int collaborationScore;

    @Column(nullable = false)
    private int sincerityScore;

    @Column(nullable = false)
    private int planningScore;

    @Column(length = 1000)
    private String comment;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    private Evaluation(User reviewer, User reviewee, Project project,
                        int presentationScore, int communicationScore,
                        int collaborationScore, int sincerityScore,
                        int planningScore, String comment) {
        this.reviewer = reviewer;
        this.reviewee = reviewee;
        this.project = project;
        this.presentationScore = presentationScore;
        this.communicationScore = communicationScore;
        this.collaborationScore = collaborationScore;
        this.sincerityScore = sincerityScore;
        this.planningScore = planningScore;
        this.comment = comment;
    }

    public static Evaluation create(User reviewer, User reviewee, Project project,
                                    int presentationScore, int communicationScore,
                                    int collaborationScore, int sincerityScore,
                                    int planningScore, String comment) {
        return Evaluation.builder()
                .reviewer(reviewer)
                .reviewee(reviewee)
                .project(project)
                .presentationScore(presentationScore)
                .communicationScore(communicationScore)
                .collaborationScore(collaborationScore)
                .sincerityScore(sincerityScore)
                .planningScore(planningScore)
                .comment(comment)
                .build();
    }

    public void update(int presentationScore, int communicationScore,
                       int collaborationScore, int sincerityScore,
                       int planningScore, String comment) {
        this.presentationScore = presentationScore;
        this.communicationScore = communicationScore;
        this.collaborationScore = collaborationScore;
        this.sincerityScore = sincerityScore;
        this.planningScore = planningScore;
        this.comment = comment;
        this.updatedAt = LocalDateTime.now();
    }
}
