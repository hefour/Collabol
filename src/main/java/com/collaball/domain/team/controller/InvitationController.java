package com.collaball.domain.team.controller;

import com.collaball.common.api.code.ResponseCode;
import com.collaball.common.api.response.ApiResponse;
import com.collaball.domain.team.dto.TeamMemberResponse;
import com.collaball.domain.team.service.TeamMemberService;
import com.collaball.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final TeamMemberService teamMemberService;

    @PostMapping("/join/{inviteCode}")
    public ResponseEntity<ApiResponse<TeamMemberResponse>> join(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String inviteCode
    ) {
        TeamMemberResponse response = teamMemberService.joinByInviteCode(inviteCode, currentUser);
        return ApiResponse.created(ResponseCode.PROJECT_JOINED, response);
    }
}
