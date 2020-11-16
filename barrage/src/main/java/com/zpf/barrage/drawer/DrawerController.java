package com.zpf.barrage.drawer;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.zpf.barrage.interfaces.IDataLoader;
import com.zpf.barrage.model.DrawInfo;
import com.zpf.barrage.model.DrawerSetting;

import java.util.LinkedList;

public class DrawerController implements IDataLoader {
    private LinkedList<RollDrawer> rollDrawerList = new LinkedList<>();
    private TopDrawer topDrawer;
    private BottomDrawer bottomDrawer;
    private int rollLineCount = 3;
    private int topLineCount = 3;
    private int bottomLineCount = 3;
    private IDataLoader dataLoader;
    private float lineSpace = DrawerSetting.DEF_LINE_SPACE;
    private float lineHeight = DrawerSetting.DEF_LINE_HEIGHT;
    private float textSize = DrawerSetting.DEF_TEXT_SIZE;

    public void onLayoutChanged(int left, int top, int right, int bottom) {
        topDrawer = new TopDrawer(new RectF(left, top + lineHeight / 2, right,
                top + lineHeight / 2 + lineHeight * topLineCount + (topLineCount - 1) * lineSpace),
                lineHeight, textSize);
        topDrawer.lineSpace = lineSpace;
        topDrawer.setDataLoader(this);
        bottomDrawer = new BottomDrawer(new RectF(left,
                bottom - lineHeight * bottomLineCount - (bottomLineCount - 1) * lineSpace,
                right, bottom), lineHeight, textSize);
        bottomDrawer.lineSpace = lineSpace;
        bottomDrawer.setDataLoader(this);
        for (int i = 0; i < rollLineCount; i++) {
            RollDrawer rollDrawer = new RollDrawer(new RectF(left, top + (lineHeight + lineSpace) * i,
                    right, top + (lineHeight + lineSpace) * (i + 1)), lineHeight, textSize);
            rollDrawer.setDataLoader(this);
            rollDrawerList.add(rollDrawer);
        }
    }

    public void doDraw(Canvas canvas) {
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

    public void prepareNext() {
        for (BaseDrawer drawer : rollDrawerList) {
            drawer.prepare();
        }
        if (topDrawer != null) {
            topDrawer.prepare();
        }
        if (bottomDrawer != null) {
            bottomDrawer.prepare();
        }
    }

    @Override
    public DrawInfo pollByType(int type) {
        if (dataLoader != null) {
            return dataLoader.pollByType(type);
        }
        return null;
    }

    public void setDataLoader(IDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
}
