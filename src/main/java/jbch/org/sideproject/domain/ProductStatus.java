package jbch.org.sideproject.domain;

public enum ProductStatus {
    WAITING,    // 승인 대기
    APPROVED,   // 승인됨 (판매중)
    REJECTED,   // 반려됨
    SOLD_OUT    // 품절
}
