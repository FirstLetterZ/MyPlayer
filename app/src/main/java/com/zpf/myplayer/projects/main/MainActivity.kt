package com.zpf.myplayer.projects.main

import com.zpf.frame.IViewProcessor
import com.zpf.support.base.CompatContainerActivity

class MainActivity : CompatContainerActivity(){
    override fun defViewProcessorClass(): Class<out IViewProcessor<Any>> {
        return MainLayout::class.java
    }
}