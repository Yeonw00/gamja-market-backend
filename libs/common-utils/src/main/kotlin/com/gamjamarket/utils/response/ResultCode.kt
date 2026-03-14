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

    // 비즈니스 특화 에러 — 경매
    AUCTION_NOT_FOUND("AUCTION-001", "경매를 찾을 수 없습니다."),
    AUCTION_ALREADY_ENDED("AUCTION-002", "이미 종료된 경매입니다."),
    AUCTION_CANNOT_DELETE_NEAR_END("AUCTION-003", "경매 마감 1시간 전부터는 상품을 삭제할 수 없습니다."),
    AUCTION_INVALID_START_TIME("AUCTION-004", "경매 시작 시간은 현재 시간 이후여야 합니다."),
    AUCTION_START_TIME_TOO_FAR("AUCTION-005", "경매 시작 시간은 등록일로부터 최대 일주일 이내로만 설정할 수 있습니다."),
    AUCTION_DURATION_TOO_SHORT("AUCTION-006", "경매는 시작 시간으로부터 최소 1시간 이상 진행되어야 합니다."),
    AUCTION_DURATION_TOO_LONG("AUCTION-007", "경매는 시작 시간으로부터 최대 5일까지만 진행할 수 있습니다."),
    AUCTION_ALREADY_ENDED_CANNOT_MODIFY("AUCTION-008", "이미 종료된 경매는 수정할 수 없습니다."),
    AUCTION_NO_INFO("AUCTION-009", "경매 정보가 없는 상품입니다."),

    // 비즈니스 특화 에러 — 입찰
    BID_LOWER_THAN_HIGHEST("BID-001", "현재 최고가보다 높은 금액을 제시해야 합니다."),
    BID_LOWER_THAN_START_PRICE("BID-002", "입찰 금액은 시작가 이상이어야 합니다."),
    BID_OWN_ITEM("BID-003", "자신의 상품에는 입찰할 수 없습니다."),

    // 비즈니스 특화 에러 — 상품
    ITEM_NOT_FOUND("ITEM-001", "상품을 찾을 수 없습니다."),
    ITEM_ALREADY_BIDDED("ITEM-002", "이미 입찰이 진행된 상품은 수정/삭제할 수 없습니다."),
    ITEM_NO_PERMISSION("ITEM-003", "해당 상품에 대한 권한이 없습니다."),

    // 비즈니스 특화 에러 — 사용자/카테고리
    USER_NOT_FOUND("USER-001", "사용자를 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND("CATEGORY-001", "존재하지 않는 카테고리입니다.")
}