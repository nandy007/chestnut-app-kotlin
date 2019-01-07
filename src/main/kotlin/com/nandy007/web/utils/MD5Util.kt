package com.nandy007.web.utils

import java.security.MessageDigest


object MD5Util {

    private val hexDigIts = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f")

    /**
     * MD5加密
     * @param origin 字符
     * @param charsetname 编码
     * @return
     */
    @JvmOverloads
    fun MD5Encode(origin: String, charsetname: String? = null): String? {
        var resultString: String? = null
        try {
            resultString = origin
            val md = MessageDigest.getInstance("MD5")
            if (null == charsetname || "" == charsetname) {
                resultString = byteArrayToHexString(md.digest(resultString!!.toByteArray()))
            } else {
                resultString = byteArrayToHexString(md.digest(resultString!!.toByteArray(charset(charsetname))))
            }
        } catch (e: Exception) {
        }

        return resultString
    }


    fun byteArrayToHexString(b: ByteArray): String {
        val resultSb = StringBuffer()
        for (i in b.indices) {
            resultSb.append(byteToHexString(b[i]))
        }
        return resultSb.toString()
    }

    fun byteToHexString(b: Byte): String {
        var n = b.toInt()
        if (n < 0) {
            n += 256
        }
        val d1 = n / 16
        val d2 = n % 16
        return hexDigIts[d1] + hexDigIts[d2]
    }

}