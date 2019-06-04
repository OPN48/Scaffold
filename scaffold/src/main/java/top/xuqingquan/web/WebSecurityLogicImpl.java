package top.xuqingquan.web;

import android.os.Build;
import androidx.collection.ArrayMap;
import com.tencent.smtt.sdk.WebView;
import top.xuqingquan.utils.Timber;
import top.xuqingquan.web.agent.AgentWebConfig;
import top.xuqingquan.web.agent.SecurityType;
import top.xuqingquan.web.x5.X5WebConfig;


public class WebSecurityLogicImpl implements WebSecurityCheckLogic {

    public static WebSecurityLogicImpl getInstance() {
        return new WebSecurityLogicImpl();
    }

    public WebSecurityLogicImpl() {
    }

    @Override
    public void dealHoneyComb(WebView view) {
        if (Build.VERSION_CODES.HONEYCOMB > Build.VERSION.SDK_INT || Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return;
        }
        view.removeJavascriptInterface("searchBoxJavaBridge_");
        view.removeJavascriptInterface("accessibility");
        view.removeJavascriptInterface("accessibilityTraversal");
    }
}
