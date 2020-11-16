package com.zpf.barrage.interfaces;

import com.zpf.barrage.model.DrawInfo;

public interface IDataParser {
    DrawInfo parseData(IDanmakuTypeBean source);
}
