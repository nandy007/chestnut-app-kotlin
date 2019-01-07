package com.nandy007.web.service.impl

import com.nandy007.web.dao.AuthorityMapper
import com.nandy007.web.model.Authority
import com.nandy007.web.service.AuthorityService
import com.nandy007.web.core.AbstractService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource


/**
 * Created by CodeGenerator on 2017/10/27.
 */
@Service
@Transactional
class AuthorityServiceImpl : AbstractService<Authority>(), AuthorityService {
    @Resource
    private val authorityMapper: AuthorityMapper? = null

}
