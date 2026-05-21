package com.collaball.domain.project.repository;

import com.collaball.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    Optional<Project> findByInviteCode(String inviteCode);
}
