package top.xuqingquan.web.agent;

import android.widget.FrameLayout;

public interface WebCreator extends IWebIndicator {
    WebCreator create();

    android.webkit.WebView getWebView();

    com.tencent.smtt.sdk.WebView getX5WebView();

    FrameLayout getWebParentLayout();
}