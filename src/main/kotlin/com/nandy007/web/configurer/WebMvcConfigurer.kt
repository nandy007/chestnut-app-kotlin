package com.nandy007.web.configurer


import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import com.alibaba.fastjson.support.config.FastJsonConfig
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter
import com.nandy007.web.core.Result
import com.nandy007.web.core.ResultCode
import com.nandy007.web.core.ServiceException
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import java.nio.charset.Charset
import java.util.ArrayList
import java.util.Collections

/**
 * Spring MVC 配置
 */
@Configuration
class WebMvcConfigurer : WebMvcConfigurationSupport() {

    private val logger = LoggerFactory.getLogger(WebMvcConfigurer::class.java)

    @Value("\${session.use}")
    private val useSession: Boolean? = null//是否使用session

    @Value("\${session.spring.profiles.active}")
    private val env: String? = null //当前激活的配置文件

    private val isValidSign: Boolean
        get() = "dev" != env && (useSession!==true)!!

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        //Spring Boot自动配置本身不会自动把/swagger-ui.html这个路径映射到对应的目录META-INF/resources/下面。我们加上这个映射即可
        registry!!.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/")

        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/")

        // 将static目录作为静态文件目录
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/")
    }

    //使用阿里 FastJson 作为JSON MessageConverter
    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        val converter = FastJsonHttpMessageConverter()
        val config = FastJsonConfig()
        config.setSerializerFeatures(SerializerFeature.WriteMapNullValue, //保留空的字段
                SerializerFeature.WriteNullStringAsEmpty, //String null -> ""
                SerializerFeature.WriteNullNumberAsZero)//Number null -> 0
        converter.fastJsonConfig = config
        converter.defaultCharset = Charset.forName("UTF-8")
        converters!!.add(converter)
    }


    //统一异常处理
    override fun configureHandlerExceptionResolvers(exceptionResolvers: MutableList<HandlerExceptionResolver>) {
        exceptionResolvers!!.add(HandlerExceptionResolver { request, response, handler, e ->
            val result = Result()
            if (e is ServiceException) {//业务失败的异常，如“账号或密码错误”
                result.setCode(ResultCode.FAIL).setMessage(e.message!!)
                logger.info(e.message)
            } else if (e is NoHandlerFoundException) {
                result.setCode(ResultCode.NOT_FOUND).setMessage("接口 [" + request.requestURI + "] 不存在")
            } else if (e is ServletException) {
                result.setCode(ResultCode.FAIL).setMessage(e.message!!)
            } else {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR).setMessage("接口 [" + request.requestURI + "] 内部错误，请联系管理员")
                val message: String?
                if (handler is HandlerMethod) {
                    val handlerMethod = handler as HandlerMethod?
                    message = String.format("接口 [%s] 出现异常，方法：%s.%s，异常摘要：%s",
                            request.requestURI,
                            handlerMethod!!.bean.javaClass.name,
                            handlerMethod.method.name,
                            e.message)
                } else {
                    message = e.message
                }
                logger.error(message, e)
            }
            responseResult(response, result)
            ModelAndView()
        })
    }

    //解决跨域问题    跨域解决方案： http://blog.csdn.net/aeroleo/article/details/52944261
    override fun addCorsMappings(registry: CorsRegistry) {
        registry!!.addMapping("/**")
    }

    //添加拦截器
    override fun addInterceptors(registry: InterceptorRegistry) {
        //接口签名认证拦截器，该签名认证比较简单，实际项目中可以使用Json Web Token或其他更好的方式替代。
        if (isValidSign) { //开发环境 并且使用session 时忽略签名认证
            registry!!.addInterceptor(object : HandlerInterceptorAdapter() {
                @Throws(Exception::class)
                override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
                    //验证签名
                    val pass = validateSign(request!!)
                    if (pass) {
                        return true
                    } else {
                        logger.warn("签名认证失败，请求接口：{}，请求IP：{}，请求参数：{}",
                                request.requestURI, getIpAddress(request), JSON.toJSONString(request.parameterMap))

                        val result = Result()
                        result.setCode(ResultCode.UNAUTHORIZED).setMessage("签名认证失败")
                        responseResult(response!!, result)
                        return false
                    }
                }
            })
        }
    }

    private fun responseResult(response: HttpServletResponse, result: Result) {
        response.characterEncoding = "UTF-8"
        response.setHeader("Content-type", "application/json;charset=UTF-8")
        response.status = 200
        try {
            response.writer.write(JSON.toJSONString(result))
        } catch (ex: IOException) {
            logger.error(ex.message)
        }

    }

    /**
     * 一个简单的签名认证，规则：
     * 1. 将请求参数按ascii码排序
     * 2. 拼接为a=value&b=value...这样的字符串（不包含sign）
     * 3. 混合密钥（secret）进行md5获得签名，与请求的签名进行比较
     */
    private fun validateSign(request: HttpServletRequest): Boolean {
        val requestSign = request.getParameter("sign")//获得请求签名，如sign=19e907700db7ad91318424a97c54ed57
        if (StringUtils.isEmpty(requestSign)) {
            return false
        }
        val keys = ArrayList(request.parameterMap.keys)
        keys.remove("sign")//排除sign参数
        Collections.sort(keys)//排序

        val sb = StringBuilder()
        for (key in keys) {
            sb.append(key).append("=").append(request.getParameter(key)).append("&")//拼接字符串
        }
        var linkString = sb.toString()
        linkString = StringUtils.substring(linkString, 0, linkString.length - 1)//去除最后一个'&'

        val secret = "Potato"//密钥，自己修改
        val sign = DigestUtils.md5Hex(linkString + secret)//混合密钥md5

        return StringUtils.equals(sign, requestSign)//比较
    }

    private fun getIpAddress(request: HttpServletRequest): String? {
        var ip: String? = request.getHeader("x-forwarded-for")
        if (ip == null || ip.length == 0 || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("Proxy-Client-IP")
        }
        if (ip == null || ip.length == 0 || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("WL-Proxy-Client-IP")
        }
        if (ip == null || ip.length == 0 || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("HTTP_CLIENT_IP")
        }
        if (ip == null || ip.length == 0 || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR")
        }
        if (ip == null || ip.length == 0 || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.remoteAddr
        }
        // 如果是多级代理，那么取第一个ip为客户端ip
        if (ip != null && ip.indexOf(",") != -1) {
            ip = ip.substring(0, ip.indexOf(",")).trim { it <= ' ' }
        }

        return ip
    }
}
