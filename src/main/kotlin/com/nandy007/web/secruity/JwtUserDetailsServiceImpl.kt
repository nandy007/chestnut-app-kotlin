package com.nandy007.web.secruity

import javax.annotation.Resource

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

import com.nandy007.web.dao.UserAuthorityMapper
import com.nandy007.web.model.UserAuthority

@Service
class JwtUserDetailsServiceImpl : UserDetailsService {
    @Resource
    private val userAuthorityMapper: UserAuthorityMapper? = null

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(userName: String): UserDetails {
        val userAuthority = userAuthorityMapper!!.getByUserName(userName)
        return if (userAuthority.user == null) {
            throw UsernameNotFoundException(String.format("No user found with username '%s'.", userName))
        } else {
            JwtUserFactory.create(userAuthority)
        }
    }
}
