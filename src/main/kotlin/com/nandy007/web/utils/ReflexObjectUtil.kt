package com.nandy007.web.utils

import java.lang.reflect.Field
import java.util.ArrayList
import java.util.HashMap

/**
 * 反射处理Bean，得到里面的属性值
 *
 * @author liulinsen
 */
object ReflexObjectUtil {

    /**
     * 单个对象的所有键值
     *
     * @param object
     * 单个对象
     *
     * @return Map<String></String>, Object> map 所有 String键 Object值 ex：{pjzyfy=0.00,
     * xh=01, zzyl=0.00, mc=住院患者压疮发生率, pjypfy=0.00, rs=0, pjzyts=0.00,
     * czydm=0037, lx=921, zssl=0.00}
     */
    fun getKeyAndValue(obj: Any): Map<String, Any> {
        val map = HashMap<String, Any>()
        // 得到类对象
        val userCla = obj.javaClass as Class<*>
        /* 得到类中的所有属性集合 */
        val fs = userCla.declaredFields
        for (i in fs.indices) {
            val f = fs[i]
            f.isAccessible = true // 设置些属性是可以访问的
            var `val` = Any()
            try {
                `val` = f.get(obj)
                // 得到此属性的值
                map[f.name] = `val`// 设置键值
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

            /*
             * String type = f.getType().toString();//得到此属性的类型 if
             * (type.endsWith("String")) {
             * System.out.println(f.getType()+"\t是String"); f.set(obj,"12") ;
             * //给属性设值 }else if(type.endsWith("int") ||
             * type.endsWith("Integer")){
             * System.out.println(f.getType()+"\t是int"); f.set(obj,12) ; //给属性设值
             * }else{ System.out.println(f.getType()+"\t"); }
             */

        }
        // System.out.println("单个对象的所有键值==反射==" + map.toString());
        return map
    }

    /**
     * 单个对象的某个键的值
     *
     * @param object
     * 对象
     *
     * @param key
     * 键
     *
     * @return Object 键在对象中所对应得值 没有查到时返回空字符串
     */
    fun getValueByKey(obj: Any, key: String): Any {
        // 得到类对象
        val userCla = obj.javaClass as Class<*>
        /* 得到类中的所有属性集合 */
        val fs = userCla.declaredFields
        for (i in fs.indices) {
            val f = fs[i]
            f.isAccessible = true // 设置些属性是可以访问的
            try {

                if (f.name.endsWith(key)) {
                    // System.out.println("单个对象的某个键的值==反射==" + f.get(obj));
                    return f.get(obj)
                }
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

        }
        // 没有查到时返回空字符串
        return ""
    }

    /**
     * 多个（列表）对象的所有键值
     *
     * @param object
     * @return List<Map></Map><String></String>,Object>> 列表中所有对象的所有键值 ex:[{pjzyfy=0.00, xh=01,
     * zzyl=0.00, mc=住院患者压疮发生率, pjypfy=0.00, rs=0, pjzyts=0.00,
     * czydm=0037, lx=921, zssl=0.00}, {pjzyfy=0.00, xh=02, zzyl=0.00,
     * mc=新生儿产伤发生率, pjypfy=0.00, rs=0, pjzyts=0.00, czydm=0037, lx=13,
     * zssl=0.00}, {pjzyfy=0.00, xh=03, zzyl=0.00, mc=阴道分娩产妇产伤发生率,
     * pjypfy=0.00, rs=0, pjzyts=0.00, czydm=0037, lx=0, zssl=0.00},
     * {pjzyfy=0.00, xh=04, zzyl=0.75, mc=输血反应发生率, pjypfy=0.00, rs=0,
     * pjzyts=0.00, czydm=0037, lx=0, zssl=0.00}, {pjzyfy=5186.12,
     * xh=05, zzyl=0.00, mc=剖宫产率, pjypfy=1611.05, rs=13, pjzyts=7.15,
     * czydm=0037, lx=13, zssl=0.00}]
     */
    fun getKeysAndValues(`object`: List<Any>): List<Map<String, Any>> {
        val list = ArrayList<Map<String, Any>>()
        for (obj in `object`) {
            val userCla: Class<*>
            // 得到类对象
            userCla = obj.javaClass
            /* 得到类中的所有属性集合 */
            val fs = userCla.declaredFields
            val listChild = HashMap<String, Any>()
            for (i in fs.indices) {
                val f = fs[i]
                f.isAccessible = true // 设置些属性是可以访问的
                var `val` = Any()
                try {
                    `val` = f.get(obj)
                    // 得到此属性的值
                    listChild[f.name] = `val`// 设置键值
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }

            }
            list.add(listChild)// 将map加入到list集合中
        }
        // System.out.println("多个（列表）对象的所有键值====" + list.toString());
        return list
    }

    /**
     * 多个（列表）对象的某个键的值
     *
     * @param object
     * @param key
     * @return List<Object> 键在列表中对应的所有值 ex:key为上面方法中的mc字段 那么返回的数据就是： [住院患者压疮发生率,
     * 新生儿产伤发生率, 阴道分娩产妇产伤发生率, 输血反应发生率, 剖宫产率]
    </Object> */
    fun getValuesByKey(`object`: List<Any>, key: String): List<Any> {
        val list = ArrayList<Any>()
        for (obj in `object`) {
            // 得到类对象
            val userCla = obj.javaClass as Class<*>
            /* 得到类中的所有属性集合 */
            val fs = userCla.declaredFields
            for (i in fs.indices) {
                val f = fs[i]
                f.isAccessible = true // 设置些属性是可以访问的
                try {
                    if (f.name.endsWith(key)) {
                        list.add(f.get(obj))
                    }
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }

            }
        }
        // System.out.println("多个（列表）对象的某个键的值列表====" + list.toString());
        return list
    }

    /**
     * @param obj
     * @param fieldName
     * @param value
     * @return
     * @throws @Description 设置 属性值
     * @author liguanghui
     * @throws Exception
     * @date 2018/10/18-10:41
     */
    fun setValue(obj: Any, fieldName: String, value: Any) {
        try {
            val field = obj.javaClass.getDeclaredField(fieldName)
            field.isAccessible = true
            field.set(obj, value)
        } catch (e: Exception) {
            // throw e;
        }

    }

}