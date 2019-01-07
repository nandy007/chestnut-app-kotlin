package com.nandy007.web.secruity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.Date

class JwtUser(
        @get:JsonIgnore
        val id: Int?,
        private val username: String,
        private val password: String,
        private val authorities: Collection<GrantedAuthority>,
        @get:JsonIgnore
        val lastPasswordResetDate: Date?) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    @JsonIgnore
    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return username
    }


    @JsonIgnore
    override fun isAccountNonExpired(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isAccountNonLocked(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isEnabled(): Boolean {
        return true
    }

    companion object {
        /**
         *
         */
        private val serialVersionUID = 1L
    }

}
