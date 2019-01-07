package com.nandy007.web.controller

// import org.springframework.http.ResponseEntity;
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import com.nandy007.web.core.Result
import com.nandy007.web.core.ResultGenerator

@RestController
@RequestMapping("/protected")
@Api(value = "MethodProtectedRestController", description = "需要ADMIN权限才可以访问")
class MethodProtectedRestController {

    /**
     * 在 @PreAuthorize 中我们可以利用内建的 SPEL 表达式：比如 'hasRole()' 来决定哪些用户有权访问。
     * 需注意的一点是 hasRole 表达式认为每个角色名字前都有一个前缀 'ROLE_'。所以这里的 'ADMIN' 其实在
     * 数据库中存储的是 'ROLE_ADMIN' 。这个 @PreAuthorize 可以修饰Controller也可修饰Controller中的方法。
     */
    //表示这个资源只能被拥有 ADMIN 角色的用户访问
    val protectedGreeting: Result
        @RequestMapping(method = arrayOf(RequestMethod.GET))
        @PreAuthorize("hasRole('ADMIN')")
        @ApiOperation(value = "测试权限")
        get() = ResultGenerator.genSuccessResult("Greetings from admin protected method!")

}