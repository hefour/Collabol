package com.collaball.domain.user.dto;

import com.collaball.domain.user.entity.User;

public record UserResponse(Long id, String name, String email, String department, String bio) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getDepartment().name(),
                user.getBio() != null ? user.getBio() : ""
        );
    }
}
