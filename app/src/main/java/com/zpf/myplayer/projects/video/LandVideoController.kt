package com.zpf.myplayer.projects.video

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.MediaController
import android.widget.RelativeLayout
import android.widget.TextView
import com.zpf.common.player.util.IjkPlayerController
import com.zpf.common.player.util.SeekHelper
import com.zpf.myplayer.R
import com.zpf.tool.SafeClickListener
import com.zpf.tool.ViewUtil
import com.zpf.tool.expand.view.IconTextView

class LandVideoController @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), IjkPlayerController, View.OnClickListener {
    private val seekHelper = SeekHelper()
    private var inflated = false
    var realClickListener: View.OnClickListener? = null

    val iconBack: IconTextView = fv(R.id.icon_back)
    val iconMore: IconTextView = fv(R.id.icon_more)
    val tvTitle: TextView = fv(R.id.tv_title)
    val iconPlay: IconTextView = fv(R.id.icon_play)
    val tvCurrent: TextView = fv(R.id.tv_current)
    val tvDuration: TextView = fv(R.id.tv_duration)
    val tvDefinition: TextView = fv(R.id.tv_definition)
    val tvBarrage: TextView = fv(R.id.tv_barrage)
    val iconLock: IconTextView = fv(R.id.icon_lock)

    override fun onFinishInflate() {
        super.onFinishInflate()

    }

    override fun onTouchContent() {

    }

    override fun onSizeChange(width: Int, height: Int, sarNum: Int, sarDen: Int) {

    }

    override fun onOrientationChange() {
    }

    override fun onComplete() {
    }

    override fun bindPlayer(player: MediaController.MediaPlayerControl?) {
    }

    override fun onPause() {
    }

    override fun onError(framework_err: Int, impl_err: Int) {
    }

    override fun onPlaying() {
    }

    private fun <T : View> fv(id: Int): T {
        if (!inflated) {
            LayoutInflater.from(context).inflate(R.layout.land_screen_controller, this, true)
            inflated = true
        }
        val result: T = findViewById(id)
        result.setOnClickListener(this)
        return result
    }

    override fun onClick(v: View?) {
        realClickListener?.onClick(v)
    }
}