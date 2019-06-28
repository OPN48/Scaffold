package top.xuqingquan.sample

import android.app.Application
import android.content.Context
import top.xuqingquan.app.ScaffoldConfig
import top.xuqingquan.delegate.AppDelegate
import top.xuqingquan.delegate.AppLifecycles

class App : Application() {

    //已经通过ContentProvider方式初始化，这边的初始化可以不用了
    private lateinit var mAppDelegate: AppLifecycles

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        mAppDelegate = AppDelegate.getInstance(base!!)
    }

    override fun onCreate() {
        super.onCreate()
        mAppDelegate.onCreate(this)
//        ScaffoldConfig.getInstance(this)
//            .setBaseUrl("https://api.douban.com")
    }

    override fun onTerminate() {
        super.onTerminate()
        mAppDelegate.onTerminate(this)
    }
}