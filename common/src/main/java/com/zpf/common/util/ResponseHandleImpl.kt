package com.zpf.common.util

import com.zpf.api.IResultBean
import com.zpf.support.network.base.IResponseHandler
import com.zpf.tool.ToastUtil
import com.zpf.tool.config.GlobalConfigImpl

class ResponseHandleImpl private constructor() : IResponseHandler {

    private object Instance {
        val mInstance = ResponseHandleImpl()
    }

    override fun parsingException(e: Throwable): IResultBean<*>? {
        return null
    }

    override fun checkDataNull(data: Any): Boolean {
        return false
    }

    override fun interceptFailHandle(result: IResultBean<*>): Boolean {
        return false
    }

    override fun showHint(code: Int, message: String?) {
        if (GlobalConfigImpl.get().isDebug) {
            ToastUtil.toast("$message($code)")
        } else {
            ToastUtil.toast(message)
        }
    }

    companion object {

        fun get(): ResponseHandleImpl {
            return Instance.mInstance
        }
    }
}
