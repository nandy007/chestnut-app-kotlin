package com.nandy007.web


import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import com.nandy007.web.core.StaticHelper


@SpringBootApplication
class Application{

	companion object {
		val logger = LoggerFactory.getLogger(Application::class.java)

		fun showSuccessMsg() {
			val n = "\n\n"
			val sep = "$n---------------------------------------------------------$n"
			val msg = sep + "ChestnutApp启动完毕。端口：" + StaticHelper.serverPort + n + "请求时请注意带上头信息：'Authorization': 'Bearer ' + token" + sep
			logger.info(msg)
		}
	}
}

fun main(args: Array<String>) {
	runApplication<Application>(*args)
	Application.showSuccessMsg()
}

