package com.example.wan.android.presentation.feature.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.example.wan.android.R

class LoadingDialog(context: Context) : Dialog(context, R.style.AppThemeDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)
        setCanceledOnTouchOutside(false)
        window?.run {
            setDimAmount(0f)
        }
    }

}