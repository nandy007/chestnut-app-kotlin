package com.nandy007.web.controller

import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import com.nandy007.web.core.Result
import com.nandy007.web.core.ResultGenerator
import com.nandy007.web.model.User
// import com.nandy007.web.model.UserAuthority;
import com.nandy007.web.service.UserAuthorityService
import com.nandy007.web.service.UserService
import com.nandy007.web.utils.SessionUtil

import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

import javax.annotation.Resource

/**
 * Created by CodeGenerator on 2017/10/26.
 */
@RestController
@RequestMapping("/user")
@Api(value = "UserController", description = "用户相关API")
class UserController {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Resource
    private val userService: UserService? = null

    @Resource
    private val userAuthorityService: UserAuthorityService? = null


    @PostMapping
    @ApiOperation(value = "创建用户", notes = "根据User对象创建用户")
    @ApiImplicitParam(name = "user", value = "用户详细实体user", required = true, dataType = "User")
    fun add(@RequestBody user: User): Result {
        userService!!.save(user)
        return ResultGenerator.genSuccessResult()
    }


    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除用户", notes = "根据url的id来指定删除对象")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long")
    fun delete(@PathVariable id: Int?): Result {
        userService!!.deleteById(id)
        return ResultGenerator.genSuccessResult()
    }

    @PutMapping
    @ApiOperation(value = "更新用户详细信息", notes = "根据url的id来指定更新对象，并根据传过来的user信息来更新用户详细信息")
    @ApiImplicitParams(ApiImplicitParam(name = "id", value = "用户ID", paramType = "path", required = true, dataType = "Long"), ApiImplicitParam(name = "user", value = "用户详细实体user", paramType = "body", required = true, dataType = "User"))
    fun update(@RequestBody user: User): Result {
        userService!!.update(user)
        return ResultGenerator.genSuccessResult()
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "获取用户详细信息", notes = "根据url的id来获取用户详细信息")
    fun detail(@PathVariable id: Int?): Result {
        val user = userService!!.findById(id)
        return ResultGenerator.genSuccessResult(user)
    }

    @ApiOperation(value = "获取用户列表", notes = "")
    @GetMapping
    // public Result list(@RequestAttribute String username, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
    fun list(@RequestParam(defaultValue = "0") page: Int?, @RequestParam(defaultValue = "0") size: Int?): Result {
        logger.info("sessionId:" + SessionUtil["token"])
        PageHelper.startPage<Any>(page!!, size!!)
        val list = userService!!.findAll()
        val pageInfo = PageInfo(list)
        return ResultGenerator.genSuccessResult(pageInfo)
    }
}
