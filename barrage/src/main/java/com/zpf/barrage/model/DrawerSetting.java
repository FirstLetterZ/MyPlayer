package com.zpf.barrage.model;

import android.content.Context;
import android.graphics.Color;

public class DrawerSetting {
    public static int DEF_PAINT_COLOR = Color.WHITE;
    public static float DEF_INSIDE_SPACE = 4f;
    public static long DEF_REMAIN_DURATION = 3000;
    public static float DEF_TEXT_SIZE = 16f;
    public static float DEF_LINE_SPACE = 1f;
    public static float DEF_LINE_HEIGHT = 24f;
    public static int DEF_LINE_COUNT = 3;

    public static final String CACHE = "cache";
    public static final String ASSETS = "assets";
    public static final String EXTERNAL = "external";
    public static final String RESOURCE = "resource";

    public static void initDefConfig(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        DEF_LINE_HEIGHT = 24 * density;
        DEF_TEXT_SIZE = 16 * density;
        DEF_LINE_SPACE = 1 * density;
        DEF_INSIDE_SPACE = 4 * density;
    }
}
