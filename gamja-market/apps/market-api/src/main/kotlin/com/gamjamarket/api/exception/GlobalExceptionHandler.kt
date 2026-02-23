package com.gamjamarket.api.exception

import com.gamjamarket.utils.exception.BusinessException
import com.gamjamarket.utils.response.ApiResponse
import com.gamjamarket.utils.response.ResultCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    // 1. 직접 만든 BusinessException을 가로채는 곳
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ApiResponse<Nothing>> {
        val response = ApiResponse.error(e.resultCode, e.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    // 2. 기본 예외 처리
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ApiResponse<Nothing>> {
        val response = ApiResponse.error(ResultCode.BAD_REQUEST, e.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    // 3. 그 외에 예상치 못한 모든 서버 에러
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        e.printStackTrace()
        val response = ApiResponse.error(ResultCode.INTERNAL_ERROR)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }
}