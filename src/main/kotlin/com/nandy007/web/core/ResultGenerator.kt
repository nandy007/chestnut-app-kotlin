package com.nandy007.web.core

/**
 * 响应结果生成工具
 */
object ResultGenerator {
    private val DEFAULT_SUCCESS_MESSAGE = "SUCCESS"

    fun genSuccessResult(): Result {
        return Result()
                .setCode(ResultCode.SUCCESS)
                .setMessage(DEFAULT_SUCCESS_MESSAGE)
    }

    fun genSuccessResult(data: Any): Result {
        return Result()
                .setCode(ResultCode.SUCCESS)
                .setMessage(DEFAULT_SUCCESS_MESSAGE)
                .setData(data)
    }

    fun genFailResult(message: String): Result {
        return Result()
                .setCode(ResultCode.FAIL)
                .setMessage(message)
    }
}
