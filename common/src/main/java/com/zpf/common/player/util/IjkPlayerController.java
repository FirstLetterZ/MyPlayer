package com.zpf.common.player.util;

import android.widget.MediaController;

import androidx.annotation.Nullable;

public interface IjkPlayerController {

    void bindPlayer(@Nullable MediaController.MediaPlayerControl player);

    void onPause();

    void onPlaying();

    void onComplete();

    void onError(int framework_err, int impl_err);

    void onTouchContent();

    void onOrientationChange();

    void onSizeChange(int width, int height, int sarNum, int sarDen);
}
