package com.tang.victor.ssrffmpeg

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@ComponentScan("com.tang.victor.ssrffmpeg")
@SpringBootApplication
class SsrFfmpegApplication

fun main(args: Array<String>) {
	runApplication<SsrFfmpegApplication>(*args)
}
