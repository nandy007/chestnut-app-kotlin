package com.nandy007.web.model

import com.fasterxml.jackson.annotation.JsonTypeInfo

// 使用redis存储的类使用JsonTypeInfo序列化/反序列化
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
class SessionInfo {
    /**
     * @return token
     */
    /**
     * @param token
     */
    var token: String? = null
    /**
     * @return the username
     */
    /**
     * @param username the username to set
     */
    var username: String? = null
}