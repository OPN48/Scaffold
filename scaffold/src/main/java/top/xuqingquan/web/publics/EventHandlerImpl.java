package top.xuqingquan.web.publics;

import android.view.KeyEvent;
import top.xuqingquan.web.nokernel.EventInterceptor;
import top.xuqingquan.web.nokernel.IEventHandler;
import top.xuqingquan.web.nokernel.WebConfig;

/**
 * IEventHandler 对事件的处理，主要是针对
 * 视屏状态进行了处理 ， 如果当前状态为 视频状态
 * 则先退出视频。
 */
public class EventHandlerImpl implements IEventHandler {
    private android.webkit.WebView mWebView;
    private com.tencent.smtt.sdk.WebView mX5WebView;
    private EventInterceptor mEventInterceptor;

    public static EventHandlerImpl getInstantce(android.webkit.WebView view, EventInterceptor eventInterceptor) {
        return new EventHandlerImpl(view, eventInterceptor);
    }

    private EventHandlerImpl(android.webkit.WebView webView, EventInterceptor eventInterceptor) {
        this.mWebView = webView;
        this.mEventInterceptor = eventInterceptor;
    }

    public static EventHandlerImpl getInstantce(com.tencent.smtt.sdk.WebView view, EventInterceptor eventInterceptor) {
        return new EventHandlerImpl(view, eventInterceptor);
    }

    private EventHandlerImpl(com.tencent.smtt.sdk.WebView webView, EventInterceptor eventInterceptor) {
        this.mX5WebView = webView;
        this.mEventInterceptor = eventInterceptor;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return back();
        }
        return false;
    }

    @Override
    public boolean back() {
        if (this.mEventInterceptor != null && this.mEventInterceptor.event()) {
            return true;
        }
        if (WebConfig.hasX5()) {
            if (mX5WebView != null && mX5WebView.canGoBack()) {
                mX5WebView.goBack();
                return true;
            }
        } else {
            if (mWebView != null && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
        }
        return false;
    }

}