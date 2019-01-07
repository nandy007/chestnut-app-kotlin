package com.nandy007.web.controller

import com.nandy007.web.core.Result
import com.nandy007.web.core.ResultGenerator
import com.nandy007.web.model.SessionInfo
import com.nandy007.web.model.User
// import com.nandy007.web.model.UserAuthority;
import com.nandy007.web.secruity.JwtAuthenticationRequest
// import com.nandy007.web.service.UserAuthorityService;
import com.nandy007.web.service.UserService
import com.nandy007.web.utils.DataBaseUtil
// import com.github.pagehelper.PageHelper;
// import com.github.pagehelper.PageInfo;
import com.nandy007.web.utils.SessionUtil

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.apache.commons.dbutils.OutParameter
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import java.sql.Types

import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest// import io.swagger.annotations.ApiImplicitParam;

// import io.swagger.annotations.ApiImplicitParams;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;


/**
 * Created by CodeGenerator on 2017/10/26.
 */
@RestController
@RequestMapping("/auth")
@Api(value = "AuthController", description = "用户授权API")
class AuthController {

    @Value("\${jwt.header}")
    private val tokenHeader: String? = null

    @Resource
    private val authService: UserService? = null

    @ApiOperation(value = "获取token", notes = "用户登录 获取token")
    @RequestMapping(method = arrayOf(RequestMethod.POST), value = "/login")
    fun createAuthenticationToken(@RequestBody authenticationRequest: JwtAuthenticationRequest): Result {
        val token = authService!!.login(authenticationRequest.username!!, authenticationRequest.password!!)
        val sessionInfo = SessionInfo()
        sessionInfo.token = token
        sessionInfo.username = authenticationRequest.username
        SessionUtil.sessionInfo = sessionInfo

        return ResultGenerator.genSuccessResult(token)

    }

    @ApiOperation(value = "刷新token", notes = "刷新token 获取最新token")
    @RequestMapping(method = arrayOf(RequestMethod.POST), value = "/refresh")
    @Throws(AuthenticationException::class)
    fun refreshAndGetAuthenticationToken(
            request: HttpServletRequest): Result {
        val token = request.getHeader(tokenHeader)
        val refreshedToken = authService!!.refresh(token)
        return if (refreshedToken == null) {
            ResultGenerator.genFailResult("refreshedToken is null")
        } else {
            ResultGenerator.genSuccessResult(refreshedToken)
        }
    }

    @ApiOperation(value = "注册用户", notes = "注册返回用户信息")
    @RequestMapping(method = arrayOf(RequestMethod.POST), value = "/register")
    @Throws(AuthenticationException::class)
    fun register(@RequestBody addedUser: User): User? {
        return authService!!.register(addedUser)
    }

}
