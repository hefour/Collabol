package com.collaball.domain.auth.service;

import com.collaball.common.api.code.ErrorCode;
import com.collaball.common.exception.BusinessException;
import com.collaball.common.jwt.JwtProperties;
import com.collaball.common.jwt.JwtProvider;
import com.collaball.domain.auth.dto.LoginRequest;
import com.collaball.domain.auth.dto.LogoutRequest;
import com.collaball.domain.auth.dto.RefreshRequest;
import com.collaball.domain.auth.dto.SendEmailRequest;
import com.collaball.domain.auth.dto.SignupRequest;
import com.collaball.domain.auth.dto.TokenResponse;
import com.collaball.domain.auth.dto.VerifyCodeRequest;
import com.collaball.domain.auth.entity.EmailVerification;
import com.collaball.domain.auth.entity.RefreshToken;
import com.collaball.domain.auth.repository.EmailVerificationRepository;
import com.collaball.domain.auth.repository.RefreshTokenRepository;
import com.collaball.domain.user.entity.User;
import com.collaball.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String SOONGSIL_DOMAIN = "@soongsil.ac.kr";
    private static final int CODE_LENGTH = 6;
    private static final int CODE_EXPIRY_MINUTES = 5;

    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final EmailService emailService;

    @Transactional
    public void sendVerificationEmail(SendEmailRequest request) {
        String email = request.email();

        if (!email.endsWith(SOONGSIL_DOMAIN)) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL_DOMAIN);
        }
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        String code = generateCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES);

        emailVerificationRepository.findByEmail(email).ifPresentOrElse(
                ev -> ev.updateCode(code, expiresAt),
                () -> emailVerificationRepository.save(
                        EmailVerification.builder()
                                .email(email)
                                .code(code)
                                .expiresAt(expiresAt)
                                .build()
                )
        );

        emailService.sendVerificationCode(email, code);
    }

    @Transactional
    public void verifyCode(VerifyCodeRequest request) {
        EmailVerification ev = emailVerificationRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.VERIFICATION_CODE_NOT_FOUND));

        if (LocalDateTime.now().isAfter(ev.getExpiresAt())) {
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_EXPIRED);
        }
        if (!ev.getCode().equals(request.code())) {
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_MISMATCH);
        }

        ev.markVerified();
    }

    @Transactional
    public void signup(SignupRequest request) {
        EmailVerification ev = emailVerificationRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.EMAIL_NOT_VERIFIED));

        if (!ev.isVerified()) {
            throw new BusinessException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        userRepository.save(User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .department(request.department())
                .build());

        emailVerificationRepository.delete(ev);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.LOGIN_FAILED));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(jwtProperties.getRefreshTokenExpiration() / 1000);

        refreshTokenRepository.findByEmail(user.getEmail()).ifPresentOrElse(
                rt -> rt.rotate(refreshToken, expiresAt),
                () -> refreshTokenRepository.save(RefreshToken.builder()
                        .email(user.getEmail())
                        .token(refreshToken)
                        .expiresAt(expiresAt)
                        .build())
        );

        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public TokenResponse refresh(RefreshRequest request) {
        String token = request.refreshToken();

        if (!jwtProvider.isValid(token)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        RefreshToken stored = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (LocalDateTime.now().isAfter(stored.getExpiresAt())) {
            refreshTokenRepository.delete(stored);
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        User user = userRepository.findByEmail(stored.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtProvider.generateAccessToken(user);
        String newRefreshToken = jwtProvider.generateRefreshToken(user.getEmail());
        LocalDateTime newExpiresAt = LocalDateTime.now()
                .plusSeconds(jwtProperties.getRefreshTokenExpiration() / 1000);

        stored.rotate(newRefreshToken, newExpiresAt);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(LogoutRequest request) {
        RefreshToken stored = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

        refreshTokenRepository.delete(stored);
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int number = random.nextInt(900000) + 100000;
        return String.valueOf(number);
    }
}
