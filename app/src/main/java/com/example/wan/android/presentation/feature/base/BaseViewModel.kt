package com.example.wan.android.presentation.feature.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wan.android.data.remote.RetrofitClient
import com.example.wan.android.data.remote.api.ApiException
import com.example.wan.android.util.UserUtils
import com.example.wan.android.util.toast
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class BaseViewModel : ViewModel(),
    // 直接将 this 赋予 MainScope
    CoroutineScope by MainScope() {

    val loginStatus = MutableLiveData<Boolean>()

    override fun onCleared() {
        cancel()
        super.onCleared()
    }

    protected fun onLogout() {
        RetrofitClient.clearCookie() // cookie 清除
        UserUtils.clear()
    }

    /**
     * 创建并执行协程
     * @param block 协程中执行
     * @param onStart 执行前只需
     * @param onCancel 取消时只需
     * @param onError 错误时执行
     * @param onEnd 结束后执行
     * @return Job
     */
    protected fun launch(
        requireLogin: Boolean = false,
        showErrorToast: Boolean = true,
        onStart: (suspend () -> Unit)? = null,
        onCancel: (suspend (Exception) -> Unit)? = null,
        onError: (suspend (Exception) -> Unit)? = null,
        onEnd: (suspend () -> Unit)? = null,
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend (CoroutineScope) -> Unit,
    ): Job {
        return viewModelScope.launch(context, start) {
            try {
                onStart?.invoke()
                block.invoke(this)
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> {
                        onCancel?.invoke(e)
                    }

                    else -> {
                        handleError(e, showErrorToast, requireLogin)
                        onError?.invoke(e)
                    }
                }
            } finally {
                onEnd?.invoke()
            }
        }
    }

    /**
     * 创建并执行协程
     * @param block 协程中执行
     * @return Deferred<T>
     */
    protected fun <T> async(block: suspend (CoroutineScope) -> T): Deferred<T> {
        return viewModelScope.async { block.invoke(this) }
    }

    /**
     * 取消协程
     * @param job 协程job
     */
    protected fun cancelJob(job: Job?) {
        if (job != null && job.isActive && !job.isCompleted && !job.isCancelled) {
            job.cancel()
        }
    }

    /**
     * 统一处理错误
     * @param exception 异常
     * @param showErrorToast 是否显示错误吐司
     */
    protected fun handleError(
        exception: Exception,
        showErrorToast: Boolean = true,
        requireLogin: Boolean = false,
    ) {
        when (exception) {
            is ApiException -> {
                when (exception.errorCode) {
                    -1001 -> {
                        // 未登录
                        if (showErrorToast) {
                            toast(exception.message)
                        }
                        if (requireLogin) {
                            loginStatus.postValue(false)
                        }
                        onLogout()
                    }
                    // 其它错误
                    else -> {
                        if (showErrorToast) {
                            toast(exception.message)
                        }
                    }
                }
            }

            is ConnectException,
            is SocketTimeoutException,
            is UnknownHostException,
            is HttpException -> {
                if (showErrorToast) toast("网络请求失败")
            }

            else -> {
                // 未知错误 不能 toast
                exception.printStackTrace()
            }
        }
    }

}