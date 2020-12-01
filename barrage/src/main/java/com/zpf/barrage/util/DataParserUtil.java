package com.zpf.barrage.util;

import com.zpf.barrage.interfaces.IDanmakuTypeBean;
import com.zpf.barrage.interfaces.IDataParser;
import com.zpf.barrage.model.DanmakuItemInfo;

import java.util.HashSet;

public class DataParserUtil {
    private static final HashSet<IDataParser<IDanmakuTypeBean,DanmakuItemInfo>> dataParsers = new HashSet<>();

    public static DanmakuItemInfo parseData(Object obj) {
        if (!(obj instanceof IDanmakuTypeBean)) {
            return null;
        }
        DanmakuItemInfo result;
        for (IDataParser<IDanmakuTypeBean,DanmakuItemInfo> p : dataParsers) {
            result = p.parseData((IDanmakuTypeBean) obj);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public static void addParser(IDataParser<IDanmakuTypeBean,DanmakuItemInfo> parser) {
        dataParsers.add(parser);
    }

    public static void removeParser(IDataParser<IDanmakuTypeBean,DanmakuItemInfo> parser) {
        dataParsers.remove(parser);
    }

    public static void clearParsers() {
        dataParsers.clear();
    }

}
