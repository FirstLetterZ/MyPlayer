package com.zpf.barrage.util;

import android.view.MotionEvent;

import com.zpf.barrage.drawer.BaseDrawer;
import com.zpf.barrage.interfaces.OnItemLinkClickListener;

import java.util.LinkedList;

public class DanmakuItemClickHelper {
    private String clickLink;
    private long downTime = 0L;
    private float downX = 0f;
    private float downY = 0f;
    private LinkedList<BaseDrawer> drawers;
    private OnItemLinkClickListener listener;

    public boolean interceptTouchEvent(MotionEvent event) {
        if (listener == null || drawers == null || drawers.size() == 0) {
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = event.getRawX();
            downY = event.getRawY();
            downTime = System.currentTimeMillis();
            for (int i = 0; i < drawers.size(); i++) {
                clickLink = drawers.get(i).checkClickLink(downX, downY);
                if (clickLink != null) {
                    return true;
                }
            }
            downTime = 0L;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (Math.abs(downX - event.getRawX()) > 8 || Math.abs(downY - event.getRawY()) > 8) {
                downTime = 0L;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (System.currentTimeMillis() - downTime < 800) {
                listener.onClick(clickLink);
                return true;
            }
        } else {
            downTime = 0L;
        }
        return false;
    }

    public void setDrawers(LinkedList<BaseDrawer> drawers) {
        this.drawers = drawers;
    }

    public void setClickListener(OnItemLinkClickListener listener) {
        this.listener = listener;
    }
}
