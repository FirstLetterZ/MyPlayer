package com.zpf.barrage.controller;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.zpf.barrage.interfaces.IDataLoader;
import com.zpf.barrage.model.DrawerType;
import com.zpf.barrage.model.DanmakuContentElement;
import com.zpf.barrage.model.DanmakuItemInfo;
import com.zpf.barrage.model.DanmakuTextElement;
import com.zpf.barrage.util.DrawHelper;

import java.util.LinkedList;
import java.util.Random;

public class DanmakuRegionController {
    public RectF infoRectF = new RectF();
    private Random random;
    @DrawerType
    public int itemType;
    private LinkedList<DanmakuItemInfo> drawCache = new LinkedList<>();
    private IDataLoader<DanmakuItemInfo> dataLoader;

    public DanmakuRegionController(Random random) {
        this.random = random;
    }

    public void layoutChanged(float left, float top, float right, float bottom) {
        drawCache.clear();
        infoRectF.left = left;
        infoRectF.top = top;
        infoRectF.right = right;
        infoRectF.bottom = bottom;
    }

    public void doDraw(Canvas canvas, Paint paint) {
        DrawHelper.doDraw(canvas, paint, drawCache);
    }

    public void prepareNextFrame() {
        int cacheSize = drawCache.size();
        DanmakuItemInfo lastItem = null;
        DanmakuItemInfo itemInfo;
        while (cacheSize > 0) {
            itemInfo = drawCache.poll();
            if (prepareItemInfo(itemInfo)) {
                drawCache.add(itemInfo);
                lastItem = itemInfo;
            }
            cacheSize--;
        }
        while (!checkCacheFull(lastItem)) {
            itemInfo = pollOneNewElement(itemType);
            if (itemInfo == null) {
                break;
            } else {
                initItemLocation(lastItem, itemInfo);
                lastItem = itemInfo;
                drawCache.add(itemInfo);
            }
        }
    }

    private DanmakuItemInfo pollOneNewElement(int type) {
        if (dataLoader != null) {
            return dataLoader.pollByType(type);
        }
        return null;
    }

    private void initItemLocation(DanmakuItemInfo lastItemInfo, DanmakuItemInfo newItem) {
        if (newItem == null) {
            return;
        }
        float dx;
        float dy = infoRectF.top - newItem.responseRegion.top;
        if (itemType == DrawerType.DRAW_ROLL) {
            if (lastItemInfo == null) {
                dx = infoRectF.right + (1f * random.nextInt(4)) * infoRectF.height();
            } else {
                dx = lastItemInfo.responseRegion.right + (2f + 0.5f * random.nextInt(4)) * infoRectF.height();
            }
        } else {
            dx = (infoRectF.width() - newItem.responseRegion.width()) / 2;
        }
        newItem.responseRegion.left = newItem.responseRegion.left + dx;
        newItem.responseRegion.right = newItem.responseRegion.right + dx;
        newItem.responseRegion.top = infoRectF.top;
        newItem.responseRegion.bottom = infoRectF.bottom;
        for (DanmakuContentElement contentInfo : newItem.contentList) {
            contentInfo.infoRectF.left += dx;
            contentInfo.infoRectF.right += dx;
            contentInfo.infoRectF.top += dy;
            contentInfo.infoRectF.bottom += dy;
            if (contentInfo instanceof DanmakuTextElement) {
                ((DanmakuTextElement) contentInfo).textDrawY = ((DanmakuTextElement) contentInfo).textDrawY + dy;
            }
        }
    }

    public boolean checkCacheFull(DanmakuItemInfo lastItemInfo) {
        if (itemType == DrawerType.DRAW_ROLL) {
            return lastItemInfo != null && lastItemInfo.responseRegion.right > 1.2 * infoRectF.right;
        } else {
            return lastItemInfo != null;
        }
    }

    private boolean prepareItemInfo(DanmakuItemInfo itemInfo) {
        if (itemInfo == null) {
            return false;
        }
        if (itemType == DrawerType.DRAW_ROLL) {
            itemInfo.alpha = 255;
            itemInfo.responseRegion.left = itemInfo.responseRegion.left - itemInfo.rollSpeed;
            itemInfo.responseRegion.right = itemInfo.responseRegion.right - itemInfo.rollSpeed;
            if (itemInfo.responseRegion.right > 0) {
                for (DanmakuContentElement contentInfo : itemInfo.contentList) {
                    contentInfo.infoRectF.left = contentInfo.infoRectF.left - itemInfo.rollSpeed;
                    contentInfo.infoRectF.right = contentInfo.infoRectF.right - itemInfo.rollSpeed;
                }
                return true;
            } else {
                return false;
            }
        } else {
            if (itemInfo.drawTime == 0) {
                itemInfo.drawTime = System.currentTimeMillis();
                itemInfo.alpha = 0;
                return true;
            } else {
                long showTime = System.currentTimeMillis() - itemInfo.drawTime;
                if (showTime < itemInfo.showDuration) {
                    if (itemInfo.showDuration * 0.8f < showTime) {
                        itemInfo.alpha = (int) (255 * (1 - 5 * (showTime * 1f / itemInfo.showDuration - 0.8f)));
                    } else if (itemInfo.showDuration * 0.2f > showTime) {
                        itemInfo.alpha = (int) (showTime * 255f * 4 / itemInfo.showDuration + 0.2f * 255f);
                    } else {
                        itemInfo.alpha = 255;
                    }
                    return true;
                }
                return false;
            }
        }
    }

    public void setDataLoader(IDataLoader<DanmakuItemInfo> dataLoader) {
        this.dataLoader = dataLoader;
    }

    public boolean checkClickInRect(float downX, float downY, RectF rectF) {
        return (downX >= rectF.left) && (downX <= rectF.right)
                && (downY >= rectF.top) && (downY <= rectF.bottom);
    }

    public DanmakuItemInfo findClickItem(float downX, float downY) {
        for (DanmakuItemInfo itemInfo : drawCache) {
            if (checkClickInRect(downX, downY, itemInfo.responseRegion)) {
                return itemInfo;
            }
        }
        return null;
    }
}