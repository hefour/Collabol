package com.collaball.domain.profile.dto;

import com.collaball.domain.evaluation.dto.ScoreSummary;
import com.collaball.domain.user.entity.User;

public record ProfileResponse(
        Long id,
        String name,
        String department,
        String bio,
        double presentationAvg,
        double communicationAvg,
        double collaborationAvg,
        double sincerityAvg,
        double planningAvg,
        double overallAvg,
        long reviewCount,
        long projectCount
) {
    public static ProfileResponse of(User user, ScoreSummary summary, long projectCount) {
        double presentation  = summary != null && summary.getPresentationAvg()  != null ? summary.getPresentationAvg()  : 0.0;
        double communication = summary != null && summary.getCommunicationAvg() != null ? summary.getCommunicationAvg() : 0.0;
        double collaboration = summary != null && summary.getCollaborationAvg() != null ? summary.getCollaborationAvg() : 0.0;
        double sincerity     = summary != null && summary.getSincerityAvg()     != null ? summary.getSincerityAvg()     : 0.0;
        double planning      = summary != null && summary.getPlanningAvg()      != null ? summary.getPlanningAvg()      : 0.0;
        long   count         = summary != null && summary.getTotalCount()       != null ? summary.getTotalCount()        : 0L;

        double overall = count > 0
                ? Math.round((presentation + communication + collaboration + sincerity + planning) / 5.0 * 10) / 10.0
                : 0.0;

        return new ProfileResponse(
                user.getId(),
                user.getName(),
                user.getDepartment().name(),
                user.getBio() != null ? user.getBio() : "",
                round(presentation),
                round(communication),
                round(collaboration),
                round(sincerity),
                round(planning),
                overall,
                count,
                projectCount
        );
    }

    private static double round(double value) {
        return Math.round(value * 10) / 10.0;
    }
}
