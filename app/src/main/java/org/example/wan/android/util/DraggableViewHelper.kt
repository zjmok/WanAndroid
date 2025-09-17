package org.example.wan.android.util

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Point
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.BounceInterpolator
import androidx.annotation.FloatRange
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ScreenUtils
import splitties.systemservices.windowManager
import kotlin.math.abs
import kotlin.math.roundToInt

// 拖拽
object DraggableViewHelper {

    private var downX = 0f
    private var downY = 0f
    private var interceptClick = false
    private var finalMoveX = 0

    @SuppressLint("ClickableViewAccessibility")
    fun intrude(view: View, stickRate: Float? = 1.0f) {
        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.x
                    downY = event.y
                    interceptClick = false
                }

                MotionEvent.ACTION_MOVE -> {
                    val dX = abs(downX - event.x)
                    val dY = abs(downY - event.y)
                    val touchSlop = view.context.touchSlop
                    // 勾股定理判断范围，只要移动超出范围，就认为不是点击而是移动，即拦截点击事件
                    if (dX * dX + dY * dY > touchSlop * touchSlop) {
                        interceptClick = true
                    }
                    // rawX是在屏幕中的坐标, x是相对view原点的坐标, 这个差值的作用是在拖动时保持按下时View的位置与手指的位置同步
                    val moveX = event.rawX - downX
                    val moveY = event.rawY - downY - BarUtils.getStatusBarHeight() // 处理状态栏的差值

                    updateLocation(view, Point((moveX).roundToInt(), (moveY).roundToInt()))
                }

                MotionEvent.ACTION_UP -> {
                    if (stickRate != null) {
                        val loc = IntArray(2)
                        view.getLocationOnScreen(loc) // getLocationInWindow 获取不到
                        loc[1] = loc[1] - BarUtils.getStatusBarHeight()

                        // 判断 View 在 Window 中的位置，以中间为界
                        finalMoveX =
                            if (loc[0] + view.width / 2 >= ScreenUtils.getScreenWidth() / 2) {
                                // 右
                                ScreenUtils.getScreenWidth() - view.width
                            } else {
                                // 左
                                0
                            }
                        stickToSide(view, Point(loc[0], loc[1]), stickRate)
                    }
                    return@setOnTouchListener interceptClick
                }
            }
            return@setOnTouchListener false
        }
    }

    private fun stickToSide(
        view: View,
        point: Point,
        @FloatRange(from = 0.0, fromInclusive = false) stickRate: Float
    ) {
        if (stickRate <= 0) {
            return
        }
        val animator = ValueAnimator.ofInt(point.x, finalMoveX)
            .setDuration((abs(point.x - finalMoveX) * (1 / stickRate)).toLong())
        animator.interpolator = BounceInterpolator()
        animator.addUpdateListener { animation: ValueAnimator ->
            point.x = animation.animatedValue as Int
            updateLocation(view, point)
        }
        animator.start()
    }

    private fun updateLocation(view: View, point: Point) {
        val params = (view.layoutParams as WindowManager.LayoutParams).apply {
            x = point.x
            y = point.y
        }
        view.windowManager.updateViewLayout(view, params)
    }

}