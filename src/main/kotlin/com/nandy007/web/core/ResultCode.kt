package com.nandy007.web.core

/**
 * 响应码枚举，参考HTTP状态码的语义
 */
enum class ResultCode private constructor(//服务器内部错误

        var code: Int) {
    SUCCESS(200), //成功
    FAIL(400), //失败
    UNAUTHORIZED(401), //未认证（签名错误）
    NOT_FOUND(404), //接口不存在
    INTERNAL_SERVER_ERROR(500)
}
