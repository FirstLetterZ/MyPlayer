package com.zpf.common.player.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;

import androidx.annotation.NonNull;

import com.zpf.common.player.util.FileMediaDataSource;
import com.zpf.common.player.util.IRenderView;
import com.zpf.common.player.util.IjkPlayerController;
import com.zpf.common.player.util.IjkPlayerState;
import com.zpf.common.player.util.MediaPlayerService;
import com.zpf.support.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tv.danmaku.ijk.media.exo.IjkExoMediaPlayer;
import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.TextureMediaPlayer;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;

@SuppressLint("ObsoleteSdkInt")
public class IjkPlayerView extends FrameLayout implements MediaController.MediaPlayerControl {
    private Uri mUri;
    private Map<String, String> mHeaders;

    private int mCurrentState = IjkPlayerState.STATE_IDLE;
    private int mTargetState = IjkPlayerState.STATE_IDLE;

    // All the stuff we need for playing and showing a video
    private IRenderView.ISurfaceHolder mSurfaceHolder = null;
    private int mVideoWidth;//视频宽度
    private int mVideoHeight;//视频高度
    private int mSurfaceWidth;//窗口宽度
    private int mSurfaceHeight;//窗口高度
    private int mVideoRotationDegree;//视频旋转角度

    private IMediaPlayer mMediaPlayer = null;//媒体播放器
    private IjkPlayerController mMediaController;//媒体控制器
    private IMediaPlayer.OnCompletionListener mOnCompletionListener;//播放完成监听
    private IMediaPlayer.OnPreparedListener mOnPreparedListener;//播放准备监听
    private int mCurrentBufferPercentage;//播放缓冲字节长度
    private IMediaPlayer.OnErrorListener mOnErrorListener;//播放错误监听
    private IMediaPlayer.OnInfoListener mOnInfoListener;//播放其他信息监听

    private int mSeekWhenPrepared;  // recording the seek position while preparing 记录寻求位置而做准备
    private IjkVideoSettings mSettings = new IjkVideoSettings(getContext());//全局设置

    private Context mAppContext;
    private int mVideoSarNum;
    private int mVideoSarDen;

    private boolean mEnableBackgroundPlay = false;

    public static final int RENDER_NONE = 0;
    public static final int RENDER_SURFACE_VIEW = 1;
    public static final int RENDER_TEXTURE_VIEW = 2;

    private IRenderView mRenderView;//渲染组件
    private List<Integer> mAllRenders = new ArrayList<>();
    private int mCurrentRenderIndex = 0;
    private int mCurrentRender = RENDER_NONE;
    //画面比例控制参数
    private static final int[] s_allAspectRatio = {
            IRenderView.AR_ASPECT_FIT_PARENT,
            IRenderView.AR_ASPECT_FILL_PARENT,
            IRenderView.AR_ASPECT_WRAP_CONTENT,
            IRenderView.AR_MATCH_PARENT,
            IRenderView.AR_16_9_FIT_PARENT,
            IRenderView.AR_4_3_FIT_PARENT};
    private int mCurrentAspectRatio = s_allAspectRatio[1];

    public IjkPlayerView(Context context) {
        super(context);
        initVideoView(context);
    }

