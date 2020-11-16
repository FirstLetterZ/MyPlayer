package com.zpf.myplayer.config

import com.zpf.api.IClassLoader
import com.zpf.api.dataparser.JsonParserInterface
import com.zpf.common.util.ResponseHandleImpl
import com.zpf.support.network.base.IResponseHandler
import com.zpf.tool.config.GlobalConfigInterface
import com.zpf.tool.expand.util.ClassLoaderImpl
import com.zpf.tool.gson.GsonUtil
import java.util.*

class RealGlobalConfigImpl : GlobalConfigInterface {
    private val uuid: String = UUID.randomUUID().toString()

    override fun invokeMethod(obj: Any?, methodName: String?, vararg args: Any?): Any? {
        return null
    }

    override fun getId(): String = uuid

    override fun onObjectInit(obj: Any) {
    }

    override fun <T : Any?> getGlobalInstance(target: Class<T>): T? {
        return when (target) {
            JsonParserInterface::class.java -> GsonUtil.get() as? T
            IResponseHandler::class.java -> ResponseHandleImpl.get() as? T
            IClassLoader::class.java -> ClassLoaderImpl.get() as? T
            else -> ClassLoaderImpl.get().newInstance(target.name, null) as? T
        }
    }
}