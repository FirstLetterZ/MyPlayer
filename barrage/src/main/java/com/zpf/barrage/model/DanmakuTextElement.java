package com.zpf.barrage.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

public class DanmakuTextElement extends DanmakuContentElement {
    public float textDrawY;

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (contentString != null && infoRectF.width() > 0) {
            canvas.drawText(contentString, infoRectF.left, textDrawY, paint);
        }
    }

    @Override
    public void measure(float startX, float top, float bottom, float space, Context context, Paint paint) {
        if (contentString != null && contentString.length() > 0) {
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float dy = (fontMetrics.ascent - fontMetrics.top) - (fontMetrics.bottom - fontMetrics.descent);
            infoRectF.top = (bottom - top - paint.getTextSize()) * 0.5f;
            infoRectF.bottom = (bottom - top + paint.getTextSize()) * 0.5f;
            textDrawY = (bottom - top + paint.getTextSize() - dy) * 0.5f;
            infoRectF.left = startX + space;
            infoRectF.right = startX + space + paint.measureText(contentString);
        } else {
            infoRectF.top = top;
            infoRectF.bottom = bottom;
            infoRectF.left = startX;
            infoRectF.right = startX;
        }
    }
}
