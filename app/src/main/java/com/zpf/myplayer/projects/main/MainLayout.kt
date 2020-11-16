package com.zpf.myplayer.projects.main

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import com.zpf.common.base.BaseViewProcessor
import com.zpf.common.util.getColor
import com.zpf.common.util.getString
import com.zpf.frame.ITitleBar
import com.zpf.myplayer.R
import com.zpf.myplayer.projects.main.home.HomeLayout
import com.zpf.myplayer.projects.main.list.VideoListLayout
import com.zpf.myplayer.projects.main.mine.MineLayout
import com.zpf.support.base.CompatContainerFragment
import com.zpf.support.constant.AppConst
import com.zpf.support.util.FragmentHelper
import com.zpf.tool.ToastUtil
import com.zpf.tool.compat.fragment.CompatFragmentManager

class MainLayout : BaseViewProcessor<Any>() {
    private val tabTextArray = arrayListOf<TextView>(
        `$`(R.id.tv_first), `$`(R.id.tv_second), `$`(R.id.tv_third)
    )
    private var lastClickBack: Long = 0
    private var currentTabId = -1
    private var targetTabId = -1
    private val manage: CompatFragmentManager by lazy {
        CompatFragmentManager(
            FragmentHelper.getComptChildManager(mContainer),
            R.id.fragment_container, arrayOf("home", "list", "mine")
        ) {
            var target: Class<*>? = null
            when (it) {
                0 -> target = HomeLayout::class.java
                1 -> target = VideoListLayout::class.java
                2 -> target = MineLayout::class.java
            }
            if (target != null) {
                val bundle = Bundle()
                bundle.putSerializable(AppConst.TARGET_VIEW_CLASS, target)
                bundle.putSerializable(
                    AppConst.TARGET_CONTAINER_CLASS,
                    CompatContainerFragment::class.java
                )
                FragmentHelper.createCompatFragment(bundle)
            } else {
                null
            }
        }
    }

    override fun initTitleBar(statusView: View, title: ITitleBar, dartText: Boolean) {
        mRootLayout.topLayout.layout.visibility = View.GONE
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind<View>(R.id.rl_tab_first)
        bind<View>(R.id.rl_tab_second)
        bind<View>(R.id.rl_tab_third)
        selectTab(0)
    }

    override fun onInterceptBackPress(): Boolean {
        val dT = System.currentTimeMillis() - lastClickBack
        if (dT > 200) {
            if (dT < 2000) {
                mContainer.finish()
            } else {
                lastClickBack = System.currentTimeMillis()
                ToastUtil.toast(getString(R.string.click_again_exit))
            }
        }
        return true
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.rl_tab_first -> {
                selectTab(0)
            }
            R.id.rl_tab_second -> {
                selectTab(1)
            }
            R.id.rl_tab_third -> {
                selectTab(2)
            }
        }
    }

    private fun selectTab(index: Int) {
        if (index >= 0 && index < tabTextArray.size && index != currentTabId) {
            tabTextArray.forEachIndexed { i, item ->
                if (index == i) {
                    item.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
                    item.setTextColor(getColor(R.color.color_304FFE))
                    manage.showFragment(i)
                } else {
                    item.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
                    item.setTextColor(getColor(R.color.color_666666))
                }
            }
            currentTabId = index
            targetTabId = -1;
        }
    }
}