package com.zpf.myplayer.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.LruCache;

import com.zpf.myplayer.R;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;

public class IconParser {

    public static final String CACHE = "cache";
    public static final String ASSETS = "assets";
    public static final String EXTERNAL = "external";
    public static final String RESOURCE = "resource";
    private static final LruCache<String, SoftReference<Bitmap>> bitmapCache = new LruCache<>(10);

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
            result = bitmapCache.get(path).get();
        } catch (Exception e) {
            //
        }
        if (result != null) {
            return result;
        }
        if (RESOURCE.equals(realUri.getScheme())) {
            if ("like_blue".equals(path)) {
                result = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_like_blue);
            } else if ("follow_yellow".equals(path)) {
                result = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_community_notice_follow);
            } else if ("female_pink".equals(path)) {
                result = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_female_pink);
            } else if ("male_blue".equals(path)) {
                result = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_male_blue);
            } else if ("hint_orange".equals(path)) {
                result = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_orange_hint);
            } else if ("horn_green".equals(path)) {
                result = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_horn_green);
            } else if ("crown".equals(path)) {
                result = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_vip_crown);
            }
        } else if (ASSETS.equals(realUri.getScheme())) {
            try {
                InputStream inputStream = context.getAssets().open(path);
                result = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                //
            }
        } else if (CACHE.equals(realUri.getScheme())) {
            result = BitmapFactory.decodeFile(context.getFilesDir() + "/" + path);
        } else if (EXTERNAL.equals(realUri.getScheme())) {
            result = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + path);
        }
        if (result != null) {
            bitmapCache.put(path, new SoftReference<>(result));
        }
        return result;
    }
}
