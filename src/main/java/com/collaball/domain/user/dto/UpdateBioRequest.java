package com.collaball.domain.user.dto;

import jakarta.validation.constraints.Size;

public record UpdateBioRequest(
        @Size(max = 500, message = "자기소개는 500자 이내로 입력해주세요.")
        String bio
) {}
