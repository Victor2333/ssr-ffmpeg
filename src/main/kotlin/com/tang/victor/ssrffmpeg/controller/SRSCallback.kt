package com.tang.victor.ssrffmpeg.controller

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty
import com.tang.victor.ssrffmpeg.pojo.SRSResponse
import com.tang.victor.ssrffmpeg.service.SRSService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
data class SRSCallbackBody(
        val action: String,
        val app: String,
        @JsonProperty("client_id")
        val clientId: String,
        val ip: String,
        val `param`: String,
        val stream: String,
        val vhost: String,
        val tcUrl: String?
)

@RestController
@RequestMapping("/api/srs")
class SRSCallback(private val srsService: SRSService) {
    private val logger = KotlinLogging.logger { }

    @PostMapping("/on_publish")
    fun onPublish(@RequestBody body: SRSCallbackBody): SRSResponse<Any> {
        srsService.publish(body)
        return SRSResponse()
    }

    @PostMapping("/on_unpublish")
    fun onNonPublish(@RequestBody body: SRSCallbackBody): SRSResponse<Any> {
        srsService.unpublished(body)
        return SRSResponse()
    }

    @GetMapping("/status")
    fun status(): SRSResponse<Any> {
        return SRSResponse()
    }
}