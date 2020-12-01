package com.zpf.barrage.bean;

import android.graphics.Color;

import com.zpf.barrage.interfaces.IDanmakuTypeBean;
import com.zpf.barrage.interfaces.IDataParser;
import com.zpf.barrage.model.DanmakuIconElement;
import com.zpf.barrage.model.DanmakuItemInfo;
import com.zpf.barrage.model.DanmakuTextElement;

import java.util.List;

public class DanmakuNetBeanParser implements IDataParser<IDanmakuTypeBean, DanmakuItemInfo> {

    private static class Instance {
        static DanmakuNetBeanParser parser = new DanmakuNetBeanParser();
    }

    public static DanmakuNetBeanParser get() {
        return DanmakuNetBeanParser.Instance.parser;
    }

    @Override
    public DanmakuItemInfo parseData(IDanmakuTypeBean source) {
        if (!(source instanceof DanmakuNetBean)) {
            return null;
        }
        DanmakuNetBean netBean = (DanmakuNetBean) source;
        DanmakuItemInfo result = new DanmakuItemInfo();
        result.showType = netBean.getType();
        result.bgRadius = netBean.bgRadius;
        result.responseInfo = netBean.linkUrl;
        if (netBean.startIconPath != null) {
            DanmakuIconElement iconElement = new DanmakuIconElement();
            iconElement.contentString = netBean.startIconPath;
            result.contentList.add(iconElement);
        }
        if (netBean.content != null && netBean.content.length() > 0) {
            DanmakuTextElement textElement = new DanmakuTextElement();
            textElement.contentString = netBean.content;
            result.contentList.add(textElement);
        }
        if (netBean.endIconPath != null) {
            DanmakuIconElement iconElement = new DanmakuIconElement();
            iconElement.contentString = netBean.endIconPath;
            result.contentList.add(iconElement);
        }
        if (netBean.bgColor != null) {
            try {
                result.bgColor = Color.parseColor(netBean.bgColor);
            } catch (Exception e) {
                //
            }
        }
        if (netBean.lineColor != null) {
            try {
                result.lineColor = Color.parseColor(netBean.lineColor);
            } catch (Exception e) {
                //
            }
            result.lineBottom = 0f;
        }
        List<String> fontColors = netBean.fontColors;
        if (fontColors == null) {
            result.textColors = null;
        } else {
            int[] ic = new int[fontColors.size()];
            for (int i = 0; i < fontColors.size(); i++) {
                try {
                    ic[i] = Color.parseColor(fontColors.get(i));
                } catch (Exception e) {
                    ic[i] = Color.WHITE;
                }
            }
            result.textColors = ic;
        }
        return result;
    }
}
