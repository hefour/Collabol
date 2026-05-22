package com.collaball.domain.evaluation.repository;

import com.collaball.domain.evaluation.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    boolean existsByReviewerIdAndRevieweeIdAndProjectId(Long reviewerId, Long revieweeId, Long projectId);

    @Query("SELECT e FROM Evaluation e JOIN FETCH e.reviewer JOIN FETCH e.reviewee JOIN FETCH e.project WHERE e.reviewer.id = :reviewerId AND e.project.id = :projectId")
    List<Evaluation> findByReviewerIdAndProjectId(@Param("reviewerId") Long reviewerId, @Param("projectId") Long projectId);

    @Query("SELECT e FROM Evaluation e JOIN FETCH e.reviewer JOIN FETCH e.reviewee JOIN FETCH e.project WHERE e.reviewee.id = :revieweeId AND e.project.id = :projectId")
    List<Evaluation> findByRevieweeIdAndProjectId(@Param("revieweeId") Long revieweeId, @Param("projectId") Long projectId);
}
