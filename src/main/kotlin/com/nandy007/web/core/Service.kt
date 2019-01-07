package com.nandy007.web.core

import org.apache.ibatis.exceptions.TooManyResultsException
import tk.mybatis.mapper.entity.Condition

/**
 * Service 层 基础接口，其他Service 接口 请继承该接口
 */
interface Service<T> {
    fun save(model: T) //持久化
    fun save(models: List<T>) //批量持久化
    fun deleteById(id: Int?) //通过主鍵刪除
    fun deleteByIds(ids: String) //批量刪除 eg：ids -> “1,2,3,4”
    fun update(model: T) //更新
    fun findById(id: Int?): T //通过ID查找
    @Throws(TooManyResultsException::class)
    fun findBy(fieldName: String, value: Any): T  //通过Model中某个成员变量名称（非数据表中column的名称）查找,value需符合unique约束

    fun findByIds(ids: String): List<T> //通过多个ID查找//eg：ids -> “1,2,3,4”
    fun findByCondition(condition: Condition): List<T> //根据条件查找
    fun findAll(): List<T> //获取所有
}
