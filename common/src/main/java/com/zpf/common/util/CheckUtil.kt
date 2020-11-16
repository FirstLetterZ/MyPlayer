package com.zpf.common.util

import android.content.Intent
import android.net.Uri
import com.zpf.tool.config.AppContext
import java.util.regex.Pattern

object CheckUtil {

    /**
     * 检测是否安装支付宝
     */
    fun checkAliPayInstalled(): Boolean {
        val uri = Uri.parse("alipays://platformapi/startApp")
        val componentName =
            Intent(Intent.ACTION_VIEW, uri).resolveActivity(AppContext.get().packageManager)
        return componentName != null
    }

    /**
     * 检测是否安装微信客户端
     */
    fun checkWeixinInstalled(): Boolean {
        val packageManager = AppContext.get().packageManager
        // 获取所有已安装程序的包信息
        val pinfo = packageManager.getInstalledPackages(0)
        if (pinfo != null) {
            for (p in pinfo) {
                val pn = p.packageName
                if (pn == "com.tencent.mm") {
                    return true
                }
            }
        }
        return false
    }

    // 大陆手机号码11位数
    fun isChinaPhoneLegal(str: String?): Boolean {
        if ((str?.length ?: 0) < 11) {
            return false
        }
        val regExp = "^(1[3-9])\\d{9}$"
        val p = Pattern.compile(regExp)
        val m = p.matcher(str)
        return m.matches()
    }

    @JvmOverloads
    fun checkIpLegal(ipString: String?, port: Int = 8080): Boolean {
        return if (port < 1000 || port > 9999) {
            false
        } else if (ipString == null || ipString.length < 7 || ipString.length > 15) {
            false
        } else {
            val regex = ("^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$")
            Pattern.matches(regex, ipString)
        }

    }
}