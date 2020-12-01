package com.zpf.barrage.controller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.zpf.barrage.interfaces.IDataLoader;
import com.zpf.barrage.interfaces.OnItemLinkClickListener;
import com.zpf.barrage.model.DrawerSetting;
import com.zpf.barrage.model.DrawerType;
import com.zpf.barrage.model.DanmakuContentElement;
import com.zpf.barrage.model.DanmakuItemInfo;

import java.util.LinkedList;
import java.util.Random;

public class DanmakuDrawInfoController implements IDataLoader<DanmakuItemInfo> {
    private Random random = new Random();
    private Context appContext;
    private float fontSize;
    private float insideSpace;
    private long remainDuration;
    private float lineSpace;
    private float lineHeight;
    private int lineCount;

    private LinkedList<DanmakuRegionController> topCache = new LinkedList<>();
    private LinkedList<DanmakuRegionController> rollCache = new LinkedList<>();
    private LinkedList<DanmakuRegionController> bottomCache = new LinkedList<>();
    private int cacheSize = 0;
    private Paint paint;
    private IDataLoader<DanmakuItemInfo> dataLoader;
    private int width;
    private int height;

    private OnItemLinkClickListener listener;
    private String clickResponse;
    private long downTime = 0L;
    private float downX = 0f;
    private float downY = 0f;

    public DanmakuDrawInfoController(Context context) {
        this.appContext = context.getApplicationContext();
        DrawerSetting.initDefConfig(context);
        fontSize = DrawerSetting.DEF_TEXT_SIZE;
        insideSpace = DrawerSetting.DEF_INSIDE_SPACE;
        remainDuration = DrawerSetting.DEF_REMAIN_DURATION;
        lineSpace = DrawerSetting.DEF_LINE_SPACE;
        lineHeight = DrawerSetting.DEF_LINE_HEIGHT;
        lineCount = DrawerSetting.DEF_LINE_COUNT;
        paint = new Paint();
        paint.setTextSize(fontSize);
    }

    public void onLayoutChanged(int left, int top, int right, int bottom) {
        width = right - left;
        height = bottom - top;
        rollCache.clear();
        topCache.clear();
        bottomCache.clear();
        if (width == 0 || height == 0) {
            return;
        }
        checkLineInfo();
        for (int i = 0; i < lineCount; i++) {
            DanmakuRegionController dmc = new DanmakuRegionController(random);
            dmc.itemType = DrawerType.DRAW_ROLL;
            dmc.layoutChanged(left, top + (lineHeight + lineSpace) * i, right,
                    top + lineHeight + (lineHeight + lineSpace) * i);
            dmc.setDataLoader(this);
            rollCache.add(dmc);
        }
        for (int i = 0; i < lineCount; i++) {
            DanmakuRegionController dmc = new DanmakuRegionController(random);
            dmc.itemType = DrawerType.DRAW_TOP;
            dmc.layoutChanged(left, top + lineHeight * 0.5f + (lineHeight + lineSpace) * i, right,
                    top + lineHeight * 1.5f + (lineHeight + lineSpace) * i);
            dmc.setDataLoader(this);
            topCache.add(dmc);
        }
        for (int i = 0; i < lineCount - 1; i++) {
            DanmakuRegionController dmc = new DanmakuRegionController(random);
            dmc.itemType = DrawerType.DRAW_BOTTOM;
            dmc.layoutChanged(left, bottom - (lineHeight + lineSpace) * (i + 1), right,
                    bottom - lineSpace - (lineHeight + lineSpace) * i);
            dmc.setDataLoader(this);
            bottomCache.add(dmc);
        }
        cacheSize = rollCache.size() + topCache.size() + bottomCache.size();
    }

    public void doDraw(Canvas canvas) {
        if (width > 0 && height > 0) {
            for (DanmakuRegionController cache : rollCache) {
                cache.doDraw(canvas, paint);
            }
            for (DanmakuRegionController cache : topCache) {
                cache.doDraw(canvas, paint);
            }
            for (DanmakuRegionController cache : bottomCache) {
                cache.doDraw(canvas, paint);
            }
        }
    }

