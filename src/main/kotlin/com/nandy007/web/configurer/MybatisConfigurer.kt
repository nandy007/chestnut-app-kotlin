package com.nandy007.web.configurer

import com.github.pagehelper.PageHelper
import org.apache.ibatis.plugin.Interceptor
import org.apache.ibatis.session.SqlSessionFactory
import org.mybatis.spring.SqlSessionFactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.io.support.ResourcePatternResolver
import tk.mybatis.spring.mapper.MapperScannerConfigurer

// import javax.annotation.Resource;
import javax.sql.DataSource
import java.util.Properties// import org.springframework.beans.factory.annotation.Qualifier;

// import org.springframework.boot.autoconfigure.AutoConfigureAfter;
// import org.springframework.context.annotation.Conditional;

import com.nandy007.web.core.ProjectConstant

/**
 * Mybatis & Mapper & PageHelper 配置
 */
@Configuration
class MybatisConfigurer{
    companion object {
        @Bean
        @Throws(Exception::class)
        fun sqlSessionFactoryBean(dataSource: DataSource): SqlSessionFactory? {
            val factory = SqlSessionFactoryBean()
            factory.setDataSource(dataSource)
            factory.setTypeAliasesPackage(ProjectConstant.MODEL_PACKAGE)

            //配置分页插件，详情请查阅官方文档
            val pageHelper = PageHelper()
            val properties = Properties()
            properties.setProperty("pageSizeZero", "true")//分页尺寸为0时查询所有纪录不再执行分页
            properties.setProperty("reasonable", "true")//页码<=0 查询第一页，页码>=总页数查询最后一页
            properties.setProperty("supportMethodsArguments", "true")//支持通过 Mapper 接口参数来传递分页参数
            pageHelper.setProperties(properties)

            //添加插件
            factory.setPlugins(arrayOf<Interceptor>(pageHelper))

            //添加XML目录
            val resolver = PathMatchingResourcePatternResolver()
            factory.setMapperLocations(resolver.getResources("classpath:mapper/*.xml"))
            return factory.getObject()
        }

        @Bean
        fun mapperScannerConfigurer(): MapperScannerConfigurer {
            val mapperScannerConfigurer = MapperScannerConfigurer()
            mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryBean")
            mapperScannerConfigurer.setBasePackage(ProjectConstant.MAPPER_PACKAGE)

            //配置通用Mapper，详情请查阅官方文档
            val properties = Properties()
            properties.setProperty("mappers", ProjectConstant.MAPPER_INTERFACE_REFERENCE)
            properties.setProperty("notEmpty", "false")//insert、update是否判断字符串类型!='' 即 test="str != null"表达式内是否追加 and str != ''
            properties.setProperty("IDENTITY", "MYSQL")
            mapperScannerConfigurer.setProperties(properties)

            return mapperScannerConfigurer
        }
    }
}


