package top.xuqingquan.lifecycle

import android.app.Application
import android.content.Context
import com.github.anrwatchdog.ANRWatchDog
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.TbsListener
import com.zxy.recovery.core.Recovery
import top.xuqingquan.app.ScaffoldConfig
import top.xuqingquan.delegate.AppLifecycles
import top.xuqingquan.error.RecoveryCrashCallback
import top.xuqingquan.utils.Timber
import kotlin.concurrent.thread

/**
 * Created by 许清泉 on 2019/4/15 00:26
 */
class AppLifecyclesImpl : AppLifecycles {

    override fun attachBaseContext(base: Context?) {
    }

    override fun onCreate(application: Application) {
        if (ScaffoldConfig.debug()) {
            Timber.plant(Timber.DebugTree())
            //根据需要启用
//            Logger.addLogAdapter(object : AndroidLogAdapter(
//                PrettyFormatStrategy.newBuilder().tag(application.getString(R.string.scaffold_app_name)).build()
//            ) {
//                override fun isLoggable(priority: Int, tag: String?): Boolean {
//                    return BuildConfig.DEBUG
//                }
//            })
            try {
                //ANR监视，debug时使用
                Class.forName("com.github.anrwatchdog.ANRWatchDog")
                ANRWatchDog()
                    .setIgnoreDebugger(true)
                    .setANRListener {
                        it.printStackTrace()
                    }
                    .start()
            } catch (t: Throwable) {
            }
            try {
                Class.forName("com.zxy.recovery.core.Recovery")
                //崩溃重启框架，debug时使用
                Recovery.getInstance()
                    .debug(true)
                    .recoverInBackground(false)
                    .recoverStack(true)
                    .recoverEnabled(true)
                    .callback(RecoveryCrashCallback())
                    .silent(false, Recovery.SilentMode.RECOVER_ACTIVITY_STACK)
                    .init(application)
            } catch (e: Throwable) {
            }
        }
        try {
            Class.forName("com.tencent.smtt.sdk.QbSdk")
            thread {
                Timber.d("QbSdk----Thread.currentThread()===${Thread.currentThread()}")
                // 在调用TBS初始化、创建WebView之前进行如下配置，以开启优化方案
                QbSdk.initTbsSettings(
                    mapOf(
                        TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER to true,
                        TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE to true
                    )
                )
                QbSdk.setDownloadWithoutWifi(true)
                QbSdk.setTbsListener(object : TbsListener {
                    override fun onInstallFinish(p0: Int) {
                        Timber.d("QbSdk----onInstallFinish--->$p0")
                    }

                    override fun onDownloadFinish(p0: Int) {
                        Timber.d("QbSdk----onDownloadFinish--->$p0")
                    }

                    override fun onDownloadProgress(p0: Int) {
                        Timber.d("QbSdk----onDownloadProgress--->$p0")
                    }
                })
                val cb = object : QbSdk.PreInitCallback {
                    override fun onCoreInitFinished() {
                        Timber.d("QbSdk----onCoreInitFinished")

                    }

                    override fun onViewInitFinished(p0: Boolean) {
                        Timber.d("QbSdk----onViewInitFinished--->$p0")
                    }
                }
                //x5内核初始化接口
                QbSdk.initX5Environment(application, cb)
            }
        } catch (e: Throwable) {
        }
    }

    override fun onTerminate(application: Application) {
    }
}