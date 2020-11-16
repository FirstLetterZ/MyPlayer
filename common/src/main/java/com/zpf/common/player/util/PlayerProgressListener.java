package com.zpf.common.player.util;

import java.util.Timer;
import java.util.TimerTask;

public class PlayerProgressListener {
    private Timer timer;
    private TimerTask timerTask;
    private volatile boolean isRunning = false;
    private Runnable runnable;
    private long delay;
    private long period;

    public PlayerProgressListener(Runnable runnable, long delay, long period) {
        this.delay = delay;
        this.period = period;
        this.runnable = runnable;
    }

    //停止轮播
    public void stopPlay() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        isRunning = false;
    }

    //开始轮播
    public void startPlay() {
        if (timer == null) {
            timer = new Timer();
        }
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            };
        }
        if (isRunning) {
            return;
        }
        isRunning = true;
        try {
            timer.schedule(timerTask, 20, 800);
        } catch (Exception e) {
            isRunning = false;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }
}
