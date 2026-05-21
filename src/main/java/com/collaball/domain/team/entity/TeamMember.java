package com.collaball.domain.team.entity;

import com.collaball.domain.project.entity.Project;
import com.collaball.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "team_member",
        uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "user_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamMemberRole role;

    @Builder
    private TeamMember(Project project, User user, TeamMemberRole role) {
        this.project = project;
        this.user = user;
        this.role = role;
    }

    public static TeamMember leader(Project project, User user) {
        return TeamMember.builder()
                .project(project)
                .user(user)
                .role(TeamMemberRole.LEADER)
                .build();
    }

    public static TeamMember member(Project project, User user) {
        return TeamMember.builder()
                .project(project)
                .user(user)
                .role(TeamMemberRole.MEMBER)
                .build();
    }

    public void updateRole(TeamMemberRole role) {
        this.role = role;
    }
}
