package com.collaball.domain.evaluation.controller;

import com.collaball.common.api.code.ResponseCode;
import com.collaball.common.api.response.ApiResponse;
import com.collaball.domain.evaluation.dto.EvaluationCreateRequest;
import com.collaball.domain.evaluation.dto.EvaluationResponse;
import com.collaball.domain.evaluation.dto.EvaluationUpdateRequest;
import com.collaball.domain.evaluation.service.EvaluationService;
import com.collaball.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/evaluations")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping
    public ResponseEntity<ApiResponse<EvaluationResponse>> createEvaluation(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long projectId,
            @Valid @RequestBody EvaluationCreateRequest request
    ) {
        EvaluationResponse response = evaluationService.createEvaluation(projectId, request, currentUser);
        return ApiResponse.created(ResponseCode.EVALUATION_CREATED, response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EvaluationResponse>>> getMyEvaluations(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long projectId
    ) {
        List<EvaluationResponse> response = evaluationService.getMyEvaluations(projectId, currentUser);
        return ApiResponse.ok(ResponseCode.EVALUATION_FOUND, response);
    }

    @GetMapping("/received")
    public ResponseEntity<ApiResponse<List<EvaluationResponse>>> getReceivedEvaluations(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long projectId
    ) {
        List<EvaluationResponse> response = evaluationService.getReceivedEvaluations(projectId, currentUser);
        return ApiResponse.ok(ResponseCode.EVALUATION_FOUND, response);
    }

    @PutMapping("/{evaluationId}")
    public ResponseEntity<ApiResponse<EvaluationResponse>> updateEvaluation(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long projectId,
            @PathVariable Long evaluationId,
            @Valid @RequestBody EvaluationUpdateRequest request
    ) {
        EvaluationResponse response = evaluationService.updateEvaluation(projectId, evaluationId, request, currentUser);
        return ApiResponse.ok(ResponseCode.EVALUATION_UPDATED, response);
    }

    @DeleteMapping("/{evaluationId}")
    public ResponseEntity<ApiResponse<Void>> deleteEvaluation(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long projectId,
            @PathVariable Long evaluationId
    ) {
        evaluationService.deleteEvaluation(projectId, evaluationId, currentUser);
        return ApiResponse.ok(ResponseCode.EVALUATION_DELETED);
    }
}
