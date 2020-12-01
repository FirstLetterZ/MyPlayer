package com.zpf.barrage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.zpf.barrage.controller.DanmakuDrawInfoController;
import com.zpf.barrage.controller.DanmakuSourceController;
import com.zpf.barrage.interfaces.IDanmakuTypeBean;
import com.zpf.barrage.interfaces.IDataDispatcher;
import com.zpf.barrage.interfaces.IDrawTimeChecker;
import com.zpf.barrage.interfaces.OnItemLinkClickListener;
import com.zpf.barrage.model.DrawerSetting;

import java.util.List;

public class DanmakuSurfaceView extends SurfaceView implements SurfaceHolder.Callback , IDataDispatcher {
    private DanmakuDrawInfoController drawInfoController;
    private DanmakuSourceController sourceController;
    private boolean playing = false;
    private SurfaceHolder surfaceHolder;
    private Runnable drawRunnable = new Runnable() {
        @Override
        public void run() {
            if (surfaceHolder != null && playing) {
                long start = System.currentTimeMillis();
                Canvas canvas = surfaceHolder.lockCanvas();
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                drawInfoController.doDraw(canvas);
                surfaceHolder.unlockCanvasAndPost(canvas);
                drawInfoController.prepareNextFrame();
                postDelayed(drawRunnable, Math.max(0, 12 + start - System.currentTimeMillis()));
            }
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        requestLayout();
        invalidate();
        super.onDraw(canvas);
    }

    public DanmakuSurfaceView(Context context) {
        super(context);
        initConfig();
    }

    public DanmakuSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initConfig();
    }

    public DanmakuSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initConfig();
    }

    public DanmakuSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initConfig();
    }

    private void initConfig() {
        DrawerSetting.initDefConfig(getContext());
        sourceController = new DanmakuSourceController();
        drawInfoController = new DanmakuDrawInfoController(getContext());
        drawInfoController.setDataLoader(sourceController);
        bindHolder(getHolder());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return (playing && surfaceHolder != null && drawInfoController.checkTouchInRect(event)) || super.onTouchEvent(event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        bindHolder(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        bindHolder(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        holder.removeCallback(this);
        surfaceHolder = null;
        playing = false;
        removeCallbacks(drawRunnable);
    }

    public void pause() {
        removeCallbacks(drawRunnable);
        playing = false;
    }

    public void start() {
        if (!playing) {
            removeCallbacks(drawRunnable);
            postDelayed(drawRunnable, 1000);
        }
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

    private void bindHolder(SurfaceHolder holder) {
        removeCallbacks(drawRunnable);
        surfaceHolder = holder;
        if (holder == null) {
            return;
        }
        holder.addCallback(this);
        Rect surfaceFrame = holder.getSurfaceFrame();
        drawInfoController.onLayoutChanged(surfaceFrame.left, surfaceFrame.top, surfaceFrame.right, surfaceFrame.bottom);
        if (playing) {
            postDelayed(drawRunnable, 1000);
        }
    }

    public void setItemClickListener(OnItemLinkClickListener linkClickListener) {
        drawInfoController.setItemLinkClickListener(linkClickListener);
    }
}
