package top.xuqingquan.web.publics

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import top.xuqingquan.R
import top.xuqingquan.utils.Timber
import top.xuqingquan.utils.clearCacheFolder
import top.xuqingquan.web.nokernel.PermissionInterceptor
import top.xuqingquan.web.nokernel.WebConfig
import top.xuqingquan.web.nokernel.WebUtils
import top.xuqingquan.web.nokernel.WebUtils.isUIThread
import java.io.File
import java.lang.ref.WeakReference

object AgentWebUtils {

    @JvmStatic
    fun clearWebView(m: android.webkit.WebView?) {
        try {
            if (m == null) {
                return
            }
            if (!isUIThread()) {
                return
            }
            m.loadUrl("about:blank")
            m.stopLoading()
            if (m.handler != null) {
                m.handler.removeCallbacksAndMessages(null)
            }
            m.removeAllViews()
            val mViewGroup = m.parent as ViewGroup?
            mViewGroup?.removeView(m)
            m.webChromeClient = null
            m.webViewClient = null
            m.tag = null
            m.clearHistory()
            m.destroy()
        } catch (t: Throwable) {
        }
    }

    @JvmStatic
    fun clearWebView(m: com.tencent.smtt.sdk.WebView?) {
        try {
            if (m == null) {
                return
            }
            if (!isUIThread()) {
                return
            }
            m.loadUrl("about:blank")
            m.stopLoading()
            if (m.handler != null) {
                m.handler.removeCallbacksAndMessages(null)
            }
            m.removeAllViews()
            val mViewGroup = m.parent as ViewGroup?
            mViewGroup?.removeView(m)
            m.webChromeClient = null
            m.webViewClient = null
            m.tag = null
            m.clearHistory()
            m.destroy()
        } catch (t: Throwable) {
        }
    }

