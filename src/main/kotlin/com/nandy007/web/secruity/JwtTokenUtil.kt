package com.nandy007.web.secruity

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

import java.io.Serializable
import java.util.Date
import java.util.HashMap

@Component
class JwtTokenUtil : Serializable {

    @Value("\${jwt.secret}")
    private val secret: String? = null

    @Value("\${jwt.expiration}")
    private val expiration: Long? = null

    fun getUsernameFromToken(token: String): String? {
        var username: String?
        try {
            val claims = getClaimsFromToken(token)
            val s = claims!!.subject
            val json = JSON.parseObject(s)
            username = json.getString("username")
        } catch (e: Exception) {
            username = null
        }

        return username
    }

    fun getCreatedDateFromToken(token: String): Date? {
        var created: Date?
        try {
            val claims = getClaimsFromToken(token)
            created = Date(claims!![CLAIM_KEY_CREATED] as Long)
        } catch (e: Exception) {
            created = null
        }

        return created
    }

    fun getExpirationDateFromToken(token: String): Date? {
        var expiration: Date?
        try {
            val claims = getClaimsFromToken(token)
            expiration = claims!!.expiration
        } catch (e: Exception) {
            expiration = null
        }

        return expiration
    }

    private fun getClaimsFromToken(token: String): Claims? {
        var claims: Claims?
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .body
        } catch (e: Exception) {
            claims = null
        }

        return claims
    }

    private fun generateExpirationDate(): Date {
        return Date(System.currentTimeMillis() + expiration!! * 1000)
    }

    private fun isTokenExpired(token: String): Boolean {
        val expiration = getExpirationDateFromToken(token)
        return expiration!!.before(Date())
    }

    private fun isCreatedBeforeLastPasswordReset(created: Date?, lastPasswordReset: Date?): Boolean {
        return lastPasswordReset != null && created!!.before(lastPasswordReset)
    }

    fun generateToken(userDetails: UserDetails): String {
        val claims = HashMap<String, Any>()
        val json = JSONObject()
        json["username"] = userDetails.username
        claims[CLAIM_KEY_USERNAME] = json.toJSONString()
        claims[CLAIM_KEY_CREATED] = Date()
        return generateToken(claims)
    }

    internal fun generateToken(claims: Map<String, Any>): String {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact()
    }

    fun canTokenBeRefreshed(token: String, lastPasswordReset: Date?): Boolean? {
        val created = getCreatedDateFromToken(token)
        return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset) && !isTokenExpired(token)
    }

    fun refreshToken(token: String): String? {
        var refreshedToken: String?
        try {
            val claims = getClaimsFromToken(token)
            claims!![CLAIM_KEY_CREATED] = Date()
            refreshedToken = generateToken(claims)
        } catch (e: Exception) {
            refreshedToken = null
        }

        return refreshedToken
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean? {
        val user = userDetails as JwtUser
        val username = getUsernameFromToken(token)
        val created = getCreatedDateFromToken(token)
        //final Date expiration = getExpirationDateFromToken(token);
        return (username == user.username
                && !isTokenExpired(token)
                && !isCreatedBeforeLastPasswordReset(created, user.lastPasswordResetDate))
    }

    companion object {
        /**
         *
         */
        private const val serialVersionUID = 1L
        private val CLAIM_KEY_USERNAME = "sub"
        private val CLAIM_KEY_CREATED = "created"
    }
}
