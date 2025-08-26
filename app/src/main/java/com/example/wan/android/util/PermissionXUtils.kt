package com.example.wan.android.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.Utils
import com.permissionx.guolindev.PermissionMediator
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.R
import com.permissionx.guolindev.dialog.allSpecialPermissions
import com.permissionx.guolindev.dialog.permissionMapOnQ
import com.permissionx.guolindev.dialog.permissionMapOnR
import com.permissionx.guolindev.dialog.permissionMapOnS
import com.permissionx.guolindev.dialog.permissionMapOnT

fun FragmentActivity.reqPermissions(
    permissionsList: List<String>,
    action: () -> Unit
) = PermissionX.init(this)
    .configAndRequest(permissionsList, action)

fun Fragment.reqPermissions(
    permissionsList: List<String>,
    action: () -> Unit
) = PermissionX.init(this)
    .configAndRequest(permissionsList, action)

private fun PermissionMediator.configAndRequest(
    permissionsList: List<String>,
    action: () -> Unit
) {
    this.permissions(permissionsList)
        .explainReasonBeforeRequest()
        .onExplainRequestReason { scope, deniedList, beforeRequest ->
            if (beforeRequest) {
                scope.showRequestReasonDialog(
                    deniedList,
                    "需要您授予必要权限\n以保证App正常使用",
                    "前往授权",
                    "取消"
                )
            }
        }
        .onForwardToSettings { scope, deniedList ->
/*
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "您需要在“设置”中手动允许必要的权限",
                    "前往设置",
                    "取消"
                )
*/
            val permissionsLabel = getPermissionsLabLel(Utils.getApp(), deniedList)
            toastLong("您已多次拒绝授权\n请前往App设置中打开相关权限\n${permissionsLabel.toList()}")
        }
        .request { allGranted, grantedList, deniedList ->
            if (allGranted) {
                action.invoke()
            } else {
                toast("您已拒绝授权")
            }
        }
}

fun getPermissionGroup(context: Context, permission: String): String? {
    val currentVersion = Build.VERSION.SDK_INT
    val permissionGroup = when {
        currentVersion < Build.VERSION_CODES.Q -> {
            try {
                val permissionInfo = context.packageManager.getPermissionInfo(permission, 0)
                permissionInfo.group
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                null
            }
        }

        currentVersion == Build.VERSION_CODES.Q -> permissionMapOnQ[permission]
        currentVersion == Build.VERSION_CODES.R -> permissionMapOnR[permission]
        currentVersion == Build.VERSION_CODES.S -> permissionMapOnS[permission]
        currentVersion == Build.VERSION_CODES.TIRAMISU -> permissionMapOnT[permission]
        else -> {
            // If currentVersion is newer than the latest version we support, we just use
            // the latest version for temp. Will upgrade in the next release.
            permissionMapOnT[permission]
        }
    }
    return permissionGroup
}

fun getPermissionsLabLel(context: Context, permissions: List<String>): List<String> {
    val permissionsLabel = mutableListOf<String>()
    val tempSet = HashSet<String>()
    for (permission in permissions) {
        val permissionGroup = getPermissionGroup(context, permission)
        if ((permission in allSpecialPermissions && !tempSet.contains(permission))
            || (permissionGroup != null && !tempSet.contains(permissionGroup))) {
            var permissionLabel: String
            when {
                permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION -> {
                    permissionLabel =
                        context.getString(R.string.permissionx_access_background_location)
                }

                permission == Manifest.permission.SYSTEM_ALERT_WINDOW -> {
                    permissionLabel = context.getString(R.string.permissionx_system_alert_window)
                }

                permission == Manifest.permission.WRITE_SETTINGS -> {
                    permissionLabel = context.getString(R.string.permissionx_write_settings)
                }

                permission == Manifest.permission.MANAGE_EXTERNAL_STORAGE -> {
                    permissionLabel = context.getString(R.string.permissionx_manage_external_storage)
                }

                permission == Manifest.permission.REQUEST_INSTALL_PACKAGES -> {
                    permissionLabel =
                        context.getString(R.string.permissionx_request_install_packages)
                }

                permission == Manifest.permission.POST_NOTIFICATIONS
                        && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU -> {
                    // When OS version is lower than Android 13, there isn't a notification icon or labelRes for us to get.
                    // So we need to handle it as special permission's way.
                    permissionLabel = context.getString(R.string.permissionx_post_notification)
                }

                permission == Manifest.permission.BODY_SENSORS_BACKGROUND -> {
                    permissionLabel = context.getString(R.string.permissionx_body_sensor_background)
                }

                else -> {
                    permissionLabel = context.getString(context.packageManager.getPermissionGroupInfo(permissionGroup!!, 0).labelRes)
                }
            }
            permissionsLabel.add(permissionLabel)
            tempSet.add(permissionGroup ?: permission)
        }
    }
    return permissionsLabel
}
