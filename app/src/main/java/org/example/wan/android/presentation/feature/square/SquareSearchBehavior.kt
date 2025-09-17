package org.example.wan.android.presentation.feature.square

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout

/**
 * 继承自官方的 AppBarLayout.ScrollingViewBehavior
 * 根据折叠程度 设置透明度，目标 View 是 CoordinatorLayout / AppBarLayout / 首个 View
 */
class SquareSearchBehavior(context: Context?, attrs: AttributeSet?) : AppBarLayout.ScrollingViewBehavior(context, attrs) {

    private var totalScrollRange = 0

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {

        if (dependency is AppBarLayout) {
            if (totalScrollRange == 0) {
                // 获取总滚动范围
                totalScrollRange = dependency.totalScrollRange
            } else {
                // 计算展开比例 [0, 1]
                // 0 完全折叠
                // 1 完全展开
                val fraction = 1 - (-dependency.y / totalScrollRange)

                // AppBarLayout 下，首个 View。对应业务代码的 搜索框
                dependency.getChildAt(0)?.let { view ->
                    // 根据折叠程度 设置透明度
                    view.alpha = fraction
                    // 修改参数后 请求重新布局
                    view.requestLayout()
                }
            }
        }

        return super.onDependentViewChanged(parent, child, dependency)
    }

}