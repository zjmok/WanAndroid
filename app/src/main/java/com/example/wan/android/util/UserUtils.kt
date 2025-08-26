package com.example.wan.android.util

import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPUtils
import com.example.wan.android.data.model.SuperUserInfo

object UserUtils {
    private const val SP_NAME = "UserInfo"
    private const val SP_KEY = "SuperUserInfo"
    private const val ENCRYPT_KEY = "UserInfo"
    private val spUtils = SPUtils.getInstance(SP_NAME)

    fun saveSuperUserInfo(superUserInfo: SuperUserInfo) {
        try {
            val json = superUserInfo.toJson()
            val encryption = json?.encrypt(ENCRYPT_KEY)
            spUtils.put(SP_KEY, encryption)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getSuperUserInfo(): SuperUserInfo? {
        return try {
            val encryption = spUtils.getString(SP_KEY, null)
            val json = encryption?.decrypt(ENCRYPT_KEY)
            GsonUtils.fromJson(json, SuperUserInfo::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun clear() {
        spUtils.remove("UserInfo")
        spUtils.remove("SuperUserInfo")
    }

    val isLogin get() = getSuperUserInfo()?.userInfo != null && getSuperUserInfo()?.userInfo?.id != null

}