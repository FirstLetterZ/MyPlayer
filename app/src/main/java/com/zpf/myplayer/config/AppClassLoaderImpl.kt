package com.zpf.myplayer.config

import com.zpf.api.IClassLoader

class AppClassLoaderImpl : IClassLoader{

    override fun getClass(name: String?): Class<*>?{
        return null
    }

    override fun newInstance(name: String?, vararg args: Any?): Any? {
        return null
    }
}