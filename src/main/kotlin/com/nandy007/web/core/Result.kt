package com.nandy007.web.core

import com.alibaba.fastjson.JSON

/**
 * 统一API响应结果封装
 */
class Result {
    private var code: Int = 0
    private var message: String? = null
    private var data: Any? = null

    fun setCode(resultCode: ResultCode): Result {
        this.code = resultCode.code
        return this
    }

    fun getCode(): Int {
        return code
    }

    fun setCode(code: Int): Result {
        this.code = code
        return this
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String): Result {
        this.message = message
        return this
    }

    fun getData(): Any? {
        return data
    }

    fun setData(data: Any): Result {
        this.data = data
        return this
    }

    override fun toString(): String {
        return JSON.toJSONString(this)
    }
}
