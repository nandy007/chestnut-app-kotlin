package com.nandy007.web.service.impl

import com.nandy007.web.dao.UserAuthorityMapper
import com.nandy007.web.dao.UserMapper
import com.nandy007.web.model.User
import com.nandy007.web.secruity.JwtTokenUtil
import com.nandy007.web.secruity.JwtUser
import com.nandy007.web.service.UserService
import com.nandy007.web.core.AbstractService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.Date

import javax.annotation.Resource


/**
 * Created by CodeGenerator on 2017/10/26.
 */
@Service
@Transactional
class UserServiceImpl : AbstractService<User>(), UserService {

    @Autowired
    private val authenticationManager: AuthenticationManager? = null

    @Autowired
    private val userDetailsService: UserDetailsService? = null

    @Autowired
    private val jwtTokenUtil: JwtTokenUtil? = null

    @Resource
    private val userAuthorityMapper: UserAuthorityMapper? = null
    @Resource
    private val userMapper: UserMapper? = null

    @Value("\${jwt.tokenHead}")
    private val tokenHead: String? = null

    override fun register(userToAdd: User): User? {
        val username = userToAdd.username
        val filterUser = User()
        filterUser.username = username
        if (userMapper!!.selectOne(filterUser) != null) {
            return null
        }
        val encoder = BCryptPasswordEncoder()
        val rawPassword = userToAdd.password
        userToAdd.password = encoder.encode(rawPassword!!)
        userToAdd.registerDate = Date()
        userToAdd.lastPasswordResetDate = Date()
        //userToAdd.setRoles(asList("ROLE_USER"));
        // todo : 添加 角色表 ，添加 用户角色关系表
        userMapper.insert(userToAdd)



        return userToAdd

    }

    override fun login(username: String, password: String): String {

        val upToken = UsernamePasswordAuthenticationToken(username, password)
        // Perform the security
        val authentication = authenticationManager!!.authenticate(upToken)
        SecurityContextHolder.getContext().authentication = authentication

        // Reload password post-security so we can generate token
        val userDetails = userDetailsService!!.loadUserByUsername(username)
        return jwtTokenUtil!!.generateToken(userDetails)
    }

    override fun refresh(oldToken: String): String? {
        val token = oldToken.substring(tokenHead!!.length - 2)
        val username = jwtTokenUtil!!.getUsernameFromToken(token)
        val user = userDetailsService!!.loadUserByUsername(username) as JwtUser
        return if (jwtTokenUtil.canTokenBeRefreshed(token, user.lastPasswordResetDate)!!) {
            jwtTokenUtil.refreshToken(token)
        } else null
    }
}
