package com.nandy007.web.service

import com.nandy007.web.model.User
import com.nandy007.web.core.Service


/**
 * Created by CodeGenerator on 2017/10/26.
 */
interface UserService : Service<User> {

    fun register(userToAdd: User): User?
    fun login(username: String, password: String): String
    fun refresh(oldToken: String): String?
}
