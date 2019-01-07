package com.nandy007.web.core

/**
 * 项目常量
 */
object ProjectConstant {
    val BASE_PACKAGE = "com.nandy007.web"//项目基础包名称，根据自己公司的项目修改

    val MODEL_PACKAGE = "$BASE_PACKAGE.model"//Model所在包
    val MAPPER_PACKAGE = "$BASE_PACKAGE.dao"//Mapper所在包
    val SERVICE_PACKAGE = "$BASE_PACKAGE.service"//Service所在包
    val SERVICE_IMPL_PACKAGE = "$SERVICE_PACKAGE.impl"//ServiceImpl所在包
    val CONTROLLER_PACKAGE = "$BASE_PACKAGE.controller"//Controller所在包

    val MAPPER_INTERFACE_REFERENCE = "$BASE_PACKAGE.core.Mapper"//Mapper插件基础接口的完全限定名
}
