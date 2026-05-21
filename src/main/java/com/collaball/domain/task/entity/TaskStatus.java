package com.collaball.domain.task.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TaskStatus {

    TODO("할 일"),
    IN_PROGRESS("진행 중"),
    DONE("완료");

    private final String label;
}
