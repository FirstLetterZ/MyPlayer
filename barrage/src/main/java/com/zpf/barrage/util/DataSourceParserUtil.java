package com.zpf.barrage.util;

import com.zpf.barrage.interfaces.IDanmakuTypeBean;
import com.zpf.barrage.interfaces.IDataParser;
import com.zpf.barrage.model.DrawInfo;
import java.util.HashSet;

public class DataSourceParserUtil {
    private static final HashSet<IDataParser> dataParsers = new HashSet<>();

    public static DrawInfo parseData(Object obj) {
        if (!(obj instanceof IDanmakuTypeBean)) {
            return null;
        }
        DrawInfo result;
        for (IDataParser p : dataParsers) {
            result = p.parseData((IDanmakuTypeBean) obj);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public static void addParser(IDataParser parser) {
        dataParsers.add(parser);
    }

    public static void removeParser(IDataParser parser) {
        dataParsers.remove(parser);
    }

    public static void clearParsers() {
        dataParsers.clear();
    }

}
