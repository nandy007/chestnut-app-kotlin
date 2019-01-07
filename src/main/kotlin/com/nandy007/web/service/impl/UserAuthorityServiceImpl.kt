package com.nandy007.web.service.impl

import com.nandy007.web.dao.UserAuthorityMapper
import com.nandy007.web.model.UserAuthority
import com.nandy007.web.service.UserAuthorityService
// import com.nandy007.web.core.AbstractService;
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource


/**
 * Created by CodeGenerator on 2017/10/27.
 */
@Service
@Transactional
class UserAuthorityServiceImpl : UserAuthorityService {
    @Resource
    private val userAuthorityMapper: UserAuthorityMapper? = null

    override fun getByUserId(userId: Int?): UserAuthority {
        return userAuthorityMapper!!.getByUserId(userId)
    }
}
