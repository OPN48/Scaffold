package top.xuqingquan.base.view.activity

import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import top.xuqingquan.app.ScaffoldConfig
import top.xuqingquan.cache.Cache
import top.xuqingquan.cache.CacheType
import top.xuqingquan.delegate.IActivity
import top.xuqingquan.utils.FragmentOnKeyListener

/**
 * Created by 许清泉 on 2019-04-24 23:32
 * 不使用MVVM模式的时候可以使用这个类
 */
abstract class SimpleActivity : AppCompatActivity(), IActivity {

    private var mCache: Cache<String, Any>? = null
    protected var onKeyListener: FragmentOnKeyListener? = null
    val launchError by lazy {
        MutableLiveData<Throwable>()
    }

    /**
     * @return 布局id
     */
    @LayoutRes
    protected abstract fun getLayoutId(): Int

    final override fun provideCache(): Cache<String, Any> {
        if (mCache == null) {
            @Suppress("UNCHECKED_CAST")
            mCache =
                ScaffoldConfig.getCacheFactory().build(CacheType.ACTIVITY_CACHE) as Cache<String, Any>
        }
        return mCache!!
    }

    override fun useEventBus(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView(savedInstanceState)
    }

    /**
     * 方便给子类继承选择是否使用这种方式填充布局
     * @param savedInstanceState
     */
    protected open fun initView(savedInstanceState: Bundle?) {
        setContentView(getLayoutId())
        initData(savedInstanceState)
    }

    override fun setFragmentOnKeyListener(listener: FragmentOnKeyListener?) {
        this.onKeyListener = listener
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (onKeyListener != null) {
            val onKeyDown = onKeyListener!!.onKeyDown(keyCode, event)
            if (onKeyDown != null) {
                return onKeyDown
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
