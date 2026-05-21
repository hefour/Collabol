package com.collaball.common.api.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {

    // 공통
    OK("COMMON_200", "요청이 성공했습니다."),
    CREATED("COMMON_201", "생성이 완료되었습니다."),

    // 인증
    LOGIN_SUCCESS("AUTH_200_01", "로그인에 성공했습니다."),
    LOGOUT_SUCCESS("AUTH_200_02", "로그아웃에 성공했습니다."),
    TOKEN_REISSUED("AUTH_200_03", "토큰이 재발급되었습니다."),
    SIGNUP_SUCCESS("AUTH_201_01", "회원가입이 완료되었습니다."),
    EMAIL_SENT("AUTH_200_04", "인증 이메일이 발송되었습니다."),
    EMAIL_VERIFIED("AUTH_200_05", "이메일 인증이 완료되었습니다."),

    // 유저
    USER_FOUND("USER_200", "사용자 조회에 성공했습니다."),
    USER_UPDATED("USER_200_02", "사용자 정보가 수정되었습니다."),
    USER_DELETED("USER_200_03", "사용자가 탈퇴되었습니다."),

    // 프로젝트
    PROJECT_CREATED("PROJECT_201", "프로젝트가 생성되었습니다."),
    PROJECT_FOUND("PROJECT_200", "프로젝트 조회에 성공했습니다."),
    PROJECT_UPDATED("PROJECT_200_02", "프로젝트가 수정되었습니다."),
    PROJECT_DELETED("PROJECT_200_03", "프로젝트가 삭제되었습니다."),

    // 팀 멤버 & 초대
    TEAM_MEMBER_ADDED("TEAM_201", "팀 멤버가 추가되었습니다."),
    TEAM_MEMBER_FOUND("TEAM_200", "팀 멤버 조회에 성공했습니다."),
    TEAM_MEMBER_ROLE_UPDATED("TEAM_200_02", "역할이 변경되었습니다."),
    TEAM_MEMBER_REMOVED("TEAM_200_03", "팀 멤버가 제거되었습니다."),
    INVITE_CODE_FOUND("TEAM_200_04", "초대 코드 조회에 성공했습니다."),
    INVITE_CODE_REGENERATED("TEAM_200_05", "초대 코드가 재생성되었습니다."),
    PROJECT_JOINED("TEAM_201_02", "프로젝트에 참여했습니다."),

    // 태스크
    TASK_CREATED("TASK_201", "태스크가 생성되었습니다."),
    TASK_FOUND("TASK_200", "태스크 조회에 성공했습니다."),
    TASK_UPDATED("TASK_200_02", "태스크가 수정되었습니다."),
    TASK_DELETED("TASK_200_03", "태스크가 삭제되었습니다."),

    // 평가
    EVALUATION_CREATED("EVAL_201", "평가가 완료되었습니다."),
    EVALUATION_FOUND("EVAL_200", "평가 조회에 성공했습니다."),
    EVALUATION_UPDATED("EVAL_200_02", "평가가 수정되었습니다."),
    EVALUATION_DELETED("EVAL_200_03", "평가가 삭제되었습니다."),

    // 프로필
    PROFILE_FOUND("PROFILE_200", "프로필 조회에 성공했습니다."),
    PROFILE_UPDATED("PROFILE_200_02", "프로필이 수정되었습니다.");

    private final String code;
    private final String message;
}