    public void prepareNextFrame() {
        if (width > 0 && height > 0) {
            for (DanmakuRegionController cache : rollCache) {
                cache.prepareNextFrame();
            }
            for (DanmakuRegionController cache : topCache) {
                cache.prepareNextFrame();
            }
            for (DanmakuRegionController cache : bottomCache) {
                cache.prepareNextFrame();
            }
        }
    }

    public boolean checkTouchInRect(MotionEvent event) {
        if (listener == null || cacheSize == 0) {
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = event.getRawX();
            downY = event.getRawY();
            downTime = System.currentTimeMillis();
            DanmakuItemInfo clickItem;
            clickResponse = null;
            boolean handled = false;
            for (DanmakuRegionController cache : rollCache) {
                if (cache.checkClickInRect(downX, downY, cache.infoRectF)) {
                    handled = true;
                    clickItem = cache.findClickItem(downX, downY);
                    if (clickItem != null) {
                        clickResponse = clickItem.responseInfo;
                    }
                }
                if (handled) {
                    break;
                }
            }
            if (handled) {
                return true;
            }
            for (DanmakuRegionController cache : topCache) {
                if (cache.checkClickInRect(downX, downY, cache.infoRectF)) {
                    handled = true;
                    clickItem = cache.findClickItem(downX, downY);
                    if (clickItem != null) {
                        clickResponse = clickItem.responseInfo;
                    }
                }
                if (handled) {
                    break;
                }
            }
            if (handled) {
                return true;
            }
            for (DanmakuRegionController cache : bottomCache) {
                if (cache.checkClickInRect(downX, downY, cache.infoRectF)) {
                    handled = true;
                    clickItem = cache.findClickItem(downX, downY);
                    if (clickItem != null) {
                        clickResponse = clickItem.responseInfo;
                    }
                }
                if (handled) {
                    break;
                }
            }
            if (handled) {
                return true;
            } else {
                downTime = 0L;
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (Math.abs(downX - event.getRawX()) > 8 || Math.abs(downY - event.getRawY()) > 8) {
                downTime = 0L;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (clickResponse != null && System.currentTimeMillis() - downTime < 800) {
                listener.onClick(clickResponse);
                return true;
            }
        } else {
            downTime = 0L;
        }
        return false;
    }

    public void setDataLoader(IDataLoader<DanmakuItemInfo> dataLoader) {
        this.dataLoader = dataLoader;
    }

    public void setItemLinkClickListener(OnItemLinkClickListener linkClickListener) {
        listener = linkClickListener;
    }

    private void checkLineInfo() {
        if (height < (lineHeight + lineSpace) * lineCount + lineHeight * 0.5f) {
            lineCount = (int) ((height - lineHeight * 0.5f) / (lineHeight + lineSpace));
        } else {
            lineCount = DrawerSetting.DEF_LINE_COUNT;
        }
    }

    @Override
    public DanmakuItemInfo pollByType(int type) {
        DanmakuItemInfo result = null;
        if (dataLoader != null) {
            result = dataLoader.pollByType(type);
        }
        if (result != null) {
            paint.setTextSize(fontSize);
            result.showDuration = remainDuration;
            result.responseRegion.left = 0f;
            result.responseRegion.top = 0f;
            result.responseRegion.bottom = lineHeight;
            float endX = 0f;
            for (DanmakuContentElement contentElement : result.contentList) {
                contentElement.measure(endX, result.responseRegion.top, result.responseRegion.bottom, insideSpace, appContext, paint);
                endX = contentElement.infoRectF.right;
            }
            if (endX > 0) {
                endX += insideSpace;
            }
            int ni = random.nextInt(50);
            if (ni == 0) {
                result.rollSpeed = 2f * appContext.getResources().getDisplayMetrics().density;
            } else {
                result.rollSpeed = appContext.getResources().getDisplayMetrics().density;
            }
            result.responseRegion.right = endX;
        }
        return result;
    }
}
