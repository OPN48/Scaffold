package top.xuqingquan.base.view.adapter.listadapter

import android.view.View

/**
 * Create by 许清泉 on 2020/6/22 23:02
 */
abstract class OnItemClickListener<T> {

    /**
     * @param view 实际点击的view
     * @param position 被点击的item
     * @param data 被点击的数据内容
     * @param viewType 被点击的viewType
     */
    abstract fun onClick(view: View, position: Int, data: T?, viewType: Int)

    /**
     * @param view 实际点击的view
     * @param position 被点击的item
     * @param data 被点击的数据内容
     * @param viewType 被点击的viewType
     * @return 长按事件常规返回
     */
    open fun onLongClick(view: View, position: Int, data: T?, viewType: Int) = true
}

class OnItemClickListenerImpl<T> : OnItemClickListener<T>() {

    private var onClick: ((view: View, position: Int, data: T?, viewType: Int) -> Unit)? = null
    private var onLongClick: ((view: View, position: Int, data: T?, viewType: Int) -> Boolean)? =
        null

    override fun onClick(view: View, position: Int, data: T?, viewType: Int) {
        onClick?.invoke(view, position, data, viewType)
    }

    override fun onLongClick(view: View, position: Int, data: T?, viewType: Int): Boolean {
        return (onLongClick?.invoke(view, position, data, viewType) ?: true)
    }

    fun onClick(l: (view: View, position: Int, data: T?, viewType: Int) -> Unit) {
        onClick = l
    }

    fun onLongClick(l: ((view: View, position: Int, data: T?, viewType: Int) -> Boolean)) {
        onLongClick = l
    }
}

abstract class OnViewClickListener {
    /**
     * 点击事件
     * @param view 被点击的视图
     * @param position 在RecyclerView中的位置
     */
    abstract fun onClick(view: View, position: Int)

    /**
     * 长按事件
     * @param view 被点击的视图
     * @param position 在RecyclerView中的位置
     */
    open fun onLongClick(view: View, position: Int) = true
}

class OnViewClickListenerImpl : OnViewClickListener() {

    private var onClick: ((view: View, position: Int) -> Unit)? = null
    private var onLongClick: ((view: View, position: Int) -> Boolean)? = null

    override fun onClick(view: View, position: Int) {
        onClick?.invoke(view, position)
    }

    override fun onLongClick(view: View, position: Int): Boolean {
        return (onLongClick?.invoke(view, position) ?: true)
    }

    fun onClick(l: (view: View, position: Int) -> Unit) {
        onClick = l
    }

    fun onLongClick(l: ((view: View, position: Int) -> Boolean)) {
        onLongClick = l
    }
}
