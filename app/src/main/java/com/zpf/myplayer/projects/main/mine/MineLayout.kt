package com.zpf.myplayer.projects.main.mine

import android.view.View
import com.zpf.common.base.BaseViewProcessor
import com.zpf.frame.ITitleBar
import com.zpf.myplayer.R

class MineLayout : BaseViewProcessor<Any>() {
    override fun getLayoutId(): Int {
        return R.layout.layout_mine
    }

    override fun initStatusTextColor() {
    }

    override fun initTitleBar(statusView: View, title: ITitleBar, dartText: Boolean) {
        mRootLayout.topLayout.layout.visibility = View.GONE
    }
}