package com.zpf.myplayer.projects.main.list

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zpf.api.ItemViewCreator
import com.zpf.common.base.BaseViewProcessor
import com.zpf.frame.ITitleBar
import com.zpf.myplayer.R
import com.zpf.myplayer.model.VideoBaseInfo
import com.zpf.myplayer.projects.TestLandLayout
import com.zpf.myplayer.projects.video.TVideoLayout
import com.zpf.rvexpand.RecyclerViewAdapter
import com.zpf.support.constant.AppConst

class VideoListLayout : BaseViewProcessor<Any>() {
    private val rvContent: RecyclerView = `$`(R.id.rv_list)
    private val layoutManager = GridLayoutManager(context, 4)
    private val adapter = RecyclerViewAdapter<VideoBaseInfo>()

    override fun getLayoutId(): Int {
        return R.layout.layout_video_list
    }

    override fun initStatusTextColor() {
    }

    override fun initTitleBar(statusView: View, title: ITitleBar, dartText: Boolean) {
        mRootLayout.topLayout.layout.visibility = View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rvContent.layoutManager = layoutManager
        adapter.setItemViewCreator(object : ItemViewCreator {
            override fun onBindView(view: View?, position: Int) {
                (view as? Button)?.text = adapter.getPositionData(position)?.name
            }

            override fun onCreateView(context: Context, type: Int): View {
                return Button(context)
            }
        }).setItemClickListener {
            adapter.getPositionData(it)?.run {
                val p = Bundle()
                p.putParcelable(AppConst.INTENT_KEY, this)
                p.putInt(
                    AppConst.TARGET_VIEW_ORIENTATION,
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                )
//                push(TestLandLayout::class.java, p)
                push(TVideoLayout::class.java, p)
            }
        }
        adapter.addData(VideoBaseInfo("凤凰卫视资讯台", "http://liveali.ifeng.com/live/FHZX.flv"))
        rvContent.adapter = adapter
    }
}

