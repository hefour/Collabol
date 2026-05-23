package com.collaball.domain.auth.controller;

import com.collaball.common.api.code.ResponseCode;
import com.collaball.common.api.response.ApiResponse;
import com.collaball.domain.auth.dto.LoginRequest;
import com.collaball.domain.auth.dto.LogoutRequest;
import com.collaball.domain.auth.dto.RefreshRequest;
import com.collaball.domain.auth.dto.SendEmailRequest;
import com.collaball.domain.auth.dto.SignupRequest;
import com.collaball.domain.auth.dto.TokenResponse;
import com.collaball.domain.auth.dto.VerifyCodeRequest;
import com.collaball.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/email/send")
    public ResponseEntity<ApiResponse<Void>> sendEmail(@Valid @RequestBody SendEmailRequest request) {
        authService.sendVerificationEmail(request);
        return ApiResponse.ok(ResponseCode.EMAIL_SENT);
    }

    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@Valid @RequestBody VerifyCodeRequest request) {
        authService.verifyCode(request);
        return ApiResponse.ok(ResponseCode.EMAIL_VERIFIED);
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        return ApiResponse.created(ResponseCode.SIGNUP_SUCCESS);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse tokens = authService.login(request);
        return ApiResponse.ok(ResponseCode.LOGIN_SUCCESS, tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        TokenResponse tokens = authService.refresh(request);
        return ApiResponse.ok(ResponseCode.TOKEN_REISSUED, tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
        return ApiResponse.ok(ResponseCode.LOGOUT_SUCCESS);
    }
}
