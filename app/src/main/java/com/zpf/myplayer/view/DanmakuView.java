package com.zpf.myplayer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.zpf.myplayer.view.bean.DanmakuNetBean;
import com.zpf.myplayer.view.drawer.BaseDrawer;
import com.zpf.myplayer.view.drawer.BottomDrawer;
import com.zpf.myplayer.view.drawer.DrawerSetting;
import com.zpf.myplayer.view.drawer.RollDrawer;
import com.zpf.myplayer.view.drawer.TopDrawer;
import com.zpf.tool.ToastUtil;

import java.util.LinkedList;
import java.util.List;

public class DanmakuView extends View {
    private LinkedList<RollDrawer> rollDrawerList = new LinkedList<>();
    private TopDrawer topDrawer;
    private BottomDrawer bottomDrawer;
    private DrawInfoDispatcher drawInfoDispatcher = new DrawInfoDispatcher(getContext());
    private int rollLineCount = 3;
    private int topLineCount = 3;
    private int bottomLineCount = 3;
    private boolean playing = false;
    private String clickLink;
    private long downTime = 0L;
    private float downX = 0f;
    private float downY = 0f;

    public DanmakuView(Context context) {
        super(context);
        initConfig();
    }

    public DanmakuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initConfig();
    }

    public DanmakuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initConfig();
    }

    private void initConfig() {
        DrawerSetting.DEF_INSIDE_SPACE = 4 * getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            initDrawer(left, top, right, bottom);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = event.getRawX();
            downY = event.getRawY();
            downTime = System.currentTimeMillis();
            for (int i = 0; i < rollDrawerList.size(); i++) {
                clickLink = rollDrawerList.get(i).checkClickLink(downX, downY);
                if (clickLink != null) {
                    return true;
                }
            }
            if (topDrawer != null) {
                clickLink = topDrawer.checkClickLink(downX, downY);
            }
            if (clickLink != null) {
                return true;
            }
            if (bottomDrawer != null) {
                clickLink = bottomDrawer.checkClickLink(downX, downY);
            }
            if (clickLink != null) {
                return true;
            }
            downTime = 0L;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (Math.abs(downX - event.getRawX()) > 8 || Math.abs(downY - event.getRawY()) > 8) {
                downTime = 0L;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (System.currentTimeMillis() - downTime < 800) {
                ToastUtil.toast("clickLink==>" + clickLink);
            }
        } else {
            downTime = 0L;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!playing) {
            return;
        }
        postInvalidateDelayed(10);
        for (BaseDrawer drawer : rollDrawerList) {
            drawer.draw(canvas);
        }
        if (topDrawer != null) {
            topDrawer.draw(canvas);
        }
        if (bottomDrawer != null) {
            bottomDrawer.draw(canvas);
        }
    }

    public void initDrawer(int left, int top, int right, int bottom) {
        float density = getResources().getDisplayMetrics().density;
        float lineHeight = 24 * density;
        float textSize = 16 * density;
        topDrawer = new TopDrawer(new RectF(left, top + 12 * density, right,
                top + 12 * density + lineHeight * topLineCount + (topLineCount - 1) * density),
                lineHeight, textSize);
        topDrawer.lineSpace = density;
        topDrawer.setDataLoader(drawInfoDispatcher);
        bottomDrawer = new BottomDrawer(new RectF(left,
                bottom - lineHeight * bottomLineCount - (bottomLineCount - 1) * density,
                right, bottom), lineHeight, textSize);
        bottomDrawer.lineSpace = density;
        bottomDrawer.setDataLoader(drawInfoDispatcher);
        for (int i = 0; i < rollLineCount; i++) {
            RollDrawer rollDrawer = new RollDrawer(new RectF(left, top + (lineHeight + density) * i,
                    right, top + (lineHeight + density) * (i + 1)), lineHeight, textSize);
            rollDrawer.setDataLoader(drawInfoDispatcher);
            rollDrawerList.add(rollDrawer);
        }
    }

    public void pause() {
        playing = false;
    }

    public void start() {
        playing = true;
    }

    public void clearDataList() {
        drawInfoDispatcher.clearDataList();
    }

    public void addDataList(List<DanmakuNetBean> list) {
        drawInfoDispatcher.addDataList(list);
    }

    public void addData(DanmakuNetBean bean, boolean insertEnd) {
        drawInfoDispatcher.addData(bean, insertEnd);
    }

}
