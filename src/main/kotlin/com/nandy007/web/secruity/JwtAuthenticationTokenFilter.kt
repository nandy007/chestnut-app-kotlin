package com.nandy007.web.secruity

import java.io.IOException

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import io.jsonwebtoken.ExpiredJwtException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter

import lombok.extern.slf4j.Slf4j

import com.nandy007.web.model.SessionInfo
import com.nandy007.web.utils.SessionUtil


@Slf4j
class JwtAuthenticationTokenFilter : OncePerRequestFilter() {

    @Autowired
    private val userDetailsService: UserDetailsService? = null

    @Autowired
    private val jwtTokenUtil: JwtTokenUtil? = null

    @Value("\${jwt.header}")
    private val tokenHeader: String? = null

    @Value("\${session.use}")
    private val useSession: Boolean? = null


    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val requestHeader = request.getHeader(this.tokenHeader)

        var username: String? = null
        var authToken: String? = null
        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            authToken = requestHeader.substring(7)

            if (useSession!!) {
                val sessionInfo = getSessionInfo(request, authToken)
                if (sessionInfo != null) {
                    setAuthentication(request, sessionInfo.username, null)
                    chain.doFilter(request, response)
                    return
                }
            }

            try {
                username = jwtTokenUtil!!.getUsernameFromToken(authToken)
            } catch (e: IllegalArgumentException) {
                logger.error("an error occured during getting username from token", e)
            } catch (e: ExpiredJwtException) {
                logger.warn("the token is expired and not valid anymore", e)
            }

        } else {
            logger.warn("couldn't find bearer string, will ignore the header")
        }


        setAuthentication(request, username, authToken)

        chain.doFilter(request, response)
    }

    private fun setAuthentication(request: HttpServletRequest, username: String?, authToken: String?) {
        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            logger.info("checking authentication for user " + username!!)
            // It is not compelling necessary to load the use details from the database. You could also store the information
            // in the token and read it from it. It's up to you ;)
            val userDetails = this.userDetailsService!!.loadUserByUsername(username)

            // For simple validation it is completely sufficient to just check the token integrity. You don't have to call
            // the database compellingly. Again it's up to you ;)
            var isValid = true
            if (authToken != null) {
                isValid = jwtTokenUtil!!.validateToken(authToken, userDetails)!!
            }
            if (isValid) {
                val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                logger.info("authenticated user $username, setting security context")
                SecurityContextHolder.getContext().authentication = authentication
            }
        }
    }

    private fun getSessionInfo(request: HttpServletRequest, token: String): SessionInfo? {
        val sessionId = SessionUtil.getSessionId(token, request)
        return SessionUtil.getSessionInfo(sessionId)
    }
}