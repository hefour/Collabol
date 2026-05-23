package com.collaball.domain.evaluation.dto;

public interface ScoreSummary {
    Double getPresentationAvg();
    Double getCommunicationAvg();
    Double getCollaborationAvg();
    Double getSincerityAvg();
    Double getPlanningAvg();
    Long getTotalCount();
}
