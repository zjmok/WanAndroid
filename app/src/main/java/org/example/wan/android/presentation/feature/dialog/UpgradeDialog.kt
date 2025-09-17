package org.example.wan.android.presentation.feature.dialog

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.util.ScreenUtils
import org.example.wan.android.R
import org.example.wan.android.databinding.DialogUpgradeBinding
import splitties.views.onClick
import kotlin.math.roundToInt

class UpgradeDialog(ctx: Context) : AlertDialog(ctx, R.style.dialog) {

    var binding = DialogUpgradeBinding.inflate(layoutInflater, null, false).apply {
//        iv.imageResource = R.drawable.ic_launcher_background
        tvOk.onClick { dismiss() }
        tvCancel.onClick { dismiss() }
    }

    override fun show() {
        super.show()
        setCustomView()
    }

    private fun setCustomView() {
        window?.apply {
            attributes = attributes.apply {
                width = (ScreenUtils.getScreenWidth() * 0.8f).roundToInt()
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                gravity = Gravity.CENTER or Gravity.CENTER
            }
            setContentView(binding.root)
        }
    }

    fun setTitle(text: String): UpgradeDialog {
        binding.tvTitle.text = text
        return this
    }

    fun setContent(text: String): UpgradeDialog {
        binding.tvContent.text = text
        return this
    }

    fun onUpgrade(listener: (() -> Unit)?): UpgradeDialog {
        binding.tvOk.onClick {
            listener?.invoke()
            dismiss()
        }
        return this
    }

    fun onCancel(listener: (() -> Unit)?): UpgradeDialog {
        binding.tvCancel.onClick {
            listener?.invoke()
            dismiss()
        }
        return this
    }

}