    @JvmStatic
    fun show(
        parent: View,
        text: CharSequence,
        duration: Int,
        @ColorInt textColor: Int,
        @ColorInt bgColor: Int,
        actionText: CharSequence?,
        @ColorInt actionTextColor: Int,
        listener: View.OnClickListener?
    ) {
        val spannableString = SpannableString(text)
        val colorSpan = ForegroundColorSpan(textColor)
        spannableString.setSpan(
            colorSpan,
            0,
            spannableString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val snackbarWeakReference = WeakReference(Snackbar.make(parent, spannableString, duration))
        val snackbar = snackbarWeakReference.get() ?: return
        snackbar.view.setBackgroundColor(bgColor)
        if (!actionText.isNullOrEmpty() && listener != null) {
            snackbar.setActionTextColor(actionTextColor)
            snackbar.setAction(actionText, listener)
        }
        snackbar.show()
    }

    @JvmStatic
    fun clearWebViewAllCache(context: Context, webView: android.webkit.WebView) {
        try {
            AgentWebConfig.removeAllCookies(null)
            webView.settings.cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE
            context.deleteDatabase("webviewCache.db")
            context.deleteDatabase("webview.db")
            webView.clearCache(true)
            webView.clearHistory()
            webView.clearFormData()
            clearCacheFolder(File(WebUtils.getCachePath(context)), 0)
        } catch (t: Throwable) {
            Timber.e(t)
        }

    }

    @JvmStatic
    fun clearWebViewAllCache(context: Context, webView: com.tencent.smtt.sdk.WebView) {
        try {
            AgentWebConfig.removeAllX5Cookies(null)
            webView.settings.cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE
            context.deleteDatabase("webviewCache.db")
            context.deleteDatabase("webview.db")
            webView.clearCache(true)
            webView.clearHistory()
            webView.clearFormData()
            clearCacheFolder(File(WebUtils.getCachePath(context)), 0)
        } catch (t: Throwable) {
            Timber.e(t)
        }

    }

    @JvmStatic
    fun clearWebViewAllCache(context: Context) {
        try {
            if (WebConfig.hasX5()) {
                clearWebViewAllCache(
                    context,
                    com.tencent.smtt.sdk.WebView(context.applicationContext)
                )
            } else {
                clearWebViewAllCache(context, android.webkit.WebView(context.applicationContext))
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }

    @JvmStatic
    fun getAgentWebUIControllerByWebView(webView: android.webkit.WebView): AbsAgentWebUIController? {
        val mWebParentLayout = getWebParentLayoutByWebView(webView)
        return mWebParentLayout.provide()
    }

    @JvmStatic
    fun getAgentWebUIControllerByWebView(webView: com.tencent.smtt.sdk.WebView): AbsAgentWebUIController? {
        val mWebParentLayout = getWebParentLayoutByWebView(webView)
        return mWebParentLayout.provide()
    }

    @JvmStatic
    fun getWebParentLayoutByWebView(webView: android.webkit.WebView): WebParentLayout {
        var mViewGroup: ViewGroup?
        check(webView.parent is ViewGroup) { "please check webcreator's create method was be called ?" }
        mViewGroup = webView.parent as ViewGroup
        while (mViewGroup != null) {
            Timber.i("ViewGroup:$mViewGroup")
            mViewGroup = if (mViewGroup.id == R.id.web_parent_layout_id) {
                val mWebParentLayout = mViewGroup as WebParentLayout?
                Timber.i("found WebParentLayout")
                return mWebParentLayout!!
            } else {
                val mViewParent = mViewGroup.parent
                if (mViewParent is ViewGroup) {
                    mViewParent
                } else {
                    null
                }
            }
        }
        throw IllegalStateException("please check webcreator's create method was be called ?")
    }

    @JvmStatic
    fun getWebParentLayoutByWebView(webView: com.tencent.smtt.sdk.WebView): WebParentLayout {
        var mViewGroup: ViewGroup?
        check(webView.parent is ViewGroup) { "please check webcreator's create method was be called ?" }
        mViewGroup = webView.parent as ViewGroup
        while (mViewGroup != null) {
            Timber.i("ViewGroup:$mViewGroup")
            mViewGroup = if (mViewGroup.id == R.id.web_parent_layout_id) {
                val mWebParentLayout = mViewGroup as WebParentLayout?
                Timber.i("found WebParentLayout")
                return mWebParentLayout!!
            } else {
                val mViewParent = mViewGroup.parent
                if (mViewParent is ViewGroup) {
                    mViewParent
                } else {
                    null
                }
            }
        }
        throw IllegalStateException("please check webcreator's create method was be called ?")
    }

    @JvmStatic
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun showFileChooserCompat(
        activity: Activity,
        webView: android.webkit.WebView,
        valueCallbacks: android.webkit.ValueCallback<Array<Uri>>?,
        fileChooserParams: android.webkit.WebChromeClient.FileChooserParams?,
        permissionInterceptor: PermissionInterceptor,
        valueCallback: android.webkit.ValueCallback<Uri>?,
        mimeType: String?,
        jsChannelCallback: Handler.Callback?
    ): Boolean {
        try {
            val builder = FileChooser.newBuilder(activity, webView)
            if (valueCallbacks != null) {
                builder.setUriValueCallbacks(valueCallbacks)
            }
            if (fileChooserParams != null) {
                builder.setFileChooserParams(fileChooserParams)
            }
            if (valueCallback != null) {
                builder.setUriValueCallback(valueCallback)
            }
            if (!TextUtils.isEmpty(mimeType)) {
                builder.setAcceptType(mimeType)
            }
            if (jsChannelCallback != null) {
                builder.setJsChannelCallback(jsChannelCallback)
            }
            builder.setPermissionInterceptor(permissionInterceptor)
            builder.build().openFileChooser()
        } catch (throwable: Throwable) {
            Timber.e(throwable)
            if (valueCallbacks != null) {
                Timber.i("onReceiveValue empty")
                return false
            }
            valueCallback?.onReceiveValue(null)
        }

        return true
    }

    @JvmStatic
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun showFileChooserCompat(
        activity: Activity,
        webView: com.tencent.smtt.sdk.WebView,
        valueCallbacks: com.tencent.smtt.sdk.ValueCallback<Array<Uri>>?,
        fileChooserParams: com.tencent.smtt.sdk.WebChromeClient.FileChooserParams?,
        permissionInterceptor: PermissionInterceptor,
        valueCallback: com.tencent.smtt.sdk.ValueCallback<Uri>?,
        mimeType: String?,
        jsChannelCallback: Handler.Callback?
    ): Boolean {
        try {
            val builder = FileChooser.newBuilder(activity, webView)
            if (valueCallbacks != null) {
                builder.setUriValueCallbacks(valueCallbacks)
            }
            if (fileChooserParams != null) {
                builder.setFileChooserParams(fileChooserParams)
            }
            if (valueCallback != null) {
                builder.setUriValueCallback(valueCallback)
            }
            if (!TextUtils.isEmpty(mimeType)) {
                builder.setAcceptType(mimeType)
            }
            if (jsChannelCallback != null) {
                builder.setJsChannelCallback(jsChannelCallback)
            }
            builder.setPermissionInterceptor(permissionInterceptor)
            builder.build().openFileChooser()
        } catch (throwable: Throwable) {
            Timber.e(throwable)
            if (valueCallbacks != null) {
                Timber.i("onReceiveValue empty")
                return false
            }
            valueCallback?.onReceiveValue(null)
        }

        return true
    }
}
