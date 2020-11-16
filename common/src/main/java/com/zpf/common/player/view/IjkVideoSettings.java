package com.zpf.common.player.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.zpf.tool.config.GlobalConfigImpl;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IjkVideoSettings {
    private SharedPreferences mSharedPreferences;

    public static final int PV_PLAYER__Auto = 0;
    public static final int PV_PLAYER__AndroidMediaPlayer = 1;
    public static final int PV_PLAYER__IjkMediaPlayer = 2;
    public static final int PV_PLAYER__IjkExoMediaPlayer = 3;

    public IjkVideoSettings(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean getEnableBackgroundPlay() {
        return mSharedPreferences.getBoolean("key", false);
    }

    public int getPlayer() {
        String value = mSharedPreferences.getString("key", "");
        try {
            return Integer.valueOf(value).intValue();
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public boolean getUsingMediaCodec() {
        return mSharedPreferences.getBoolean("key", false);
    }

    public boolean getUsingMediaCodecAutoRotate() {
        return mSharedPreferences.getBoolean("key", false);
    }

    public boolean getMediaCodecHandleResolutionChange() {
        return mSharedPreferences.getBoolean("key", false);
    }

    public boolean getUsingOpenSLES() {
        return mSharedPreferences.getBoolean("key", false);
    }

    public String getPixelFormat() {
        return mSharedPreferences.getString("key", "");
    }

    public boolean getEnableDetachedSurfaceTextureView() {
        return true;
    }


    public IjkMediaPlayer createPlayerWithSettings() {
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        if (GlobalConfigImpl.get().isDebug()) {
            ijkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
        } else {
            ijkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_ERROR);
        }
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);//处理同域名下http与https切换请求不到的问题
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 102400);//预加载100k，画面加载更快
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);//优化进度条关键帧跳动问题
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1000L);//设置播放前的最大探测时间
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1);//每处理一个packet之后刷新io上下文
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "fastseek");//解决m3u8文件拖动加载太慢问题
        if (getUsingMediaCodec()) {
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
            if (getUsingMediaCodecAutoRotate()) {
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
            } else {
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);
            }
            if (getMediaCodecHandleResolutionChange()) {
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
            } else {
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 0);
            }
        } else {
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
        }

        if (getUsingOpenSLES()) {
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
        } else {
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
        }
        String pixelFormat = getPixelFormat();
        if (TextUtils.isEmpty(pixelFormat)) {
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
        } else {
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", pixelFormat);
        }
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
        return ijkMediaPlayer;
    }

}
