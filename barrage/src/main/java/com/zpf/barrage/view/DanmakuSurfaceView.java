package com.zpf.barrage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.zpf.barrage.bean.DanmakuNetBean;
import com.zpf.barrage.drawer.DrawerController;
import com.zpf.barrage.model.DrawerSetting;
import com.zpf.barrage.util.DanmakuItemClickHelper;
import com.zpf.barrage.util.DataDispatcher;

import java.util.List;

public class DanmakuSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private DataDispatcher drawInfoDispatcher = new DataDispatcher();
    private boolean playing = false;
    private DrawerController drawerController = new DrawerController();
    private DanmakuItemClickHelper clickHelper = new DanmakuItemClickHelper();
    private SurfaceHolder surfaceHolder;
    private Runnable drawRunnable = new Runnable() {
        @Override
        public void run() {
            if (surfaceHolder != null && playing) {
                long start = System.currentTimeMillis();
                Canvas canvas = surfaceHolder.lockCanvas();
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                drawerController.doDraw(canvas);
                surfaceHolder.unlockCanvasAndPost(canvas);
                drawerController.prepareNext();
                postDelayed(drawRunnable, Math.max(0, 16 + start - System.currentTimeMillis()));
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
        drawerController.setDataLoader(drawInfoDispatcher);
        bindHolder(getHolder());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return (playing && surfaceHolder != null && clickHelper.interceptTouchEvent(event)) || super.onTouchEvent(event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("ZPF", "surfaceCreated==>>" + holder.getSurfaceFrame().toString());
        bindHolder(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e("ZPF", "surfaceChanged==>>" + holder.getSurfaceFrame().toString());
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

    public void clearDataList() {
        drawInfoDispatcher.clearDataList();
    }

    public void addDataList(List<DanmakuNetBean> list) {
        drawInfoDispatcher.addDataList(list);
    }

    public void addData(DanmakuNetBean bean, boolean insertEnd) {
        drawInfoDispatcher.addData(bean, insertEnd);
    }

    private void bindHolder(SurfaceHolder holder) {
        removeCallbacks(drawRunnable);
        surfaceHolder = holder;
        if (holder == null) {
            return;
        }
        holder.addCallback(this);
        Rect surfaceFrame = holder.getSurfaceFrame();
        drawerController.onLayoutChanged(surfaceFrame.left, surfaceFrame.top, surfaceFrame.right, surfaceFrame.bottom);
        if (playing) {
            postDelayed(drawRunnable, 1000);
        }
    }

}
