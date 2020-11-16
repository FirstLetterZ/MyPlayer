package com.zpf.myplayer.view.bean;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import com.zpf.myplayer.view.drawer.DrawerSetting;


public class DrawInfo implements Cloneable {
    public float textWidth;
    public float textSize;
    public float textDrawX;
    public float textDrawY;
    public int[] colors;
    public String content;
    public LinearGradient linearGradient;
    public Matrix matrix;

    public RectF rectF = new RectF();
    public float speed;

    public int bgColor;
    public float bgRadius;
    public int alpha = 255;

    public RectF startIconRect = new RectF();
    public Bitmap startBitmap;
    public RectF endIconRect = new RectF();
    public Bitmap endBitmap;

    public long drawTime = 0;
    public boolean underLine;
    public int lineColor = 0;
    public String linkUrl;

    public float getDrawWidth() {
        return rectF.width();
    }

    public float getDrawHeight() {
        return rectF.height();
    }

    public void draw(Canvas canvas, Paint paint, int defColor) {
        if (bgColor != 0) {
            paint.setColor(bgColor);
            paint.setAlpha(alpha);
            if (bgRadius > 0) {
                canvas.drawRoundRect(rectF, bgRadius, bgRadius, paint);
            } else {
                canvas.drawRect(rectF, paint);
            }
        }
        float drawLine = -1;
        if (startBitmap != null && startIconRect != null) {
            paint.setAlpha(alpha);
            canvas.drawBitmap(startBitmap, null, startIconRect, paint);
            if (underLine) {
                drawLine = Math.max(drawLine, startIconRect.bottom);
            }
        }
        if (textWidth > 0) {
            if (linearGradient != null) {
                matrix.setTranslate(textDrawX, 0f);
                linearGradient.setLocalMatrix(matrix);
                paint.setShader(linearGradient);
            } else {
                paint.setShader(null);
                if (colors != null && colors.length > 0) {
                    paint.setColor(colors[0]);
                } else {
                    paint.setColor(defColor);
                }
            }
            paint.setAlpha(alpha);
            canvas.drawText(content, textDrawX, textDrawY, paint);
            if (underLine) {
                drawLine = Math.max(drawLine, textDrawY + textSize / 6);
            }
        }
        if (endBitmap != null && endIconRect != null) {
            paint.setAlpha(alpha);
            canvas.drawBitmap(endBitmap, null, endIconRect, paint);
            if (underLine) {
                drawLine = Math.max(drawLine, startIconRect.bottom);
            }
        }
        if (underLine) {
            if (lineColor > 0) {
                paint.setColor(lineColor);
            } else {
                paint.setColor(defColor);
            }
            drawLine = Math.min(drawLine + 2, rectF.bottom);
            paint.setAlpha(alpha);
            canvas.drawRect(rectF.left + DrawerSetting.DEF_INSIDE_SPACE, drawLine - 2,
                    rectF.right - DrawerSetting.DEF_INSIDE_SPACE, drawLine, paint);
        }
    }
}
