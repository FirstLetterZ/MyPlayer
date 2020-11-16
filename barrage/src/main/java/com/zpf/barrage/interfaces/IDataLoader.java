package com.zpf.barrage.interfaces;

import com.zpf.barrage.model.DrawInfo;

public interface IDataLoader {
    DrawInfo pollByType(int type);
}
