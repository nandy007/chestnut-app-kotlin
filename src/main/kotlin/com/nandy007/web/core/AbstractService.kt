package com.nandy007.web.core


import org.apache.ibatis.exceptions.TooManyResultsException
import org.springframework.beans.factory.annotation.Autowired
import tk.mybatis.mapper.entity.Condition

import java.lang.reflect.ParameterizedType

/**
 * 基于通用MyBatis Mapper插件的Service接口的实现
 */
abstract class AbstractService<T> : Service<T> {

    @Autowired
    protected var mapper: Mapper<T>? = null

    private val modelClass: Class<T>    // 当前泛型真实类型的Class

    init {
        val pt = this.javaClass.genericSuperclass as ParameterizedType
        val class1 = pt.actualTypeArguments[0] as Class<T>
        modelClass = class1
    }

    override fun save(model: T) {
        mapper!!.insertSelective(model)
    }

    override fun save(models: List<T>) {
        mapper!!.insertList(models)
    }

    override fun deleteById(id: Int?) {
        mapper!!.deleteByPrimaryKey(id)
    }

    override fun deleteByIds(ids: String) {
        mapper!!.deleteByIds(ids)
    }

    override fun update(model: T) {
        mapper!!.updateByPrimaryKeySelective(model)
    }

    override fun findById(id: Int?): T {
        return mapper!!.selectByPrimaryKey(id)
    }

    @Throws(TooManyResultsException::class)
    override fun findBy(fieldName: String, value: Any): T {
        try {
            val model = modelClass.newInstance()
            val field = modelClass.getDeclaredField(fieldName)
            field.isAccessible = true
            field.set(model, value)
            return mapper!!.selectOne(model)
        } catch (e: ReflectiveOperationException) {
            throw ServiceException(e.message, e)
        }

    }

    override fun findByIds(ids: String): List<T> {
        return mapper!!.selectByIds(ids)
    }

    override fun findByCondition(condition: Condition): List<T> {
        return mapper!!.selectByCondition(condition)
    }

    override fun findAll(): List<T> {
        return mapper!!.selectAll()
    }
}
