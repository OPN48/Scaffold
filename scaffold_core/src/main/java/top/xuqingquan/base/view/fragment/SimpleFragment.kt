package top.xuqingquan.base.view.fragment

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import top.xuqingquan.app.ScaffoldConfig
import top.xuqingquan.cache.Cache
import top.xuqingquan.cache.CacheType
import top.xuqingquan.delegate.IFragment
import top.xuqingquan.utils.FragmentOnKeyListener

/**
 * Created by 许清泉 on 2019-04-24 23:38
 * 不使用MVVM模式的时候可以使用这个类
 */
abstract class SimpleFragment : Fragment(), IFragment, FragmentOnKeyListener {

    private var mCache: Cache<String, Any>? = null
    protected var mContext: Context? = null
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
            mCache = ScaffoldConfig.getCacheFactory()
                .build(CacheType.FRAGMENT_CACHE) as Cache<String, Any>
        }
        return mCache!!
    }

    override fun onAttach(context: Context) {
        this.mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = initView(inflater, container)
        initView(view)
        return view
    }

    /**
     * 给fragment简单的初始化布局
     *
     * @param view
     */
    protected abstract fun initView(view: View)

    protected open fun initView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onDetach() {
        super.onDetach()
        mContext = null
    }

    /**
     * @return 在fragment中默认使用EventBus
     */
    override fun useEventBus(): Boolean {
        return true
    }

    /**
     * 给子类提供按键监听功能
     *
     * @return null 不拦截 其余跟Activity一致
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean? {
        return null
    }

    override fun setData(data: Any?) {}

}
