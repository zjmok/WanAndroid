package org.example.wan.android.data.local.cache

import java.math.BigInteger
import java.security.MessageDigest

interface CacheKeyGenerator {
    fun generateKey(url: String, params: Map<String, Any>? = null): String
}

class Md5CacheKeyGenerator : CacheKeyGenerator {
    override fun generateKey(url: String, params: Map<String, Any>?): String {
        val keyBuilder = StringBuilder(url)
        params?.entries?.sortedBy { it.key }?.forEach { (key, value) ->
            keyBuilder.append("_$key=$value")
        }
        return md5(keyBuilder.toString())
    }

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}
