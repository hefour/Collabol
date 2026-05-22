package com.collaball.domain.evaluation.dto;

import jakarta.validation.constraints.*;

public record EvaluationCreateRequest(
        @NotNull(message = "평가 대상자 ID는 필수입니다.")
        Long revieweeId,

        @NotNull(message = "발표력 점수는 필수입니다.")
        @Min(value = 1, message = "점수는 1점 이상이어야 합니다.")
        @Max(value = 5, message = "점수는 5점 이하여야 합니다.")
        Integer presentationScore,

        @NotNull(message = "커뮤니케이션 점수는 필수입니다.")
        @Min(value = 1, message = "점수는 1점 이상이어야 합니다.")
        @Max(value = 5, message = "점수는 5점 이하여야 합니다.")
        Integer communicationScore,

        @NotNull(message = "협업태도 점수는 필수입니다.")
        @Min(value = 1, message = "점수는 1점 이상이어야 합니다.")
        @Max(value = 5, message = "점수는 5점 이하여야 합니다.")
        Integer collaborationScore,

        @NotNull(message = "성실성 점수는 필수입니다.")
        @Min(value = 1, message = "점수는 1점 이상이어야 합니다.")
        @Max(value = 5, message = "점수는 5점 이하여야 합니다.")
        Integer sincerityScore,

        @NotNull(message = "기획력 점수는 필수입니다.")
        @Min(value = 1, message = "점수는 1점 이상이어야 합니다.")
        @Max(value = 5, message = "점수는 5점 이하여야 합니다.")
        Integer planningScore,

        @Size(max = 1000, message = "코멘트는 1000자 이하여야 합니다.")
        String comment
) {
}
