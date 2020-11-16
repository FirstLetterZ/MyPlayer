package com.zpf.myplayer.view.drawer;

import android.graphics.RectF;

import com.zpf.myplayer.view.bean.DrawInfo;

import java.util.Random;

public class RollDrawer extends BaseDrawer {
    private Random random = new Random();

    public RollDrawer(RectF rectf, float lineHeight, float textSize) {
        super(rectf, lineHeight, textSize);
    }

    @Override
    protected boolean shouldAddItem(RectF lastDrawRectF) {
        return lastDrawRectF.height() == 0 || lastDrawRectF.width() == 0 || lastDrawRectF.right <= totalRectF.right;
    }

    @Override
    protected boolean checkDraw(DrawInfo item) {
        item.rectF.left = item.rectF.left - item.speed;
        item.rectF.right = item.rectF.right - item.speed;
        if (item.startIconRect != null && !item.startIconRect.isEmpty()) {
            item.startIconRect.left = item.startIconRect.left - item.speed;
            item.startIconRect.right = item.startIconRect.right - item.speed;
        }
        item.textDrawX = item.textDrawX - item.speed;
        if (item.endIconRect != null && !item.endIconRect.isEmpty()) {
            item.endIconRect.left = item.endIconRect.left - item.speed;
            item.endIconRect.right = item.endIconRect.right - item.speed;
        }
        return item.rectF.right > 0;
    }

    @Override
    protected void initLocation(RectF lastDrawRectF, DrawInfo item) {
        float dx;
        if (lastDrawRectF.isEmpty()) {
            dx = totalRectF.right + (4 + random.nextInt(12)) * paint.getTextSize() - item.rectF.left;
        } else {
            dx = lastDrawRectF.right + (4 + random.nextInt(12)) * paint.getTextSize() - item.rectF.left;
        }
        item.rectF.left = item.rectF.left + dx;
        item.rectF.right = item.rectF.right + dx;
        if (item.startIconRect != null && !item.startIconRect.isEmpty()) {
            item.startIconRect.left = item.startIconRect.left + dx;
            item.startIconRect.right = item.startIconRect.right + dx;
        }
        item.textDrawX = item.textDrawX + dx;
        if (item.endIconRect != null && !item.endIconRect.isEmpty()) {
            item.endIconRect.left = item.endIconRect.left + dx;
            item.endIconRect.right = item.endIconRect.right + dx;
        }

        float dY = totalRectF.top - item.rectF.top;
        item.rectF.top = item.rectF.top + dY;
        item.rectF.bottom = item.rectF.bottom + dY;
        if (item.startIconRect != null && !item.startIconRect.isEmpty()) {
            item.startIconRect.top = item.startIconRect.top + dY;
            item.startIconRect.bottom = item.startIconRect.bottom + dY;
        }
        item.textDrawY = item.textDrawY + dY;
        if (item.endIconRect != null && !item.endIconRect.isEmpty()) {
            item.endIconRect.top = item.endIconRect.top + dY;
            item.endIconRect.bottom = item.endIconRect.bottom + dY;
        }
    }
}
