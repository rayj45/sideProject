package jbch.org.sideproject.domain;

public enum ReservationStatus {
    REQUESTED,  // 신청됨
    APPROVED,   // 승인됨
    REJECTED,   // 반려됨
    COMPLETED,  // 사용완료
    CANCELED    // 취소됨
}
