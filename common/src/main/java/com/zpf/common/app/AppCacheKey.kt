package com.zpf.common.app

import com.zpf.tool.expand.CacheKey

object AppCacheKey {
    /** string **/
    val ACCESS_TOKEN = CacheKey.getBaseStringKey(1)
    val HOST_HTML = CacheKey.getBaseStringKey(2)
    val HOST_BASE = CacheKey.getBaseStringKey(3)
    val USER_INFO = CacheKey.getBaseStringKey(4)
    val USER_AGENT = CacheKey.getBaseStringKey(5)
    val WEB_KIT = CacheKey.getBaseStringKey(6)

    /** boolean **/
    val SHOW_UPDATE = -CacheKey.getBaseBooleanKey(1)//显示升级弹窗
    val SUPPORT_DART_TEXT = CacheKey.getBaseBooleanKey(1)

    /** long **/
    val USER_ID = CacheKey.getBaseLongKey(1)
    /** int **/

    /** float **/
}