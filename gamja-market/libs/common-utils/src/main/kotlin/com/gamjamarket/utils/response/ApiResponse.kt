package com.gamjamarket.utils.response

data class ApiResponse<T>(
    val code: String,
    val message: String,
    val data: T?
) {
    companion object {
        // 1. 데이터가 있는 성공 응답
        fun <T> success(data: T, message: String? = null): ApiResponse<T> {
            return ApiResponse(
                code = ResultCode.SUCCESS.code,
                message = message ?: ResultCode.SUCCESS.defaultMessage,
                data = data
            )
        }

        // 2. 데이터가 없는 성공 응답
        fun successWithNoData(message: String? = null): ApiResponse<Nothing> {
            return ApiResponse(
                code = ResultCode.SUCCESS.code,
                message = message ?: ResultCode.SUCCESS.defaultMessage,
                data = null
            )
        }

        // 3. 에러 응답
        fun error(resultCode: ResultCode, message: String? = null): ApiResponse<Nothing> {
            return ApiResponse(
                code = resultCode.code,
                message = message ?: resultCode.defaultMessage,
                data = null
            )
        }
    }
}