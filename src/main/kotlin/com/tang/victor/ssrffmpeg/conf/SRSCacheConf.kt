package com.tang.victor.ssrffmpeg.conf

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SRSCacheConf {
    @Bean
    fun srsCache(): Cache<String, Process> {
        return CacheBuilder.newBuilder()
            .removalListener<String, Process> {
                it.value?.destroy()
            }
            .build()
    }
}