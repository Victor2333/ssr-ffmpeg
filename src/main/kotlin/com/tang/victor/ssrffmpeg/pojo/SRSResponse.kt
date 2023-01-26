package com.tang.victor.ssrffmpeg.pojo

data class SRSResponse<T>(val message: String? = "success", val code: Number? = 0, val data: T? = null)
