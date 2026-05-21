package com.collaball.domain.project.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.collaball.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status;

    @Column(nullable = false, unique = true, length = 36)
    private String inviteCode;

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
    private Project(String name, String description, User owner, ProjectStatus status) {
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.status = status == null ? ProjectStatus.IN_PROGRESS : status;
        this.inviteCode = UUID.randomUUID().toString();
    }

    public static Project create(String name, String description, User owner) {
        return Project.builder()
                .name(name)
                .description(description)
                .owner(owner)
                .status(ProjectStatus.IN_PROGRESS)
                .build();
    }

    public void regenerateInviteCode() {
        this.inviteCode = UUID.randomUUID().toString();
    }

    public void update(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public boolean isOwnedBy(Long userId) {
        return owner != null && owner.getId().equals(userId);
    }
}
