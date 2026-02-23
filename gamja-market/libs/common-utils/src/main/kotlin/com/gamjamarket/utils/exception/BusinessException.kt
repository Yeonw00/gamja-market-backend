package com.gamjamarket.utils.exception

import com.gamjamarket.utils.response.ResultCode

open class BusinessException(
    val resultCode: ResultCode,
    message: String? = null
) : RuntimeException(message ?: resultCode.defaultMessage)