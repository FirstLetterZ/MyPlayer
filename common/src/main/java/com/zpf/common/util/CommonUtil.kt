package com.zpf.common.util

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import com.zpf.tool.PublicUtil
import com.zpf.tool.config.AppContext
import java.io.File

object CommonUtil {
    fun getUserAgent(context: Context): String {
        val separator = File.separator
        return PublicUtil.getAppName(context) + separator +
                //平台
                "android" + separator +
                //应用包名
                AppContext.get().packageName + separator +
                //设备名称
                Build.BRAND + Build.MODEL + separator +
                //设备唯一id
                getDeviceId(context) + separator +
                //大版本号
                PublicUtil.getVersionName(context) + separator +
                //小版本号
                PublicUtil.getVersionCode(context) + separator +
                //手机系统版本
                Build.VERSION.SDK_INT
    }

    fun getDeviceId(context: Context): String {
        val deviceId = PublicUtil.getDeviceId(context)
        if ("unknown" == deviceId) {
            return Build.DISPLAY
        } else {
            return deviceId
        }
    }
}

fun getScreenDensity(): Float {
    return AppContext.get().resources.displayMetrics.density
}

fun getScreenWidth(): Int {
    return AppContext.get().resources.displayMetrics.widthPixels
}

fun getScreenHeight(): Int {
    return AppContext.get().resources.displayMetrics.heightPixels
}

fun getColor(id: Int): Int {
    return AppContext.get().resources.getColor(id)
}

fun getString(id: Int): String {
    return AppContext.get().resources.getString(id)
}

fun getDrawable(id: Int): Drawable {
    return AppContext.get().resources.getDrawable(id, null)
}

fun getResources(): Resources {
    return AppContext.get().resources
}