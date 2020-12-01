package com.zpf.barrage.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.zpf.barrage.util.IconBitmapParseUtil;

public class DanmakuIconElement extends DanmakuContentElement {
    public Bitmap iconBitmap;

    public void draw(Canvas canvas, Paint paint) {
        if (iconBitmap != null && infoRectF.width() > 0) {
            canvas.drawBitmap(iconBitmap, null, infoRectF, paint);
        }
    }

    @Override
    public void measure(float startX, float top, float bottom, float space, Context context, Paint paint) {
        iconBitmap = IconBitmapParseUtil.parseBitmap(context, contentString);
        if (iconBitmap != null && iconBitmap.getHeight() > 0) {
            infoRectF.left = startX + space;
            if (iconBitmap.getHeight() >= bottom - top) {
                infoRectF.top = top;
                infoRectF.bottom = bottom;
                infoRectF.right = startX + space + (bottom - top) / iconBitmap.getHeight() * iconBitmap.getWidth();
            } else {
                infoRectF.top = (bottom - top - iconBitmap.getHeight()) / 2;
                infoRectF.bottom = infoRectF.top + iconBitmap.getHeight();
                infoRectF.right = startX + space * 0.5f + iconBitmap.getWidth();
            }
        } else {
            infoRectF.top = top;
            infoRectF.bottom = bottom;
            infoRectF.left = startX;
            infoRectF.right = startX;
        }
    }
}
