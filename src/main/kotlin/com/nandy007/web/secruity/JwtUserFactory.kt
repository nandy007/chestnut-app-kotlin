package com.nandy007.web.secruity

import java.util.stream.Collector
import java.util.stream.Collectors


import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

import com.nandy007.web.model.Authority
import com.nandy007.web.model.UserAuthority

object JwtUserFactory {

    fun create(ua: UserAuthority): JwtUser {
        return JwtUser(
                ua.user!!.id,
                ua.user!!.username!!,
                ua.user!!.password!!,
                mapToGrantedAuthorities(ua.authorityList!!),
                ua.user!!.lastPasswordResetDate
        )
    }

    private fun mapToGrantedAuthorities(authorities: List<Authority>): List<GrantedAuthority> {

        val collector = Collectors.toList<GrantedAuthority>() as
                Collector<in SimpleGrantedAuthority, Any, List<GrantedAuthority>>

        return authorities.stream()
                .map { authority -> SimpleGrantedAuthority(authority.name) }
                .collect<List<GrantedAuthority>, Any>(collector)
    }
}
