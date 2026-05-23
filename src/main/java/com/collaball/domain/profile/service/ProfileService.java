package com.collaball.domain.profile.service;

import com.collaball.common.api.code.ErrorCode;
import com.collaball.common.exception.BusinessException;
import com.collaball.domain.evaluation.dto.ScoreSummary;
import com.collaball.domain.evaluation.repository.EvaluationRepository;
import com.collaball.domain.profile.dto.ProfileResponse;
import com.collaball.domain.team.repository.TeamMemberRepository;
import com.collaball.domain.user.entity.User;
import com.collaball.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final EvaluationRepository evaluationRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        ScoreSummary summary = evaluationRepository.findScoreSummaryByRevieweeId(userId);
        long projectCount = teamMemberRepository.countByUserId(userId);

        return ProfileResponse.of(user, summary, projectCount);
    }
}
