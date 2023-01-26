package com.tang.victor.ssrffmpeg.service

import com.google.common.cache.Cache
import com.tang.victor.ssrffmpeg.conf.ApiConfiguration
import com.tang.victor.ssrffmpeg.controller.SRSCallbackBody
import mu.KotlinLogging
import org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_AAC
import org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_H264
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.springframework.stereotype.Service
import java.lang.Exception
import java.util.concurrent.ExecutorService

private val log = KotlinLogging.logger {}

data class SRSValue(var recorder: FFmpegFrameRecorder, var grabber: FFmpegFrameGrabber)

@Service
class SRSService(private val srsCache: Cache<String, SRSValue>, private val processSrsThreadPoolExecutorService: ExecutorService, private val apiConfiguration: ApiConfiguration) {
    fun publish(srsCallbackBody: SRSCallbackBody) {
        processSrsThreadPoolExecutorService.submit {
            process("${srsCallbackBody.tcUrl}/${srsCallbackBody.stream}",
                    apiConfiguration.bilibiliRtmp, srsCache, srsCallbackBody.clientId)
        }
    }

    fun unpublished(srsCallbackBody: SRSCallbackBody) {
        log.info(srsCallbackBody.clientId)
        val srsValue = srsCache.getIfPresent(srsCallbackBody.clientId) ?: return
        try {
            srsValue.recorder.close()
            srsValue.grabber.close()
        } finally {
            srsCache.invalidate(srsCallbackBody.clientId)
        }
    }
}

fun process(from: String, to: String, srsCache: Cache<String, SRSValue>, key: String) {
    log.info("from: $from, to: $to")
    val grabber = FFmpegFrameGrabber(from)
    grabber.start()
    val recorder = FFmpegFrameRecorder(to, grabber.imageWidth, grabber.imageHeight, grabber.audioChannels)
    recorder.format = "flv"
    recorder.videoCodec = AV_CODEC_ID_H264;
    recorder.audioCodec = AV_CODEC_ID_AAC;
    recorder.start(grabber.formatContext)
    srsCache.put(key, SRSValue(recorder, grabber))
    try {
        log.info("start record")
        while (true) {
            recorder.recordPacket(grabber.grabPacket())
        }
    } catch (e: Exception) {
        log.warn("forward failed", e)
    } finally {
        log.info("close recorder and grabber")
        recorder.close()
        grabber.close()
    }
}