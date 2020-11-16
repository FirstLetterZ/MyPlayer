package com.zpf.myplayer.projects

import android.animation.Animator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.airbnb.lottie.LottieAnimationView
import com.zpf.barrage.bean.DanmakuNetBean
import com.zpf.barrage.bean.DanmakuNetBeanParser
import com.zpf.barrage.util.DataSourceParserUtil
import com.zpf.barrage.view.DanmakuView
import com.zpf.common.base.BaseViewProcessor
import com.zpf.frame.ITitleBar
import com.zpf.myplayer.R
import com.zpf.support.util.LogUtil
import com.zpf.tool.config.MainHandler
import java.util.*

class TestLandLayout : BaseViewProcessor<Any>() {
    private val tvDanmaku: DanmakuView = `$`(R.id.tv_danmaku)
    private val viewAnim: LottieAnimationView = `$`(R.id.anim_view)
    private val files = context.assets.list("Tests")
    private var index = 0
    private val parser = DanmakuNetBeanParser(mContainer.context)
    override fun getLayoutId(): Int {
        return R.layout.layout_test
    }

    override fun initStatusTextColor() {
        //
    }

    override fun initTitleBar(statusView: View, title: ITitleBar, dartText: Boolean) {
        mRootLayout.topLayout.layout.visibility = View.GONE
        mRootLayout.layout.setBackgroundColor(Color.BLACK)
    }

    private val animRunnable = Runnable {
        val path = files?.getOrNull(index)
        if (path == null) {
            index = -1
            viewAnim.clearAnimation()
        } else {
            try {
                LogUtil.w("setAnimation==>>Tests/$path")
                viewAnim.setAnimation("Tests/$path")
                viewAnim.playAnimation()
            } catch (e: Exception) {
                LogUtil.e("OnError==>>Tests/$path ;;$e")
            }
            index++
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataSourceParserUtil.addParser(parser)
        val sa = context.resources.getStringArray(R.array.danmu)
        val dataList = LinkedList<DanmakuNetBean>()
        for (i in 0 until 999) {
            val info = DanmakuNetBean()
            val ys = i % 3
            if (ys == 0) {
                info.fontColors = arrayListOf("#304FFE")
            } else if (ys == 1) {
                info.fontColors = arrayListOf("#FFD600")
                val icon = DanmakuNetBean()
                icon.type = 1
                icon.underLine = true
                icon.bgColor = "#80FFFFFF"
                icon.bgRadius = 24f
                icon.fontColors = arrayListOf("#DD2C00")
                icon.content = sa[i % sa.size]
                dataList.add(icon)
            } else {
                info.fontColors = arrayListOf("#00C853")
            }
            info.startIconPath = "assets://male_adult_avatar.png"
            info.endIconPath = "resource://icon_like_blue"
            info.type = ys + 1
            info.content = "i=$i"
            dataList.add(info)
        }

        viewAnim.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                MainHandler.get().postDelayed(animRunnable, 1000)
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
        tvDanmaku.addDataList(dataList)
    }

    override fun onResume() {
        tvDanmaku.start()
//        if (index >= 0) {
//            MainHandler.get().postDelayed(animRunnable, 1000)
//        }
    }

    override fun onStop() {
        tvDanmaku.pause()
        viewAnim.clearAnimation()
        MainHandler.get().removeCallbacks(animRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        DataSourceParserUtil.removeParser(parser)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.icon_back -> {
                poll()
            }
            R.id.tv_barrage -> {
                val info = DanmakuNetBean()
                info.content = "发条弹幕说点什么✈"
                tvDanmaku.addData(info, false)
            }
        }
    }
}