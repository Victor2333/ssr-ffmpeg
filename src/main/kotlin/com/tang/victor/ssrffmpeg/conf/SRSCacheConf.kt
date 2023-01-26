package com.tang.victor.ssrffmpeg.conf

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.tang.victor.ssrffmpeg.service.SRSValue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Configuration
class SRSCacheConf {

    @Bean
    fun srsCache(): Cache<String, SRSValue> {
        return CacheBuilder.newBuilder().build()
    }

    @Bean
    fun processSrsThreadPoolExecutorService(): ExecutorService {
        val threadFactory = ThreadFactoryBuilder()
                .setNameFormat("srs-thread-%d").build();
        return Executors.newCachedThreadPool(threadFactory)
    }
}