package com.zpf.barrage.drawer;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.zpf.barrage.model.DrawInfo;
import com.zpf.barrage.model.DrawerType;

public class BottomDrawer extends BaseDrawer {

    private long duration = 3000;
    private float animTime = 500;

    public BottomDrawer(RectF rectf, float lineHeight, float textSize) {
        super(rectf, lineHeight, textSize);
        drawerType = DrawerType.DRAW_BOTTOM;
    }

    @Override
    protected boolean shouldAddItem(RectF lastDrawRectF) {
        return lastDrawRectF.height() == 0 || lastDrawRectF.width() == 0 || lastDrawRectF.top - lineHeight >= totalRectF.top;
    }

    @Override
    protected void doDraw(Canvas canvas, DrawInfo item) {
        super.doDraw(canvas, item);
    }

    @Override
    protected boolean checkDraw(DrawInfo item) {
        if (item.drawTime == 0) {
            item.drawTime = System.currentTimeMillis();
            item.alpha = 0;
            return true;
        } else {
            long remainTime = duration - (System.currentTimeMillis() - item.drawTime);
            if (remainTime > 0) {
                if (duration - remainTime < animTime) {
                    item.alpha = (int) ((duration - remainTime) * 1f / animTime * 255f);
                } else if (remainTime >= animTime) {
                    item.alpha = 255;
                } else {
                    item.alpha = (int) (remainTime * 1f / animTime * 255f);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected void initLocation(RectF lastDrawRectF, DrawInfo item) {
        float dx = (totalRectF.width() - item.rectF.width()) / 2;
        item.rectF.left = item.rectF.left + dx;
        item.rectF.right = item.rectF.right + dx;
        if (item.startIconRect != null) {
            item.startIconRect.left = item.startIconRect.left + dx;
            item.startIconRect.right = item.startIconRect.right + dx;
        }
        item.textDrawX = item.textDrawX + dx;
        if (item.endIconRect != null) {
            item.endIconRect.left = item.endIconRect.left + dx;
            item.endIconRect.right = item.endIconRect.right + dx;
        }

        float dY;
        if (lastDrawRectF.isEmpty()) {
            dY = totalRectF.bottom - item.rectF.bottom;
        } else {
            dY = lastDrawRectF.top - Math.max(0, lineSpace) - item.rectF.bottom;
        }
        item.rectF.top = item.rectF.top + dY;
        item.rectF.bottom = item.rectF.bottom + dY;
        if (item.startIconRect != null) {
            item.startIconRect.top = item.startIconRect.top + dY;
            item.startIconRect.bottom = item.startIconRect.bottom + dY;
        }
        item.textDrawY = item.textDrawY + dY;
        if (item.endIconRect != null) {
            item.endIconRect.top = item.endIconRect.top + dY;
            item.endIconRect.bottom = item.endIconRect.bottom + dY;
        }
    }
}