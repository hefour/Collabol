package com.collaball.domain.user.controller;

import com.collaball.common.api.code.ResponseCode;
import com.collaball.common.api.response.ApiResponse;
import com.collaball.domain.user.dto.UpdateBioRequest;
import com.collaball.domain.user.dto.UserResponse;
import com.collaball.domain.user.entity.User;
import com.collaball.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(@AuthenticationPrincipal User currentUser) {
        return ApiResponse.ok(ResponseCode.USER_FOUND, UserResponse.from(currentUser));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMe(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody UpdateBioRequest request
    ) {
        return ApiResponse.ok(ResponseCode.USER_UPDATED, userService.updateBio(currentUser, request));
    }
}
