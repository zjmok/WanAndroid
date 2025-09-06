package com.example.wan.android.util

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

// 获取当前应用的签名证书
fun Context.getAppSignature(): Signature? {
    val context = this
    // 提取第一个签名证书（多数 APK 只有一个签名）
    val signature = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        // 低于 Android P 的版本使用 GET_SIGNATURES
        val packageInfo = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_SIGNATURES
        )
        packageInfo.signatures?.firstOrNull()
    } else {
        // Android P 及以上版本使用 GET_SIGNING_CERTIFICATES
        val packageInfo = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_SIGNING_CERTIFICATES
        )
        packageInfo.signingInfo?.apkContentsSigners?.firstOrNull()
    }
    return signature
}

// 获取当前应用的签名信息 CN
// DN == Distinguished Name == 可分辨名称
// CN == Common Name == 常用名称
fun Context.getAppSigningCertificateCN(): String? {
    val context = this
    return try {
        // 生成证书对象 cert，包含完整证书信息
        val certFactory = CertificateFactory.getInstance("X.509")
        val cert = certFactory.generateCertificate(
            ByteArrayInputStream(context.getAppSignature()?.toByteArray())
        ) as X509Certificate

        // 解析证书的 Subject DN 中的 CN 字段
        val subjectDN = cert.subjectX500Principal.name
        val cn = subjectDN.split(",")
            .firstOrNull { it.trim().startsWith("CN=") }
            ?.substringAfter("CN=")
        // return
        cn
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// 获取当前应用的签名证书 MD5 SHA1 SHA256
fun Context.getAppSignatureHashCheck(algorithm: String = "SHA256"): String? {
    try {
        val md = MessageDigest.getInstance(algorithm)
        this.getAppSignature()?.toByteArray()?.let { md.update(it) }
        val digest = md.digest()
        return digest.joinToString("") { "%02x".format(it) }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

// 获取当前应用的 TargetSdk
fun Context.getAppTargetSdk(): Int {
    val context = this
    return try {
        val packageInfo = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_ACTIVITIES
        )
        packageInfo.applicationInfo?.targetSdkVersion ?: -1
    } catch (e: PackageManager.NameNotFoundException) {
        -1
    }
}

// 获取当前应用的 TargetSdk
val Context.appApi get() = getAppTargetSdk()
