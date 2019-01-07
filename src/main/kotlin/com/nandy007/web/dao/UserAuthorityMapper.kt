package com.nandy007.web.dao

import org.apache.ibatis.annotations.Param

import com.nandy007.web.model.UserAuthority

interface UserAuthorityMapper {

	fun getByUserId(@Param("userId") userId: Int?): UserAuthority
	fun getByUserName(@Param("userName") userName: String): UserAuthority
}