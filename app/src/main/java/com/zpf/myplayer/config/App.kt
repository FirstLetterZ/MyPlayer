package com.zpf.myplayer.config

import com.zpf.common.app.BaseApplication
import com.zpf.tool.config.GlobalConfigImpl
import com.zpf.tool.expand.util.ClassLoaderImpl

class App : BaseApplication() {
    override fun initConfigsOnMain() {
        GlobalConfigImpl.get().init(this, RealGlobalConfigImpl())
        super.initConfigsOnMain()
    }

    override fun initConfigsOnThread() {
        ClassLoaderImpl.get().add(AppClassLoaderImpl())
        super.initConfigsOnThread()
    }
}