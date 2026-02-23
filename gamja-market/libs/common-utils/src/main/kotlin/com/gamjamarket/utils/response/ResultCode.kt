package com.gamjamarket.utils.response

enum class ResultCode(
    val code: String,
    val defaultMessage: String
) {
    // 정상 상태
    SUCCESS("200", "정상 처리되었습니다."),

    // 클라이언트 에러 (4xx)
    BAD_REQUEST("400", "잘못된 요청입니다."),
    UNAUTHORIZED("401", "인증이 필요합니다."),
    FORBIDDEN("403", "권한이 없습니다."),
    NOT_FOUND("404", "대상을 찾을 수 없습니다."),

    // 서버 에러 (5xx)
    INTERNAL_ERROR("500", "서버 내부 오류가 발생했습니다."),

    // 비즈니스 특화 에러 (예: 입찰 관련)
    BID_LOWER_THAN_HIGHEST("BID-001", "현재 최고가보다 높은 금액을 제시해야 합니다."),
    ITEM_ALREADY_BIDDED("ITEM-001", "이미 입찰이 진행된 상품은 수정/삭제할 수 없습니다.")
}