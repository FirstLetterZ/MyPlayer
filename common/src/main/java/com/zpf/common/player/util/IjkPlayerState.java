package com.zpf.common.player.util;

import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@IntDef(value = {
        IjkPlayerState.STATE_ERROR,
        IjkPlayerState.STATE_IDLE,
        IjkPlayerState.STATE_PREPARING,
        IjkPlayerState.STATE_PREPARED,
        IjkPlayerState.STATE_PLAYING,
        IjkPlayerState.STATE_PAUSED,
        IjkPlayerState.STATE_PLAYBACK_COMPLETED
})
@Target(value = ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface IjkPlayerState {
    int STATE_ERROR = -1;
    int STATE_IDLE = 0;
    int STATE_PREPARING = 1;
    int STATE_PREPARED = 2;
    int STATE_PLAYING = 3;
    int STATE_PAUSED = 4;
    int STATE_PLAYBACK_COMPLETED = 5;
}
