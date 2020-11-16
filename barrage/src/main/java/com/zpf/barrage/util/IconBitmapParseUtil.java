package com.zpf.barrage.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.LruCache;


import com.zpf.barrage.interfaces.IBitmapParser;
import com.zpf.barrage.model.DrawerSetting;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashSet;

public class IconBitmapParseUtil {
    private static final LruCache<String, SoftReference<Bitmap>> bitmapCache = new LruCache<>(20);
    private static final HashSet<IBitmapParser> bitmapParsers = new HashSet<>();

    public static Bitmap parseBitmap(Context context, String uriString) {
        if (context == null || uriString == null) {
            return null;
        }
        Uri realUri = null;
        try {
            realUri = Uri.parse(uriString);
        } catch (Exception e) {
            //
        }
        if (realUri == null) {
            return null;
        }
        String path = realUri.getHost();
        if (path == null) {
            return null;
        }
        Bitmap result = null;
        try {
            result = bitmapCache.get(uriString).get();
        } catch (Exception e) {
            //
        }
        if (result != null) {
            return result;
        }
        for (IBitmapParser p : bitmapParsers) {
            result = p.parseBitmap(context, uriString);
            if (result != null) {
                bitmapCache.put(uriString, new SoftReference<>(result));
                return result;
            }
        }
        if (DrawerSetting.RESOURCE.equals(realUri.getScheme())) {
            int resId;
            try {
                int index = path.lastIndexOf(".");
                if (index > 0) {
                    path = path.substring(0, index);
                }
                resId = context.getResources().getIdentifier(path, "mipmap", context.getPackageName());
            } catch (Exception e) {
                resId = 0;
            }
            if (resId != 0) {
                result = BitmapFactory.decodeResource(context.getResources(), resId);
            }
        } else if (DrawerSetting.ASSETS.equals(realUri.getScheme())) {
            try {
                InputStream inputStream = context.getAssets().open(path);
                result = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                //
            }
        } else if (DrawerSetting.CACHE.equals(realUri.getScheme())) {
            result = BitmapFactory.decodeFile(context.getFilesDir() + "/" + path);
        } else if (DrawerSetting.EXTERNAL.equals(realUri.getScheme())) {
            result = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + path);
        }
        if (result != null) {
            bitmapCache.put(uriString, new SoftReference<>(result));
        }
        return result;
    }

    public static void addParser(IBitmapParser parser) {
        bitmapParsers.add(parser);
    }

    public static void removeParser(IBitmapParser parser) {
        bitmapParsers.remove(parser);
    }

    public static void clearParsers() {
        bitmapParsers.clear();
    }

    public static void clearCache() {
        bitmapCache.evictAll();
    }
}