    public IjkPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVideoView(context);
    }

    public IjkPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IjkPlayerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initVideoView(context);
    }

    private void initVideoView(Context context) {
        mAppContext = context.getApplicationContext();
        initBackground();
        initRenders();
        mVideoWidth = 0;
        mVideoHeight = 0;
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        mCurrentState = IjkPlayerState.STATE_IDLE;
        mTargetState = IjkPlayerState.STATE_IDLE;
    }


    private void initPlayerConfig() {
        try {
            mMediaPlayer = createPlayer(mSettings.getPlayer());
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mCurrentBufferPercentage = 0;
            bindSurfaceHolder(mMediaPlayer, mSurfaceHolder);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            if (mMediaController != null) {
                mMediaController.bindPlayer(this);
            }
        } catch (Exception ex) {
            mCurrentState = IjkPlayerState.STATE_ERROR;
            mTargetState = IjkPlayerState.STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        }
    }

    private IMediaPlayer createPlayer(int playerType) {
        IMediaPlayer mediaPlayer;
        switch (playerType) {
            case IjkVideoSettings.PV_PLAYER__IjkExoMediaPlayer: {
                mediaPlayer = new IjkExoMediaPlayer(mAppContext);
            }
            break;
            case IjkVideoSettings.PV_PLAYER__AndroidMediaPlayer: {
                mediaPlayer = new AndroidMediaPlayer();
            }
            break;
            case IjkVideoSettings.PV_PLAYER__IjkMediaPlayer:
            default: {
                mediaPlayer = mSettings.createPlayerWithSettings();
            }
            break;
        }
        if (mSettings.getEnableDetachedSurfaceTextureView()) {
            mediaPlayer = new TextureMediaPlayer(mediaPlayer);
        }
        return mediaPlayer;
    }

    private void initRenders() {
        mAllRenders.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mAllRenders.add(RENDER_TEXTURE_VIEW);
        }
        mAllRenders.add(RENDER_SURFACE_VIEW);
        mAllRenders.add(RENDER_NONE);
        mCurrentRender = mAllRenders.get(mCurrentRenderIndex);
        setRender(mCurrentRender);
    }

    private void initBackground() {
        mEnableBackgroundPlay = mSettings.getEnableBackgroundPlay();
        if (mEnableBackgroundPlay) {
            MediaPlayerService.intentToStart(getContext());
            mMediaPlayer = MediaPlayerService.getMediaPlayer();
        }
    }

    private void prepareAudioManager() {
        AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    private void stopAudioManager() {
        AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(null);
    }

    private void prepareVideo() {
        if (mUri == null || mSurfaceHolder == null) {
            // not ready for playback just yet, will try again later
            return;
        }
        if (mMediaPlayer == null) {
            initPlayerConfig();
        } else if (mMediaPlayer.getDataSource() != null) {
            initPlayerConfig();
        }
        if (mMediaPlayer == null) {
            mCurrentState = IjkPlayerState.STATE_ERROR;
            mTargetState = IjkPlayerState.STATE_ERROR;
            mErrorListener.onError(null, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        }
        prepareAudioManager();
        try {
            mCurrentBufferPercentage = 0;
            String scheme = mUri.getScheme();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    (TextUtils.isEmpty(scheme) || "file".equalsIgnoreCase(scheme))) {
                IMediaDataSource dataSource = new FileMediaDataSource(new File(mUri.toString()));
                mMediaPlayer.setDataSource(dataSource);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mMediaPlayer.setDataSource(mAppContext, mUri, mHeaders);
            } else {
                mMediaPlayer.setDataSource(mUri.toString());
            }
            mMediaPlayer.prepareAsync();
            mCurrentState = IjkPlayerState.STATE_PREPARING;
        } catch (IOException e) {
            mCurrentState = IjkPlayerState.STATE_ERROR;
            mTargetState = IjkPlayerState.STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        }
    }

    public void setController(IjkPlayerController controller) {
        if (mMediaController != null) {
            mMediaController.bindPlayer(null);
        }
        if (controller != null) {
            controller.bindPlayer(this);
        }
        mMediaController = controller;
    }

    private IMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener =
            new IMediaPlayer.OnVideoSizeChangedListener() {
                public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
                    mVideoWidth = mp.getVideoWidth();
                    mVideoHeight = mp.getVideoHeight();
                    mVideoSarNum = mp.getVideoSarNum();
                    mVideoSarDen = mp.getVideoSarDen();
                    if (mVideoWidth != 0 && mVideoHeight != 0) {
                        if (mRenderView != null) {
                            mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                            mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                        }
                        if (mMediaController != null) {
                            mMediaController.onSizeChange(mVideoWidth, mVideoHeight, mVideoSarNum, mVideoSarDen);
                        }
                        requestLayout();
                    }
                }
            };

    private IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {
        public void onPrepared(IMediaPlayer mp) {
            mCurrentState = IjkPlayerState.STATE_PREPARED;
            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);
            }
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            int seekToPosition = mSeekWhenPrepared;  // mSeekWhenPrepared may be changed after seekTo() call
            if (seekToPosition != 0) {
                seekTo(seekToPosition);
            }
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                if (mRenderView != null) {
                    mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                    mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                    if (!mRenderView.shouldWaitForResize() || mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
                        // We didn't actually change the size (it was already at the size
                        // we need), so we won't get a "surface changed" callback, so
                        // start the video here instead of in the callback.
                        if (mTargetState == IjkPlayerState.STATE_PLAYING) {
                            start();
                        } else if (!isPlaying() && (seekToPosition != 0 || getCurrentPosition() > 0)) {
                            if (mMediaController != null) {
                                // Show the media controls when we're paused into a video and make 'em stick.
                                mMediaController.onPlaying();
                            }
                        }
                    }
                }
            } else {
                // We don't know the video size yet, but should start anyway.
                // The video size might be reported to us later.
                if (mTargetState == IjkPlayerState.STATE_PLAYING) {
                    start();
                }
            }
        }
    };

    private IMediaPlayer.OnCompletionListener mCompletionListener =
            new IMediaPlayer.OnCompletionListener() {
                public void onCompletion(IMediaPlayer mp) {
                    mCurrentState = IjkPlayerState.STATE_PLAYBACK_COMPLETED;
                    mTargetState = IjkPlayerState.STATE_PLAYBACK_COMPLETED;
                    if (mMediaController != null) {
                        mMediaController.onComplete();
                    }
                    if (mOnCompletionListener != null) {
                        mOnCompletionListener.onCompletion(mMediaPlayer);
                    }
                }
            };

    private IMediaPlayer.OnInfoListener mInfoListener =
            new IMediaPlayer.OnInfoListener() {
                public boolean onInfo(IMediaPlayer mp, int arg1, int arg2) {
                    if (arg1 == IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED) {
                        mVideoRotationDegree = arg2;
                        if (mRenderView != null) {
                            mRenderView.setVideoRotation(arg2);
                        }
                    }
                    if (mOnInfoListener != null) {
                        mOnInfoListener.onInfo(mp, arg1, arg2);
                    }
                    return true;
                }
            };

    private IMediaPlayer.OnErrorListener mErrorListener =
            new IMediaPlayer.OnErrorListener() {
                public boolean onError(IMediaPlayer mp, int framework_err, int impl_err) {
                    mCurrentState = IjkPlayerState.STATE_ERROR;
                    mTargetState = IjkPlayerState.STATE_ERROR;
                    if (mMediaController != null) {
                        mMediaController.onError(framework_err, impl_err);
                    }
                    if (mOnErrorListener != null) {
                        if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
                            return true;
                        }
                    }
                    if (getWindowToken() != null) {
                        //TODO 默认的报错弹窗
                    }
                    return true;
                }
            };

    private IMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
            new IMediaPlayer.OnBufferingUpdateListener() {
                public void onBufferingUpdate(IMediaPlayer mp, int percent) {
                    mCurrentBufferPercentage = percent;
                }
            };

    private void bindSurfaceHolder(IMediaPlayer mp, IRenderView.ISurfaceHolder holder) {
        if (mp == null)
            return;
        if (holder == null) {
            mp.setDisplay(null);
            return;
        }
        holder.bindToMediaPlayer(mp);
    }

    IRenderView.IRenderCallback mSHCallback = new IRenderView.IRenderCallback() {
        @Override
        public void onSurfaceChanged(@NonNull IRenderView.ISurfaceHolder holder, int format, int w, int h) {
            if (holder.getRenderView() != mRenderView) {
                return;
            }
            mSurfaceWidth = w;
            mSurfaceHeight = h;
            boolean isValidState = (mTargetState == IjkPlayerState.STATE_PLAYING);
            boolean hasValidSize = !mRenderView.shouldWaitForResize() || (mVideoWidth == w && mVideoHeight == h);
            if (mMediaPlayer != null && isValidState && hasValidSize) {
                if (mSeekWhenPrepared != 0) {
                    seekTo(mSeekWhenPrepared);
                }
                start();
            }
        }

        @Override
        public void onSurfaceCreated(@NonNull IRenderView.ISurfaceHolder holder, int width, int height) {
            if (holder.getRenderView() != mRenderView) {
                return;
            }

            mSurfaceHolder = holder;
            if (mMediaPlayer != null)
                bindSurfaceHolder(mMediaPlayer, holder);
            else
                prepareVideo();
        }

        @Override
        public void onSurfaceDestroyed(@NonNull IRenderView.ISurfaceHolder holder) {
            if (holder.getRenderView() != mRenderView) {
                return;
            }
            LogUtil.e("onSurfaceDestroyed");
            // after we return from this we can't use the surface any more
            mSurfaceHolder = null;
            releaseWithoutStop();
        }
    };

    public void releaseWithoutStop() {
        if (mMediaPlayer != null)
            mMediaPlayer.setDisplay(null);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isInPlaybackState() && ev.getAction() == MotionEvent.ACTION_DOWN && mMediaController != null) {
            mMediaController.onTouchContent();
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isInPlaybackState() && mMediaController != null) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK ||
                    keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                if (mMediaPlayer.isPlaying()) {
                    pause();
                } else {
                    start();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                if (!mMediaPlayer.isPlaying()) {
                    start();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                    || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                if (mMediaPlayer.isPlaying()) {
                    pause();
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private volatile boolean lockStart = false;

    @Override
    public void start() {
        if (isInPlaybackState() && !lockStart) {
            if (mCurrentState == IjkPlayerState.STATE_PLAYBACK_COMPLETED && getCurrentPosition() > 1) {
                seekTo(1);
                lockStart = true;
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lockStart = false;
                        LogUtil.w("CurrentPosition=" + getCurrentPosition());
                        start();
                    }
                }, 100);
                return;
            } else {
                prepareAudioManager();
            }
            mMediaPlayer.start();
            mCurrentState = IjkPlayerState.STATE_PLAYING;
            if (mMediaController != null) {
                mMediaController.onPlaying();
            }
        }
        mTargetState = IjkPlayerState.STATE_PLAYING;
    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentState = IjkPlayerState.STATE_PAUSED;
                if (mMediaController != null) {
                    mMediaController.onPause();
                }
            }
        }
        mTargetState = IjkPlayerState.STATE_PAUSED;
    }

    public boolean isPaused() {
        return mCurrentState == IjkPlayerState.STATE_PAUSED;
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mCurrentState = IjkPlayerState.STATE_IDLE;
            mTargetState = IjkPlayerState.STATE_IDLE;
            stopAudioManager();
            if (mMediaController != null) {
                mMediaController.onPause();
            }
        }
    }

    /*
     * release the media player in any state
     */
    public void release(boolean cleartargetstate) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            // REMOVED: mPendingSubtitleTracks.clear();
            mCurrentState = IjkPlayerState.STATE_IDLE;
            if (cleartargetstate) {
                mTargetState = IjkPlayerState.STATE_IDLE;
            }
            stopAudioManager();
        }
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getDuration();
        }

        return -1;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int msec) {
        if (isInPlaybackState()) {
            mMediaPlayer.seekTo(msec);
            mSeekWhenPrepared = 0;
            if (mCurrentState == IjkPlayerState.STATE_PLAYBACK_COMPLETED) {
                mCurrentState = IjkPlayerState.STATE_PAUSED;
                mTargetState = IjkPlayerState.STATE_PAUSED;
            }
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    private boolean isInPlaybackState() {
        return (mMediaPlayer != null &&
                mCurrentState != IjkPlayerState.STATE_ERROR &&
                mCurrentState != IjkPlayerState.STATE_IDLE &&
                mCurrentState != IjkPlayerState.STATE_PREPARING);
    }

    @Override
    public boolean canPause() {
        return isInPlaybackState() && mMediaPlayer.getDuration() > 0;
    }

    @Override
    public boolean canSeekBackward() {
        return isInPlaybackState() && mMediaPlayer.getDuration() > 0
                && mMediaPlayer.getCurrentPosition() < mMediaPlayer.getDuration();
    }

    @Override
    public boolean canSeekForward() {
        return isInPlaybackState() && mMediaPlayer.getDuration() > 0
                && mMediaPlayer.getCurrentPosition() > 0;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    //切换画面比例
    public int setAspectRatio(int aspectRatio) {
        mCurrentAspectRatio = aspectRatio % s_allAspectRatio.length;
        if (mRenderView != null) {
            mRenderView.setAspectRatio(mCurrentAspectRatio);
        }
        return mCurrentAspectRatio;
    }

    //切换渲染组件
    public int toggleRender() {
        mCurrentRenderIndex++;
        mCurrentRenderIndex %= mAllRenders.size();
        mCurrentRender = mAllRenders.get(mCurrentRenderIndex);
        setRender(mCurrentRender);
        return mCurrentRender;
    }

    //切换播放组件
    public int togglePlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        if (mRenderView != null) {
            mRenderView.getView().invalidate();
        }
        prepareVideo();
        return mSettings.getPlayer();
    }

    public void setRenderView(IRenderView renderView) {
        if (mRenderView != null) {
            if (mMediaPlayer != null) {
                mMediaPlayer.setDisplay(null);
            }
            View renderUIView = mRenderView.getView();
            mRenderView.removeRenderCallback(mSHCallback);
            mRenderView = null;
            removeView(renderUIView);
        }
        if (renderView == null)
            return;
        mRenderView = renderView;
        renderView.setAspectRatio(mCurrentAspectRatio);
        if (mVideoWidth > 0 && mVideoHeight > 0)
            renderView.setVideoSize(mVideoWidth, mVideoHeight);
        if (mVideoSarNum > 0 && mVideoSarDen > 0)
            renderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);

        View renderUIView = mRenderView.getView();
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        renderUIView.setLayoutParams(lp);
        addView(renderUIView);

        mRenderView.addRenderCallback(mSHCallback);
        mRenderView.setVideoRotation(mVideoRotationDegree);
    }

    public void setRender(int render) {
        switch (render) {
            case RENDER_NONE:
                setRenderView(null);
                break;
            case RENDER_TEXTURE_VIEW: {
                TextureRenderView renderView = new TextureRenderView(getContext());
                if (mMediaPlayer != null) {
                    renderView.getSurfaceHolder().bindToMediaPlayer(mMediaPlayer);
                    renderView.setVideoSize(mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight());
                    renderView.setVideoSampleAspectRatio(mMediaPlayer.getVideoSarNum(), mMediaPlayer.getVideoSarDen());
                    renderView.setAspectRatio(mCurrentAspectRatio);
                }
                setRenderView(renderView);
                break;
            }
            case RENDER_SURFACE_VIEW: {
                SurfaceRenderView renderView = new SurfaceRenderView(getContext());
                setRenderView(renderView);
                break;
            }
            default:
                break;
        }
    }

    public void setVideoPath(String path) {
        Uri uri = null;
        try {
            uri = Uri.parse(path);
        } catch (Exception e) {
            //
        }
        setVideoURI(uri);
    }

    public void setVideoURI(Uri uri) {
        setVideoURI(uri, null);
    }

    public void setVideoURI(Uri uri, Map<String, String> headers) {
        mUri = uri;
        mHeaders = headers;
        mSeekWhenPrepared = 0;
        release(false);
        prepareVideo();
        requestLayout();
    }

    public Uri getCurrentUri() {
        return mUri;
    }

    public Bitmap getShortcut() {
        return this.mRenderView instanceof TextureRenderView ? ((TextureRenderView) this.mRenderView).getBitmap() : null;
    }

    public boolean isBackgroundPlayEnabled() {
        return mEnableBackgroundPlay;
    }

    public void enterBackground() {
        if (mEnableBackgroundPlay) {
            MediaPlayerService.setMediaPlayer(mMediaPlayer);
        }
    }

    public void stopBackgroundPlay() {
        MediaPlayerService.setMediaPlayer(null);
    }

    public void setOnPreparedListener(IMediaPlayer.OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    public void setOnCompletionListener(IMediaPlayer.OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    public void setOnErrorListener(IMediaPlayer.OnErrorListener l) {
        mOnErrorListener = l;
    }

    public void setOnInfoListener(IMediaPlayer.OnInfoListener l) {
        mOnInfoListener = l;
    }
}
