package com.example.wan.android.util

import android.content.Context
import android.content.DialogInterface
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import splitties.alertdialog.appcompat.alertDialog

inline fun Context.alert(
    title: CharSequence? = null,
    message: CharSequence? = null,
    isCancellable: Boolean = true,
    @DrawableRes iconResource: Int = 0,
    dialogConfig: AlertDialog.Builder.() -> Unit = {}
): AlertDialog {
    return alertDialog(
        title = title,
        message = message,
        iconResource = iconResource,
        isCancellable = isCancellable,
    ) {
        dialogConfig()
    }
}

inline fun AlertDialog.Builder.ok(
    text: String? = "确定",
    crossinline handler: (dialog: DialogInterface) -> Unit
) {
    setPositiveButton(text) { dialog: DialogInterface, _: Int -> handler(dialog) }
}

inline fun AlertDialog.Builder.cancel(
    text: String? = "取消",
    crossinline handler: (dialog: DialogInterface) -> Unit
) {
    setNegativeButton(text) { dialog: DialogInterface, _: Int -> handler(dialog) }
}

inline fun AlertDialog.Builder.neutral(
    text: String,
    crossinline handler: (dialog: DialogInterface) -> Unit
) {
    setNeutralButton(text) { dialog: DialogInterface, _: Int -> handler(dialog) }
}
