package com.zpf.common.base

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import com.zpf.api.IEvent
import com.zpf.common.R
import com.zpf.common.app.AppCacheKey
import com.zpf.common.app.AppConstant
import com.zpf.common.util.getString
import com.zpf.frame.ITitleBar
import com.zpf.support.base.ViewProcessor
import com.zpf.tool.StatusBarTextUtil
import com.zpf.tool.expand.util.CacheMap
import com.zpf.tool.expand.util.EventManagerImpl

abstract class BaseViewProcessor<C> : ViewProcessor<C>() {

    private var visible = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initStatusTextColor()
        initTitleBar(
            mRootLayout.statusBar,
            mTitleBar,
            CacheMap.getBoolean(AppCacheKey.SUPPORT_DART_TEXT)
        )
        EventManagerImpl.get().register(javaClass.name) { a ->
            if (mContainer != null && isLiving) {
                onReceiveEvent(a)
            }
        }
    }

    protected open fun initStatusTextColor() {
        if (mContainer is Activity) {
            CacheMap.put(
                AppCacheKey.SUPPORT_DART_TEXT,
                StatusBarTextUtil.SetStatusBarLightMode(mContainer.currentActivity.window, true)
            )
        }
    }

    protected open fun initTitleBar(statusView: View, title: ITitleBar, dartText: Boolean) {
        mRootLayout.topLayout.layout.setBackgroundColor(Color.WHITE)
        title.leftImage.text = getString(R.string.icon_back)
        title.leftImage.textColor = Color.BLACK
        if (dartText) {
            mRootLayout.statusBar.setBackgroundColor(Color.TRANSPARENT)
        } else {
            mRootLayout.statusBar.setBackgroundColor(Color.LTGRAY)
        }
    }

    @CallSuper
    override fun onDestroy() {
        EventManagerImpl.get().unregister(javaClass.name)
    }

    @CallSuper
    override fun onVisibleChanged(visibility: Boolean) {
        visible = visibility
        super.onVisibleChanged(visibility)
    }

    override fun onReceiveEvent(event: IEvent<*>?) {
        event?.let {
            if ("close" == event.eventName) {
                poll()
            } else if ("showLoading" == event.eventName) {
                mContainer.showLoading(event.eventMessage)
            } else if ("hideLoading" == event.eventName) {
                mContainer.hideLoading()
            } else if ("showTitleBar" == event.eventName) {
                val data = event.eventData
                if (data == null || data == true) {
                    mRootLayout.topLayout.layout.visibility = View.VISIBLE
                } else {
                    mRootLayout.topLayout.layout.visibility = View.GONE
                }
            } else if (AppConstant.EVENT_CLOSE_PAGE == event.eventCode) {
                poll()
            } else {
                super.onReceiveEvent(event)
            }
        }
    }

    fun isVisible(): Boolean = visible

}