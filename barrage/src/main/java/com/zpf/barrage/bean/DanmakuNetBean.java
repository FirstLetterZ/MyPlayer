package com.zpf.barrage.bean;

import com.zpf.barrage.interfaces.IDanmakuTypeBean;

import java.util.List;

public class DanmakuNetBean implements IDanmakuTypeBean {
    public List<String> fontColors;
    public String bgColor;
    public String lineColor;

    public String content;
    public float bgRadius;
    public int type;
    public String startIconPath;
    public String endIconPath;
    public long showTime;
    public boolean underLine;
    public String linkUrl;

    @Override
    public int getType() {
        return type;
    }
}
