package com.nandy007.web.utils

import com.nandy007.web.core.ServiceException

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Types

import javax.sql.DataSource

import org.apache.commons.dbutils.QueryRunner
import org.apache.commons.dbutils.handlers.MapListHandler
import org.slf4j.LoggerFactory

/**
 * 数据库操作工具
 */
object DataBaseUtil {

    private var dataSource: DataSource? = null
    private var databaseType: String? = null // 数据库类型，如果有需要根据数据库类型区别sql，可以使用

    // TODO Auto-generated catch block
    // e.printStackTrace();
    val connection: Connection?
        get() {
            var conn: Connection? = null
            try {
                conn = dataSource!!.connection
            } catch (e: SQLException) {
                throw ServiceException(e.message, e)
            }

            return conn
        }

    /**
     * 日志
     */
    private val log = LoggerFactory.getLogger(DataBaseUtil::class.java)

    fun setDataSource(dataSource: DataSource) {
        DataBaseUtil.dataSource = dataSource
    }

    fun setDatabaseType(databaseType: String) {
        DataBaseUtil.databaseType = databaseType
    }


    /*
	 *  定义方法,使用QueryRunner类的方法delete将数据表的数据删除
	 */
    @JvmOverloads
    fun execute(sql: String, params: Array<Any> = arrayOf()): Boolean {
        var rs = false
        try {
            //创建QueryRunner类对象
            val qr = QueryRunner(DataBaseUtil.dataSource)
            //调用QueryRunner方法update
            val row = qr.update(sql, *params)
            /*
            *  判断insert,update,delete执行是否成功
            *  对返回值row判断
            *  if(row>0) 执行成功
            */
            rs = if (row > 0) true else false
        } catch (e: SQLException) {
            throw ServiceException(e.message, e)
        }

        return rs

    }

    fun query(sql: String, params: Array<Any> = arrayOf()): List<Map<String, Any>>? {
        try {
            //创建QueryRunner类对象
            val qr = QueryRunner(DataBaseUtil.dataSource)

            return qr.query(sql, MapListHandler(), *params)
        } catch (e: SQLException) {
            throw ServiceException(e.message, e)
        }

    }

    fun query(sql: String): List<Map<String, Any>> {
        return query(sql)
    }

    @JvmOverloads
    fun queryRow(sql: String, params: Array<Any> = arrayOf()): Map<String, Any>? {
        val list = query(sql, params) ?: return null
        return if (list.isNotEmpty()) {
            list[0]
        } else null
    }

    @Throws(SQLException::class)
    fun setParam(prStmt: PreparedStatement?, params: Array<Any>?) {
        if (params != null) {
            for (i in params.indices) {
                if (params[i] == null) {
                    prStmt!!.setNull(i + 1, Types.NULL)
                } else if (params[i] is String) {
                    prStmt!!.setString(i + 1, params[i] as String)
                } else if (params[i] is Int) {
                    prStmt!!.setInt(i + 1, (params[i] as Int).toInt())
                } else if (params[i] is Long) {
                    prStmt!!.setLong(i + 1, (params[i] as Long).toLong())
                } else if (params[i] is java.sql.Date) {
                    prStmt!!.setDate(i + 1, params[i] as java.sql.Date)
                } else if (params[i] is java.util.Date) {
                    val udate = params[i] as java.util.Date
                    val t = java.sql.Timestamp(udate.time)
                    prStmt!!.setTimestamp(i + 1, t)
                } else if (params[i] is java.sql.Timestamp) {
                    val t = params[i] as java.sql.Timestamp
                    if (t.time == 0L) {
                        prStmt!!.setNull(i + 1, Types.NULL)
                    } else {
                        prStmt!!.setTimestamp(i + 1, params[i] as java.sql.Timestamp)
                    }
                } else if (params[i] is ByteArray) {
                    prStmt!!.setBytes(i + 1, params[i] as ByteArray)
                } else if (params[i] is Float) {
                    prStmt!!.setFloat(i + 1, (params[i] as Float).toFloat())
                } else if (params[i] is Double) {
                    prStmt!!.setDouble(i + 1, (params[i] as Double).toDouble())
                } else if (params[i] is Char) {
                    prStmt!!.setString(i + 1, params[i].toString())
                } else {
                    throw ServiceException("暂不支持此类型的数据更新 " + params[i].javaClass)
                }
            }
        }
    }

    /**
     * 批处理调用
     * @param sql
     * @param params
     * @return int[]
     * 用法：
     * List<Map></Map><String></String>, Object>> sqlObjList = new ArrayList();
     *
     * Map<String></String>, Object> map1 = new HashMap();
     * map1.put("sql", "update user set sex=? where username=?");
     * map1.put("params", new Object[]{1, "test"});
     * sqlObjList.add(map1);
     *
     * init[] rs = DatabaseUtil.batch(sqlObjList);
     */
    fun batch(sqlObjList: List<Map<String, Any>>): IntArray {
        val size = sqlObjList.size
        val result = IntArray(size)
        val con = connection
        try {
            con!!.autoCommit = false//设置禁用自动提交
            for (i in 0 until size) {
                var prStmt: PreparedStatement? = null
                try {
                    val sqlObj = sqlObjList[i]
                    val sql = sqlObj["sql"] as String
                    val params = sqlObj["params"] as Array<Any>

                    prStmt = con.prepareStatement(sql)
                    setParam(prStmt, params)
                    result[i] = prStmt!!.executeUpdate()

                } catch (ex: SQLException) {
                    throw ex
                } finally {
                    prStmt?.close()
                }
            }
            con.commit()
        } catch (e: SQLException) {
            if (con != null) {
                try {
                    con.rollback()
                } catch (ex: SQLException) {
                    throw ServiceException(ex.message, ex)
                }

            }
            throw ServiceException(e.message, e)
        } finally {
            if (con != null) {
                try {
                    con.autoCommit = true
                } catch (e: SQLException) {
                    ServiceException(e.message, e)
                }

                try {
                    con.close()
                } catch (e: SQLException) {
                    ServiceException(e.message, e)
                }

            }
        }

        return result
    }

    fun batchSimple(sqlObjList: List<Map<String, Any>>): Boolean {
        val rs = batch(sqlObjList)
        return rs.size == sqlObjList.size
    }

    /**
     * 批处理调用
     * @param sql
     * @param params
     * @return int[]
     */
    fun batch(sql: String, params: Array<Array<Any>>): IntArray {
        try {
            val qr = QueryRunner(DataBaseUtil.dataSource)
            return qr.batch(sql, params)
        } catch (e: SQLException) {
            throw ServiceException(e.message, e)
        }

    }

    fun batchSimple(sql: String, params: Array<Array<Any>>): Boolean {
        val rs = batch(sql, params)
        return rs.size == params.size
    }


    /**
     * 存储过程调用
     * @param sql
     * @param params
     *
     * 用法：
     *
     * OutParameter<Integer> op = new OutParameter<Integer>(Types.INTEGER, Integer.class);
     *
     * DataBaseUtil.call("call myFourth_proc(?, ?)", new Object[]{3, op});
     *
     * System.err.println(op);
    </Integer></Integer> */
    fun call(sql: String, params: Array<Any>) {
        val qr = QueryRunner(DataBaseUtil.dataSource)
        try {
            qr.execute(sql, *params)
        } catch (e: SQLException) {
            // TODO Auto-generated catch block
            throw ServiceException(e.message, e)
        }

    }

}
