package com.collaball.common.config;

import com.collaball.domain.user.entity.Department;
import com.collaball.domain.user.entity.User;
import com.collaball.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final String TEST_EMAIL = "test@test.com";
    private static final String TEST_PASSWORD = "test1234";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail(TEST_EMAIL)) {
            log.info("[DataInitializer] 테스트 계정이 이미 존재합니다: {}", TEST_EMAIL);
            return;
        }

        userRepository.save(User.builder()
                .email(TEST_EMAIL)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .name("테스트유저")
                .department(Department.소프트웨어학부)
                .build());

        log.info("[DataInitializer] 테스트 계정 생성 완료 - email: {}, password: {}", TEST_EMAIL, TEST_PASSWORD);
    }
}
