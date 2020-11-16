package com.zpf.myplayer.view.drawer;

import com.zpf.myplayer.view.bean.DrawInfo;

public interface DrawInfoDataLoader {
    DrawInfo pollByType(int type);
}
