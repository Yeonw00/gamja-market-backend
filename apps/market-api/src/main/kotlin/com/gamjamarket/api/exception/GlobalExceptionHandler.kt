package com.gamjamarket.api.exception

import com.gamjamarket.utils.exception.BusinessException
import com.gamjamarket.utils.response.ApiResponse
import com.gamjamarket.utils.response.ResultCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(this::class.java)

    // 1. BusinessException — ResultCode에 따라 HTTP 상태코드 매핑
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ApiResponse<Nothing>> {
        val response = ApiResponse.error(e.resultCode, e.message)
        val status = when (e.resultCode) {
            ResultCode.NOT_FOUND -> HttpStatus.NOT_FOUND
            ResultCode.UNAUTHORIZED -> HttpStatus.UNAUTHORIZED
            ResultCode.FORBIDDEN -> HttpStatus.FORBIDDEN
            ResultCode.INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR
            else -> HttpStatus.BAD_REQUEST
        }
        return ResponseEntity.status(status).body(response)
    }

    // 3. 그 외에 예상치 못한 모든 서버 에러
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        log.error("예상치 못한 서버 오류 발생", e)
        val response = ApiResponse.error(ResultCode.INTERNAL_ERROR)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }
}