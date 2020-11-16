package com.zpf.common.player.util;

import android.view.View;
import android.widget.MediaController;

import androidx.annotation.Nullable;

import com.zpf.support.util.LogUtil;

public class SimpleIjkController implements IjkPlayerController {
    protected MediaController.MediaPlayerControl player;
    private Runnable waitHideRunnable = new Runnable() {
        @Override
        public void run() {
            if (player != null && player.isPlaying()) {
                controllerView.setVisibility(View.GONE);
            }
        }
    };
    private View controllerView;
    private float lastProgress = 0f;
    private long lastProgressTime = 0;
    private volatile boolean loading = false;

    private Runnable listenerRunnable = new Runnable() {
        @Override
        public void run() {
            if (player == null || !checkCurrentProgress() || !player.isPlaying()) {
                progressListener.stopPlay();
            }
        }
    };
    private PlayerProgressListener progressListener = new PlayerProgressListener(new Runnable() {
        @Override
        public void run() {
            controllerView.post(listenerRunnable);
        }
    }, 20, 800);

    public SimpleIjkController(View controllerView) {
        this.controllerView = controllerView;
    }

    @Override
    public void bindPlayer(@Nullable MediaController.MediaPlayerControl player) {
        this.player = player;
        lastProgress = 0;
        lastProgressTime = 0L;
    }

    @Override
    public void onPause() {
        showControllerView(false);
        checkCurrentProgress();
        if (loading) {
            loading = false;
            onLoadingChange(loading);
        }
    }

    @Override
    public void onPlaying() {
        controllerView.setVisibility(View.GONE);
        controllerView.removeCallbacks(waitHideRunnable);
        if (checkCurrentProgress()) {
            progressListener.startPlay();
        }
    }

    @Override
    public void onComplete() {
        showControllerView(false);
        onProgressChange(1f);
    }

    @Override
    public void onError(int framework_err, int impl_err) {
        showControllerView(false);
        if (loading) {
            loading = false;
            onLoadingChange(loading);
        }
        LogUtil.e("onError==>framework_err=" + framework_err + ";impl_err=" + impl_err);
    }

    @Override
    public void onTouchContent() {
        showControllerView(player != null);
    }

    @Override
    public void onOrientationChange() {

    }

    @Override
    public void onSizeChange(int width, int height, int sarNum, int sarDen) {

    }

    public void showControllerView(boolean autoHide) {
        controllerView.setVisibility(View.VISIBLE);
        controllerView.removeCallbacks(waitHideRunnable);
        if (autoHide) {
            controllerView.postDelayed(waitHideRunnable, 2000);
        }
    }

    public void onLoadingChange(boolean load) {

    }

    //np不大于1.0
    public void onProgressChange(float np) {

    }

    protected boolean checkCurrentProgress() {
        if (player != null && (player.canSeekForward() || player.canSeekBackward())) {
            final float p = player.getCurrentPosition() * 1f / player.getDuration();
            if (p != lastProgress) {
                lastProgress = p;
                onProgressChange(p);
                if (loading) {
                    loading = false;
                    onLoadingChange(loading);
                }
            } else if (lastProgressTime == 0) {
                lastProgressTime = System.currentTimeMillis();
            } else if (System.currentTimeMillis() - lastProgressTime >= 1000) {
                if (!loading && player.isPlaying()) {
                    loading = true;
                    onLoadingChange(loading);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public View getControllerView() {
        return controllerView;
    }
}
