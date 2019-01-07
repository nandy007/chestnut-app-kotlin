package com.nandy007.web.service

import com.nandy007.web.model.UserAuthority

// import org.apache.ibatis.annotations.Param;


/**
 * Created by CodeGenerator on 2017/10/27.
 */
interface UserAuthorityService {
	fun getByUserId(userId: Int?): UserAuthority
}
