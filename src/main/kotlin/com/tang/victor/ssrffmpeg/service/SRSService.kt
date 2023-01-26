package com.tang.victor.ssrffmpeg.service

import com.google.common.cache.Cache
import com.tang.victor.ssrffmpeg.conf.ApiConfiguration
import com.tang.victor.ssrffmpeg.controller.SRSCallbackBody
import jakarta.annotation.PreDestroy
import mu.KotlinLogging
import org.bytedeco.javacpp.Loader
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class SRSService(
    private val srsCache: Cache<String, Process>,
    private val apiConfiguration: ApiConfiguration
) {
    fun publish(srsCallbackBody: SRSCallbackBody) {
        val p = process(
            "${srsCallbackBody.tcUrl}/${srsCallbackBody.stream}",
            apiConfiguration.bilibiliRtmp
        )
        srsCache.put(srsCallbackBody.clientId, p)
    }

    fun unpublished(srsCallbackBody: SRSCallbackBody) {
        log.info(srsCallbackBody.clientId)
        srsCache.invalidate(srsCallbackBody.clientId)
    }

    @PreDestroy
    fun destory() {
        srsCache.invalidateAll()
    }
}

fun process(from: String, to: String): Process {
    val ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg::class.java)
    val processBuilder =
        ProcessBuilder(ffmpeg, "-re", "-i", from, "-codec", "copy", "-f", "flv", "-y", to)
    return processBuilder.inheritIO().start()
}