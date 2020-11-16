package com.zpf.myplayer.projects.video

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.zpf.common.base.BaseViewProcessor
import com.zpf.common.player.view.IjkPlayerView
import com.zpf.frame.ITitleBar
import com.zpf.myplayer.R
import com.zpf.myplayer.model.VideoBaseInfo
import com.zpf.support.constant.AppConst
import tv.danmaku.ijk.media.player.IjkMediaPlayer

class TVideoLayout : BaseViewProcessor<Any>() {

    private val ijkPlayer: IjkPlayerView = `$`(R.id.ijk_player)
    private val videoController: LandVideoController = `$`(R.id.controller)

    override fun getLayoutId(): Int {
        return R.layout.layout_tv_video
    }

    override fun initStatusTextColor() {
        //
    }

    override fun initTitleBar(statusView: View, title: ITitleBar, dartText: Boolean) {
        mRootLayout.topLayout.layout.visibility = View.GONE
        mRootLayout.layout.setBackgroundColor(Color.BLACK)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        IjkMediaPlayer.loadLibrariesOnce(null)
        IjkMediaPlayer.native_profileBegin("libijksdl.so")
        val videoInfo = params.getParcelable<VideoBaseInfo>(AppConst.INTENT_KEY)
        ijkPlayer.setController(videoController)
//        ijkPlayer.setVideoPath("http://192.168.1.108:8080/live/livestream.m3u8")
        ijkPlayer.setVideoPath(videoInfo?.path)
        ijkPlayer.start()
        videoController.realClickListener = safeClickListener
    }

    override fun onStop() {
        super.onStop()
        ijkPlayer.pause()
    }

    override fun onDestroy() {
        ijkPlayer.release(true)
        IjkMediaPlayer.native_profileEnd()
        super.onDestroy()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.icon_back -> {
                poll()
            }
            R.id.tv_barrage -> {


            }
//            R.id.icon_play -> {
//                if (ijkPlayer.isPlaying) {
//                    ijkPlayer.pause()
//                } else if (ijkPlayer.isPaused) {
//                    ijkPlayer.start()
//                }
//            }
        }
    }
}