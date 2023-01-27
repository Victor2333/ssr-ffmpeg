package com.tang.victor.ssrffmpeg.conf

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

val log = KotlinLogging.logger { }

@Configuration
class SRSCacheConf {
    @Bean
    fun srsCache(): Cache<String, Process> {
        return CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofDays(1))
            .removalListener<String, Process> {
                log.info { "Cache invalidate ${it.key} ${it.value?.pid()}" }
                if (it.value?.isAlive == true) {
                    it.value?.destroy()
                }
            }
            .build()
    }
}