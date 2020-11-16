package com.zpf.myplayer.projects.launch

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.zpf.myplayer.projects.main.MainActivity
import com.zpf.myplayer.R
import com.zpf.tool.PublicUtil
import com.zpf.tool.TimeCountUtil

class SplashActivity : AppCompatActivity() {

    private var isFirst = true
    private lateinit var tvCount: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        //防止初次安装从后台返回的重启问题
        val isLauncher =
            intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN == intent.action
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0 && isLauncher) {
            finish()
            return
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        tvCount = findViewById(R.id.tv_count)
    }

    override fun onBackPressed() {
    }

    override fun onResume() {
        super.onResume()
        if (isFirst) {
            isFirst = false
            val countUtil = TimeCountUtil(3)
            countUtil.setListener(object : TimeCountUtil.SecondCountListener {
                override fun onTimeFinish() {
                    tvCount.text = "0"
                    jumpToNext()
                }

                override fun onTimeCutDown(second: Long) {
                    tvCount.text = (second + 1).toString()
                }
            })
            countUtil.start()
        }
    }

    private fun jumpToNext() {
        PublicUtil.moveTaskToTop(this)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}