package com.example.wan.android.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.example.wan.android.utils.MyAppUtils.getMyAppInfo
import com.example.wan.android.utils.dp2pxInt
import com.example.wan.android.utils.ext.setTypeface

class AppDetailDialog(context: Context) :
    AlertDialog(context) {

    override fun show() {
        setView(getCustomView())
        super.show()
    }

    private fun getCustomView(): View {
        return LinearLayout(context).also { outLL ->
            outLL.orientation = LinearLayout.VERTICAL
            outLL.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
            outLL.gravity = Gravity.CENTER
            outLL.setPadding(10.dp2pxInt, 0, 10.dp2pxInt, 0)
            outLL.addView(ScrollView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                ).apply {
                    weight = 1f
                }
                addView(LinearLayout(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                    )
                    addView(TextView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                        )
                        gravity = Gravity.START
                        setPadding(16.dp2pxInt, 16.dp2pxInt, 16.dp2pxInt, 16.dp2pxInt)
                        text = getMyAppInfo(context)
                        // https://www.jetbrains.com/lp/mono/
                        // https://github.com/JetBrains/JetBrainsMono
                        setTypeface("fonts/JetBrainsMono-Light.ttf")
                    })
                })
            })
            outLL.addView(LinearLayout(context).also { btnLL ->
                btnLL.orientation = LinearLayout.VERTICAL
                btnLL.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                )
                btnLL.gravity = Gravity.CENTER
                btnLL.addView(View(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1,
                    )
                    setBackgroundColor(Color.GRAY)
                })
                btnLL.addView(TextView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                    )
                    gravity = Gravity.CENTER
                    setPadding(16.dp2pxInt, 16.dp2pxInt, 16.dp2pxInt, 16.dp2pxInt)
                    text = "确定"
                    setOnClickListener {
                        dismiss()
                    }
                })
            })
        }
    }

}
