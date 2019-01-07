package com.nandy007.web.configurer

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.BeanIds
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

import com.nandy007.web.secruity.JwtAuthenticationEntryPoint
import com.nandy007.web.secruity.JwtAuthenticationTokenFilter


// @SuppressWarnings(WebSecurityConfig.SPRING_JAVA_AUTOWIRING_INSPECTION)
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private val unauthorizedHandler: JwtAuthenticationEntryPoint? = null

    @Autowired
    private val userDetailsService: UserDetailsService? = null

    @Autowired
    @Throws(Exception::class)
    fun configureAuthentication(authenticationManagerBuilder: AuthenticationManagerBuilder) {
        authenticationManagerBuilder
                .userDetailsService<UserDetailsService>(this.userDetailsService)
                .passwordEncoder(passwordEncoder())
    }


    // 解决无法@Autowired AuthenticationManager的问题
    // 参考https://liuyanzhao.com/7852.html
    @Bean(name = arrayOf(BeanIds.AUTHENTICATION_MANAGER))
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationTokenFilterBean(): JwtAuthenticationTokenFilter {
        return JwtAuthenticationTokenFilter()
    }

    @Throws(Exception::class)
    override fun configure(httpSecurity: HttpSecurity) {
        httpSecurity
                // 由于使用的是JWT，我们这里不需要csrf
                .csrf().disable()

                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()

                // 基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                .authorizeRequests()
                //.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 允许对于网站静态资源的无授权访问
                .antMatchers(
                        HttpMethod.GET,
                        "/",
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js"
                ).permitAll()
                .antMatchers(
                        "/v2/api-docs",
                        "/configuration/ui",
                        "/swagger-resources",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**"
                ).permitAll()
                // 对于获取token的rest api要允许匿名访问
                .antMatchers("/auth/**").permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated()

        // 添加JWT filter
        httpSecurity
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter::class.java)

        // 禁用缓存
        httpSecurity.headers().cacheControl()
    }

    companion object {

        /**
         *
         */

        internal val SPRING_JAVA_AUTOWIRING_INSPECTION = "SpringJavaAutowiringInspection"
    }
}