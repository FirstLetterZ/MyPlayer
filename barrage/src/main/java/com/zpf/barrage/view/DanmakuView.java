package com.zpf.barrage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.zpf.barrage.controller.DanmakuDrawInfoController;
import com.zpf.barrage.controller.DanmakuSourceController;
import com.zpf.barrage.interfaces.IDanmakuTypeBean;
import com.zpf.barrage.interfaces.IDataDispatcher;
import com.zpf.barrage.interfaces.IDrawTimeChecker;
import com.zpf.barrage.interfaces.OnItemLinkClickListener;
import com.zpf.barrage.model.DrawerSetting;

import java.util.List;

public class DanmakuView extends View implements IDataDispatcher {
    private DanmakuDrawInfoController drawInfoController;
    private DanmakuSourceController sourceController;
    private boolean playing = false;

    public DanmakuView(Context context) {
        super(context);
        initConfig();
    }

    public DanmakuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initConfig();
    }

    public DanmakuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initConfig();
    }

    private void initConfig() {
        DrawerSetting.initDefConfig(getContext());
        sourceController = new DanmakuSourceController();
        drawInfoController = new DanmakuDrawInfoController(getContext());
        drawInfoController.setDataLoader(sourceController);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            drawInfoController.onLayoutChanged(left, top, right, bottom);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return playing && drawInfoController.checkTouchInRect(event) || super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!playing) {
            return;
        }
        long start = System.currentTimeMillis();
        drawInfoController.doDraw(canvas);
        drawInfoController.prepareNextFrame();
        postInvalidateDelayed(Math.max(0, 8 + start - System.currentTimeMillis()));
    }

    public void pause() {
        playing = false;
    }

    public void start() {
        playing = true;
    }

    @Override
    public void clearDataList() {
        sourceController.clearDataList();
    }

    @Override
    public void addDataList(List<? extends IDanmakuTypeBean> list) {
        sourceController.addDataList(list);
    }

    @Override
    public void addData(IDanmakuTypeBean bean, boolean insertEnd) {
        sourceController.addData(bean, insertEnd);
    }

    @Override
    public void setDrawTimeChecker(IDrawTimeChecker checker) {
        sourceController.setDrawTimeChecker(checker);
    }

    public void setItemClickListener(OnItemLinkClickListener linkClickListener) {
        drawInfoController.setItemLinkClickListener(linkClickListener);
    }
}
