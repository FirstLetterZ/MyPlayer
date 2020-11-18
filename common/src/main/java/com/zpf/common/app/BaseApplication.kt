package com.zpf.common.app

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.webkit.WebView
import androidx.annotation.CallSuper
import com.zpf.support.util.LogUtil
import com.zpf.tool.PublicUtil
import com.zpf.tool.config.GlobalConfigImpl
import com.zpf.tool.expand.util.CacheMap
import com.zpf.tool.expand.util.SpUtil
import com.zpf.tool.stack.AppStackUtil

open class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (PublicUtil.isPackageProcess(this)) {
            initConfigsOnMain()
            Thread {
                initConfigsOnThread()
            }.start()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            //Android P 以及之后版本不支持同时从多个进程使用具有相同数据目录的WebView
            val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val myPid = android.os.Process.myPid()
            val suffix = activityManager?.let {
                val appProcessInfoList = it.runningAppProcesses
                for (info in appProcessInfoList) {
                    if (info.pid == myPid) {
                        return@let info.processName
                    }
                }
                null
            }
            WebView.setDataDirectorySuffix(suffix ?: myPid.toString())
        }
    }

    @CallSuper
    protected open fun initConfigsOnMain() {
        //初始化Fresco
        registerActivityLifecycleCallbacks(AppStackUtil.get())
        CacheMap.setLocalStorage(SpUtil.get())
    }

    @CallSuper
    protected open fun initConfigsOnThread() {
        LogUtil.setLogOut(true)
        LogUtil.setTAG(AppConstant.APP_TAG)
        LogUtil.setLogOut(GlobalConfigImpl.get().isDebug)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        PublicUtil.fixAssetManager(arrayOf("OPPO R9", "OPPO A5", "OPPO A3", "OPPO R7", "360 180", "LE X8"));
    }

}