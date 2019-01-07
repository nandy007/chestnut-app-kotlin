package com.nandy007.web.utils

import com.nandy007.web.core.StaticHelper
import com.nandy007.web.model.SessionInfo


import javax.servlet.http.HttpServletRequest

import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes


object SessionUtil {

    /**
     * 获取request
     * @return
     */
    private val httpRequest: HttpServletRequest?
        get() {
            val requestAttributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?
            return requestAttributes?.request
        }


    private val redisUtil: RedisUtil?
        get() = StaticHelper.redisUtil


    private val ttl: Int?
        get() = StaticHelper.sessionTimeout

    private var redisSessionInfo: SessionInfo?
        get() {
            val sessionId = sessionId
            return redisUtil!![sessionId] as SessionInfo?
        }
        set(sessionInfo) {
            if(sessionInfo===null) return
            val sessionId = sessionId
            redisUtil!!.set(sessionId, sessionInfo, ttl!!.toLong())
        }


    val sessionId: String
        get() {

            val token = httpRequest!!.session.getAttribute("authToken") as String

            if (token == null) {
                throwError()
            }

            return createSessionId(token)
        }

    var sessionInfo: SessionInfo?
        get() = redisSessionInfo
        set(sessionInfo) {
            if(sessionInfo!==null) {
                httpRequest!!.session.setAttribute("authToken", sessionInfo.token)
                redisSessionInfo = sessionInfo
            }
        }

    private fun getRedisSessionInfo(sessionId: String): SessionInfo? {
        val sessionInfo = redisUtil!![sessionId]
        return sessionInfo as SessionInfo?
    }


    fun getSessionId(token: String?, request: HttpServletRequest): String {
        if (token == null) {
            throwError()
        }
        request.session.setAttribute("authToken", token)
        return createSessionId(token ?: "")
    }

    private fun createSessionId(token: String): String {
        return StaticHelper.sessionId + ":" + MD5Util.MD5Encode(token)
    }

    fun getSessionInfo(sessionId: String): SessionInfo? {
        return getRedisSessionInfo(sessionId)
    }

    operator fun set(key: String, value: Any) {
        val sessionInfo = redisSessionInfo
        if (sessionInfo == null) {
            throwError()
        }
        ReflexObjectUtil.setValue(sessionInfo!!, key, value)
        redisSessionInfo = sessionInfo
    }

    operator fun get(key: String): Any {
        val sessionInfo = redisSessionInfo
        return ReflexObjectUtil.getValueByKey(sessionInfo!!, key)
    }

    private fun throwError() {
        throw Error("会话为空")
    }


}