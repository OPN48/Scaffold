package top.xuqingquan.base.model.datasource

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PositionalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import top.xuqingquan.app.ScaffoldConfig
import top.xuqingquan.base.model.entity.NetworkStatus
import kotlin.coroutines.CoroutineContext

abstract class BasePositionalDataSource<Value> : PositionalDataSource<Value>() {
    /**
     * 获取Retrofit的Service
     */
    protected val repositoryManager by lazy {
        ScaffoldConfig.getRepositoryManager()
    }

    /**
     * 用于更新全过程的网络状态
     */
    val networkState by lazy {
        MutableLiveData<NetworkStatus>()
    }

    /**
     * 用于更新刚开始的网络状态
     */
    val initialLoad by lazy {
        MutableLiveData<NetworkStatus>()
    }

    /**
     * 如果为空的话就用这个发射空数据提示
     */
    val empty by lazy {
        MutableLiveData<String>()
    }

    /**
     * 错误后重试的加载函数
     */
    protected var retry: (() -> Any)? = null

    /**
     * error监听
     */
    val exception by lazy {
        MutableLiveData<Throwable>()
    }

    /**
     * 错误重试调用
     */
    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.invoke()
    }

    protected fun <T> launch(
        context: CoroutineContext = Dispatchers.IO,
        tryBlock: suspend CoroutineScope.() -> T,
        catchBlock: suspend CoroutineScope.(Throwable) -> Unit = {},
        finallyBlock: suspend CoroutineScope.() -> Unit = {}
    ): Job {
        return CoroutineScope(context).launch {
            try {
                tryBlock()
            } catch (e: Throwable) {
                if (ScaffoldConfig.debug()) {
                    e.printStackTrace()
                }
                catchBlock(e)
                exception.postValue(e)
                initialLoad.postValue(NetworkStatus.FAILED)
                networkState.postValue(NetworkStatus.FAILED)
            } finally {
                finallyBlock()
            }
        }
    }

    protected fun <T> launch(
        context: CoroutineContext = Dispatchers.IO,
        tryBlock: suspend CoroutineScope.() -> T
    ): Job {
        return launch(context, tryBlock, {}, {})
    }
}