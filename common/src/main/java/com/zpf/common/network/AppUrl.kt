package com.zpf.common.network

import android.text.TextUtils
import com.zpf.common.app.AppCacheKey
import com.zpf.tool.config.AppContext
import com.zpf.tool.config.GlobalConfigImpl
import com.zpf.tool.expand.util.CacheMap
import java.io.IOException
import java.util.*

object AppUrl {

    var BASE_HOST: String = getBaseApiHost()
    var HTML_HOST: String = getHtmlHost()

    fun reset() {
        BASE_HOST = getBaseApiHost()
        HTML_HOST = getHtmlHost()
    }

    private fun getBaseApiHost(): String {
        if (!GlobalConfigImpl.get().isDebug) {
            val properties = Properties()
            try {
                properties.load(AppContext.get().assets.open("host_release.properties"))
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return properties.getProperty("HOST_BASE")
        }
        var result = CacheMap.getString(AppCacheKey.HOST_BASE)
        if (TextUtils.isEmpty(result)) {
            val properties = Properties()
            try {
                properties.load(AppContext.get().assets.open("host_default.properties"))
            } catch (e: IOException) {
                e.printStackTrace()
            }
            result = properties.getProperty("HOST_BASE")
        }
        CacheMap.put(AppCacheKey.HOST_BASE, result)
        return result
    }

    private fun getHtmlHost(): String {
        if (!GlobalConfigImpl.get().isDebug) {
            val properties = Properties()
            try {
                properties.load(AppContext.get().assets.open("host_release.properties"))
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return properties.getProperty("HOST_HTML")
        }
        var result = CacheMap.getString(AppCacheKey.HOST_HTML)
        if (TextUtils.isEmpty(result)) {
            val properties = Properties()
            try {
                properties.load(AppContext.get().assets.open("host_default.properties"))
            } catch (e: IOException) {
                e.printStackTrace()
            }
            result = properties.getProperty("HOST_HTML")
        }
        CacheMap.put(AppCacheKey.HOST_HTML, result)
        return result
    }


}