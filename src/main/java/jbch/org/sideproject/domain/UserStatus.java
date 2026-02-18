package jbch.org.sideproject.domain;

public enum UserStatus {
    ACTIVE,     // 활성 (로그인 가능)
    PENDING,    // 신청 (승인 대기, 로그인 불가)
    DORMANT,    // 휴면
    DELETED,    // 탈퇴
    BANNED      // 차단
}
