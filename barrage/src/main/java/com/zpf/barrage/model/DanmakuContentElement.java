package com.zpf.barrage.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public abstract class DanmakuContentElement {
    public RectF infoRectF = new RectF();
    public String contentString;

    public abstract void draw(Canvas canvas, Paint paint);

    public abstract void measure(float startX, float top, float bottom, float space, Context context, Paint paint);

}
