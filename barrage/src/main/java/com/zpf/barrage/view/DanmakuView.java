package com.zpf.barrage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.zpf.barrage.bean.DanmakuNetBean;
import com.zpf.barrage.drawer.DrawerController;
import com.zpf.barrage.util.DataDispatcher;
import com.zpf.barrage.model.DrawerSetting;
import com.zpf.barrage.util.DanmakuItemClickHelper;

import java.util.List;

public class DanmakuView extends View {
    private DataDispatcher drawInfoDispatcher = new DataDispatcher();
    private boolean playing = false;
    private DrawerController drawerController = new DrawerController();
    private DanmakuItemClickHelper clickHelper = new DanmakuItemClickHelper();

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
        drawerController.setDataLoader(drawInfoDispatcher);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            drawerController.onLayoutChanged(left, top, right, bottom);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return playing && clickHelper.interceptTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!playing) {
            return;
        }
        drawerController.doDraw(canvas);
        postInvalidateDelayed(10);
        drawerController.prepareNext();
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
