package com.collaball.domain.team.repository;

import com.collaball.domain.project.entity.Project;
import com.collaball.domain.team.entity.TeamMember;
import com.collaball.domain.team.entity.TeamMemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    @Query("""
            select distinct p
            from TeamMember tm
            join tm.project p
            join fetch p.owner
            where tm.user.id = :userId
            order by p.createdAt desc
            """)
    List<Project> findParticipatingProjects(@Param("userId") Long userId);

    @Query("""
            select tm from TeamMember tm
            join fetch tm.user
            where tm.project.id = :projectId
            order by tm.role asc, tm.id asc
            """)
    List<TeamMember> findAllByProjectIdWithUser(@Param("projectId") Long projectId);

    Optional<TeamMember> findByProjectIdAndUserId(Long projectId, Long userId);

    boolean existsByProjectIdAndUserId(Long projectId, Long userId);

    boolean existsByProjectIdAndUserIdAndRole(Long projectId, Long userId, TeamMemberRole role);

    void deleteByProjectId(Long projectId);

    void deleteByProjectIdAndUserId(Long projectId, Long userId);
}
