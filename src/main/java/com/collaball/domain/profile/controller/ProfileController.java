package com.collaball.domain.profile.controller;

import com.collaball.common.api.code.ResponseCode;
import com.collaball.common.api.response.ApiResponse;
import com.collaball.domain.profile.dto.ProfileResponse;
import com.collaball.domain.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(@PathVariable Long userId) {
        return ApiResponse.ok(ResponseCode.PROFILE_FOUND, profileService.getProfile(userId));
    }
}
