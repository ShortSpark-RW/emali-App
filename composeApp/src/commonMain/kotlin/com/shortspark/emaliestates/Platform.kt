package com.shortspark.emaliestates

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform