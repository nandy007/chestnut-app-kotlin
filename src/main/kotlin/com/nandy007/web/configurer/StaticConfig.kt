package com.nandy007.web.configurer

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import com.nandy007.web.core.StaticHelper

@Configuration
class StaticConfig {

    @Value("\${server.port}")
    private val serverPort: String? = null


    @Value("\${session.timeout}")
    private val sessionTimeout: Int? = null

    @Value("\${session.id}")
    private val sessionId: String? = null


    @Bean
    fun initStatic(): Int {
        StaticHelper.serverPort = serverPort
        StaticHelper.sessionTimeout = sessionTimeout
        StaticHelper.sessionId = sessionId
        return 0
    }
}