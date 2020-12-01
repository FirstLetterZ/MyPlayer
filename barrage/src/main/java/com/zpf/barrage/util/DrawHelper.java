package com.zpf.barrage.util;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;

import com.zpf.barrage.model.DrawerSetting;
import com.zpf.barrage.model.DanmakuContentElement;
import com.zpf.barrage.model.DanmakuItemInfo;
import com.zpf.barrage.model.DanmakuTextElement;

import java.util.List;

public class DrawHelper {

    public static void doDraw(Canvas canvas, Paint paint, List<DanmakuItemInfo> itemList) {
        if (itemList != null) {
            for (DanmakuItemInfo itemInfo : itemList) {
                drawBackGround(canvas, paint, itemInfo);
                drawContent(canvas, paint, itemInfo);
                drawBottomLine(canvas, paint, itemInfo);
            }
        }
    }

    public static void drawBackGround(Canvas canvas, Paint paint, DanmakuItemInfo itemInfo) {
        if (itemInfo == null) {
            return;
        }
        if (itemInfo.bgColor != 0) {
            paint.setColor(itemInfo.bgColor);
            paint.setAlpha(itemInfo.alpha);
            if (itemInfo.bgRadius > 0) {
                canvas.drawRoundRect(itemInfo.responseRegion, itemInfo.bgRadius, itemInfo.bgRadius, paint);
            } else {
                canvas.drawRect(itemInfo.responseRegion, paint);
            }
        }
    }

    public static void drawContent(Canvas canvas, Paint paint, DanmakuItemInfo itemInfo) {
        if (itemInfo == null) {
            return;
        }
        float maxBottom = 0f;
        for (DanmakuContentElement contentInfo : itemInfo.contentList) {
            if (contentInfo instanceof DanmakuTextElement) {
                if (itemInfo.textColors == null || itemInfo.textColors.length == 0) {
                    paint.setColor(DrawerSetting.DEF_PAINT_COLOR);
                } else if (itemInfo.textColors.length == 1) {
                    paint.setColor(itemInfo.textColors[0]);
                } else {
                    if (itemInfo.textGradient == null) {
                        itemInfo.textGradient = new LinearGradient(0, 0, contentInfo.infoRectF.width(), 0,
                                itemInfo.textColors, null, Shader.TileMode.CLAMP);
                    }
                    if (itemInfo.textMatrix == null) {
                        itemInfo.textMatrix = new Matrix();
                    }
                    itemInfo.textMatrix.setTranslate(contentInfo.infoRectF.left, 0f);
                    itemInfo.textGradient.setLocalMatrix(itemInfo.textMatrix);
                    paint.setShader(itemInfo.textGradient);
                }
            }
            maxBottom = Math.max(contentInfo.infoRectF.bottom, maxBottom);
            paint.setAlpha(itemInfo.alpha);
            contentInfo.draw(canvas, paint);
            if (paint.getShader() != null) {
                paint.setShader(null);
            }
        }
        itemInfo.lineBottom = maxBottom;
    }

    public static void drawBottomLine(Canvas canvas, Paint paint, DanmakuItemInfo itemInfo) {
        if (itemInfo == null) {
            return;
        }
        if (itemInfo.lineColor != 0) {
            paint.setColor(itemInfo.lineColor);
            paint.setAlpha(itemInfo.alpha);
            canvas.drawRect(itemInfo.responseRegion.left + DrawerSetting.DEF_INSIDE_SPACE, itemInfo.lineBottom - 2,
                    itemInfo.responseRegion.right - DrawerSetting.DEF_INSIDE_SPACE, itemInfo.lineBottom, paint);
        }
    }
}
