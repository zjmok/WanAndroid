package com.zjmok.util

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/*
 * AES 默认模式在不同平台或实现中可能会有不同的默认填充和工作模式，明确指定为 "AES/ECB/PKCS5Padding" 可以确保加密和解密过程中的一致性和兼容性。
 * - "AES" 只是算法名称，默认模式和填充方式不一定明确，可能导致不同环境下行为不一致。
 * - "ECB" 是电子密码本模式，简单但安全性较低，适合某些场景。
 * - "PKCS5Padding" 是一种填充方式，确保明文长度不是块大小倍数时能正确加密。
 * 因此, "AES/ECB/PKCS5Padding" 明确指定了加密模式和填充方式，避免因默认值不同导致的加解密失败或数据错误。
 */
/**
 * AES 加密
 */
fun String.encrypt(key: String): String {
    // 1. 生成密钥
    val secretKey = generateKey(key)
//    val cipher = Cipher.getInstance("AES")
    val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    // 2. 把字符串转为字节数组
    val toByteArray = this.toByteArray(Charsets.UTF_8)
    // 3. 使用 AES 加密字节数组
    val encryptedBytes = cipher.doFinal(toByteArray)
    // 4. 使用 Base64 把字节数组编码成字符串，以便存储或传输
    return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
}

/**
 * AES 解密
 */
fun String.decrypt(key: String): String {
    // 1. 生成密钥
    val secretKey = generateKey(key)
//    val cipher = Cipher.getInstance("AES")
    val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, secretKey)
    // 2. 使用 Base64 把字符串解码成字节数组
    val decodeBase64 = Base64.decode(this, Base64.NO_WRAP)
    // 3. 使用 AES 解密字节数组
    val decryptedBytes = cipher.doFinal(decodeBase64)
    // 4. 把字节数组还原字符串
    return String(decryptedBytes, Charsets.UTF_8)
}

// 生成一个 16 字节的密钥
private fun generateKey(key: String): SecretKey {
    val keyBytes = key.toByteArray(Charsets.UTF_8).copyOf(16) // AES 要求密钥长度为 16 字节
    return SecretKeySpec(keyBytes, "AES")
}
