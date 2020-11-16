package com.zpf.barrage.interfaces;

import android.content.Context;
import android.graphics.Bitmap;

public interface IBitmapParser {
    Bitmap parseBitmap(Context context, String uriString);
}
