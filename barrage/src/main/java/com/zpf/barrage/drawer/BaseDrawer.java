package com.zpf.barrage.drawer;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.zpf.barrage.model.DrawInfo;
import com.zpf.barrage.interfaces.IDataLoader;
import com.zpf.barrage.model.DrawerSetting;
import com.zpf.barrage.model.DrawerType;

import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

public abstract class BaseDrawer implements IDanmakuDrawer{

    protected float lineHeight;
    protected float lineSpace = 1f;
    protected IDataLoader dataLoader;
    protected boolean lackData = true;
    protected RectF totalRectF;
    protected RectF drawRectF = new RectF();
    protected Paint paint;
    protected LinkedList<DrawInfo> elementList = new LinkedList<>();
    @DrawerType
    protected int drawerType = DrawerType.DRAW_ROLL;
    protected boolean needMeasure = false;

    public BaseDrawer(RectF rectf, float lineHeight, float textSize) {
        totalRectF = rectf;
        paint = new Paint();
        paint.setTextSize(textSize);
        this.lineHeight = lineHeight;
    }

    protected abstract boolean shouldAddItem(RectF lastDrawRectF);

    protected abstract boolean checkDraw(DrawInfo item);

    protected abstract void initLocation(RectF lastDrawRectF, DrawInfo item);

    @Override
    public void prepare() {
        int s = elementList.size();
        drawRectF.setEmpty();
        if (s > 0) {
            for (int i = 0; i < s; i++) {
                DrawInfo item = elementList.pollFirst();
                if (item == null) {
                    continue;
                }
                checkMeasure(drawRectF, item);
                if (checkDraw(item)) {
                    elementList.add(item);
                    drawRectF.set(item.rectF);
                }
            }
        } else {
            lackData = true;
        }
        while (lackData || shouldAddItem(drawRectF)) {
            DrawInfo addItem = pollOne();
            if (addItem == null) {
                lackData = true;
                break;
            }
            lackData = false;
            checkMeasure(drawRectF, addItem);
            elementList.add(addItem);
            drawRectF.set(addItem.rectF);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if(!lackData){
            for (DrawInfo item :elementList) {
                doDraw(canvas, item);
            }
        }
    }

    public String checkClickLink(float downX, float downY) {
        if (downX < totalRectF.left || downX > totalRectF.right
                || downY < totalRectF.top || downY > totalRectF.bottom) {
            return null;
        }
        String link = "";
        DrawInfo info;
        RectF elementRectF;
        for (int i = 0; i < elementList.size(); i++) {
            info = elementList.get(i);
            if (info == null) {
                break;
            }
            elementRectF = info.rectF;
            if (elementRectF == null) {
                continue;
            }
            if (downX >= elementRectF.left && downX <= elementRectF.right
                    && downY >= elementRectF.top && downY <= elementRectF.bottom) {
                link = info.linkUrl;
                break;
            }
        }
        if (link == null) {
            link = "";
        }
        return link;
    }

    protected void doDraw(Canvas canvas, DrawInfo item) {
        if (item.getDrawWidth() > 0) {
            item.draw(canvas, paint, DrawerSetting.DEF_PAINT_COLOR);
        }
    }

    @Nullable
    private DrawInfo pollOne() {
        if (dataLoader != null) {
            return dataLoader.pollByType(drawerType);
        }
        return null;
    }

    private void checkMeasure(RectF lastDrawRectF, DrawInfo item) {
        if (item.getDrawWidth() == 0 || item.textSize != paint.getTextSize() || needMeasure) {
            item.rectF.left = 0;
            item.rectF.top = 0;
            item.rectF.bottom = lineHeight;
            item.rectF.right = 0;
            float iconWidth;
            float iconHeight;
            item.textSize = paint.getTextSize();
            if (item.startBitmap != null) {
                iconWidth = item.startBitmap.getWidth();
                iconHeight = item.startBitmap.getHeight();
                if (item.startIconRect == null) {
                    item.startIconRect = new RectF();
                } else {
                    item.startIconRect.setEmpty();
                }
                if (iconWidth > 0 && iconHeight > 0) {
                    if (iconHeight > lineHeight) {
                        iconHeight = lineHeight;
                        iconWidth = (lineHeight * 1.0f / item.startBitmap.getHeight() * item.startBitmap.getWidth());
                    }
                    item.startIconRect.left = DrawerSetting.DEF_INSIDE_SPACE;
                    item.startIconRect.right = DrawerSetting.DEF_INSIDE_SPACE + iconWidth;
                    item.startIconRect.top = (lineHeight - iconHeight) / 2;
                    item.startIconRect.bottom = item.startIconRect.top + iconHeight;
                    item.rectF.right = item.startIconRect.right;
                }
            }

            item.textSize = paint.getTextSize();
            item.textWidth = paint.measureText(item.content);
            if (item.colors != null && item.colors.length > 1) {
                item.linearGradient = new LinearGradient(0, 0, item.textWidth, 0,
                        item.colors, null, Shader.TileMode.CLAMP);
                if (item.matrix == null) {
                    item.matrix = new Matrix();
                }
            }
            item.textDrawX = item.rectF.right + DrawerSetting.DEF_INSIDE_SPACE;
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float dy = (fontMetrics.ascent - fontMetrics.top) - (fontMetrics.bottom - fontMetrics.descent);
            item.textDrawY = (lineHeight + item.textSize - dy) * 0.5f;
            if (item.textWidth > 0) {
                item.rectF.right = item.rectF.right + DrawerSetting.DEF_INSIDE_SPACE + item.textWidth;
            }
            if (item.endBitmap != null) {
                iconWidth = item.endBitmap.getWidth();
                iconHeight = item.endBitmap.getHeight();
                if (item.endIconRect == null) {
                    item.endIconRect = new RectF();
                } else {
                    item.endIconRect.setEmpty();
                }
                if (iconWidth > 0 && iconHeight > 0) {
                    if (iconHeight > lineHeight) {
                        iconHeight = lineHeight;
                        iconWidth = (lineHeight * 1.0f / item.endBitmap.getHeight() * item.endBitmap.getWidth());
                    }
                    item.endIconRect.left = item.rectF.right + DrawerSetting.DEF_INSIDE_SPACE;
                    item.endIconRect.right = item.endIconRect.left + iconWidth;
                    item.endIconRect.top = (lineHeight - iconHeight) / 2;
                    item.endIconRect.bottom = item.endIconRect.top + iconHeight;
                    item.rectF.right = item.endIconRect.right;
                }
            }
            item.rectF.right = item.rectF.right + DrawerSetting.DEF_INSIDE_SPACE;
            initLocation(lastDrawRectF, item);
        }
    }

    public void setTextSize(float size) {
        paint.setTextSize(Math.min(size, lineHeight));
    }

    public void setLineHeight(float lineHeight) {
        this.lineHeight = lineHeight;
    }

    public void setDataLoader(IDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
}
