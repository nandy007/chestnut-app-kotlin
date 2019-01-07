package com.nandy007.web.controller

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest


@RestController
class TestController {

    @RequestMapping("/")
    @ResponseBody
    fun pageIndex(req: HttpServletRequest): String {
        return "index"
    }
}