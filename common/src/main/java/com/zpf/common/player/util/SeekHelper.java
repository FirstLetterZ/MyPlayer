package com.zpf.common.player.util;

import android.widget.MediaController;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SeekHelper {
    private MediaController.MediaPlayerControl mControl;
    private int nextSeekMsec = 0;
    private int currentSeekMsec = 0;
    private AtomicInteger timeCount = new AtomicInteger(1);
    private PlayerProgressListener progressListener = new PlayerProgressListener(new Runnable() {
        @Override
        public void run() {
            if (mControl == null) {
                progressListener.stopPlay();
                return;
            }
            timeCount.set(timeCount.intValue() % 4);
            if (timeCount.get() <= 0 && currentSeekMsec != nextSeekMsec) {
                currentSeekMsec = nextSeekMsec;
                if (mControl.getCurrentPosition() != currentSeekMsec) {
                    mControl.seekTo(currentSeekMsec);
                }
            }
            if (timeCount.get() < 0) {
                progressListener.stopPlay();
            } else {
                timeCount.set(timeCount.intValue() + 1);
            }
        }
    }, 600, 200);

    public int startDragSeekProgress(float p) {
        if (mControl == null) {
            return -1;
        }
        int msec = (int) (p * mControl.getDuration());
        startDragSeek(msec);
        return msec;
    }

    public void startDragSeek(int msec) {
        if (mControl == null) {
            return;
        }
        progressListener.stopPlay();
        currentSeekMsec = msec;
        nextSeekMsec = msec;
        mControl.seekTo(msec);
        if (!progressListener.isRunning()) {
            timeCount.set(1);
            progressListener.startPlay();
        }
    }

    public int endDragSeekProgress(float p) {
        if (mControl == null) {
            return -1;
        }
        int msec = (int) (p * mControl.getDuration());
        endDragSeekMsec(msec);
        return msec;
    }

    public void endDragSeekMsec(int msec) {
        if (mControl == null) {
            return;
        }
        timeCount.set(-1);
    }

    public int onDraggingSeekProgress(float p) {
        if (mControl == null) {
            return -1;
        }
        int msec = (int) (p * mControl.getDuration());
        onDraggingSeekMsec(msec);
        return msec;
    }

    public void onDraggingSeekMsec(int msec) {
        nextSeekMsec = msec;
    }

    public void bindController(MediaController.MediaPlayerControl c) {
        mControl = c;
    }

    public void release() {
        progressListener.stopPlay();
        mControl = null;
        currentSeekMsec = 0;
        nextSeekMsec = 0;
    }


    public static void main(String[] args) {

        File file = new File("/Users/tbtx/work/schedule.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);

            List<String> actions = Arrays.asList("riding", "running", "swimming");
            List<String> userNames = Arrays.asList("lily", "tom", "bob");
            List<String> times = Arrays.asList("2018-2-2", "2018-3-3");

            for (int i = 0; i < 1000; i++) {
                for (String username : userNames) {
                    for (String action : actions) {
                        for (String time : times) {
                            String request = username + " is " + action + " on " + time + "\r\n";
                            bw.write(request);
                        }
                    }
                }
            }
            bw.flush();
            bw.close();
            fw.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("Write is over");
    }

}
