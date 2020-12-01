package com.zpf.barrage.model;

import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.RectF;

import java.util.LinkedList;

public class DanmakuItemInfo {
    @DrawerType
    public int showType;
    public long showTime = 0L;
    public long showDuration = 0L;
    public long drawTime = 0L;
    public float rollSpeed;
    public int alpha = 0;
    public int bgColor;
    public float bgRadius;
    public int[] textColors;
    public LinearGradient textGradient;
    public Matrix textMatrix;
    public int lineColor = 0;
    public float lineBottom = 0f;
    public RectF responseRegion = new RectF();
    public String responseInfo;
    public LinkedList<DanmakuContentElement> contentList = new LinkedList<>();
}
