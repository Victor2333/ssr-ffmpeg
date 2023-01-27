package com.tang.victor.ssrffmpeg.service

import com.google.common.cache.Cache
import com.tang.victor.ssrffmpeg.conf.ApiConfiguration
import com.tang.victor.ssrffmpeg.controller.SRSCallbackBody
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.util.Base64Utils

private val log = KotlinLogging.logger {}

@OptIn(DelicateCoroutinesApi::class)
@Service
class SRSService(
    private val srsCache: Cache<String, Process>,
    private val apiConfiguration: ApiConfiguration
) {
    fun publish(srsCallbackBody: SRSCallbackBody) {
        log.info("start stream $srsCallbackBody")
        if (srsCallbackBody.app != "bili") return
        val p = process(
            "${srsCallbackBody.tcUrl}/${srsCallbackBody.stream}",
            "${apiConfiguration.bilibiliRtmp}${String(Base64Utils.decodeFromString(srsCallbackBody.stream))}"
        )
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                p.waitFor()
                if (srsCache.getIfPresent(srsCallbackBody.clientId) != null) {
                    srsCache.invalidate(srsCallbackBody.clientId)
                }
            }
        }
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
    val processBuilder =
        ProcessBuilder("ffmpeg", "-re", "-i", from, "-codec", "copy", "-f", "flv", "-y", to)
    return processBuilder.inheritIO().start()
}