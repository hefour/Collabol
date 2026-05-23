package com.collaball.domain.evaluation.repository;

import com.collaball.domain.evaluation.dto.ScoreSummary;
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

    long countByProjectId(Long projectId);

    void deleteByProjectId(Long projectId);

    @Query("""
            SELECT
                AVG(e.presentationScore)  AS presentationAvg,
                AVG(e.communicationScore) AS communicationAvg,
                AVG(e.collaborationScore) AS collaborationAvg,
                AVG(e.sincerityScore)     AS sincerityAvg,
                AVG(e.planningScore)      AS planningAvg,
                COUNT(e)                  AS totalCount
            FROM Evaluation e
            WHERE e.reviewee.id = :revieweeId
            """)
    ScoreSummary findScoreSummaryByRevieweeId(@Param("revieweeId") Long revieweeId);
}
