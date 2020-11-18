package com.zpf.myplayer.util;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;
import com.zpf.tool.FileUtil;

import java.io.File;

public class CacheProxyUtil {

    private static volatile HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        if (proxy == null) {
            synchronized (CacheProxyUtil.class) {
                if (proxy == null) {
                    proxy = new HttpProxyCacheServer.Builder(context)
                            .cacheDirectory(new File(FileUtil.getAppDataPath(context)))
                            .build();
                }
            }
        }
        return proxy;
    }

}
