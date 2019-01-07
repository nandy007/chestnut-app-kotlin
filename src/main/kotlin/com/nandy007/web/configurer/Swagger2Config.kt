package com.nandy007.web.configurer

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

/**
 * @author 程序猿DD
 * @version 1.0.0
 * @date 16/4/18 下午12:02.
 * @blog http://blog.didispace.com
 */
@Configuration
@EnableSwagger2
class Swagger2Config {

    @Bean
    fun createRestApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.nandy007.web"))
                .paths(PathSelectors.any())
                .build()
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder()
                .title("使用Chestnut App构建RESTful APIs")
                .description("一个基于Spring Boot & MyBatis & 前端agile 的基础项目，用于快速开发WebApp。")
                .termsOfServiceUrl("https://github.com/nandy7/chestnut-app-java")
                .contact("nandy007")
                .version("1.0")
                .build()
    }

}