package com.collaball.domain.task.entity;

import com.collaball.domain.project.entity.Project;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

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
    private Task(Project project, String title, String description) {
        this.project = project;
        this.title = title;
        this.description = description;
        this.status = TaskStatus.TODO;
    }

    public static Task create(Project project, String title, String description) {
        return Task.builder()
                .project(project)
                .title(title)
                .description(description)
                .build();
    }

    public void update(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void updateStatus(TaskStatus status) {
        this.status = status;
    }
}
