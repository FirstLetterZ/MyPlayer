package com.zpf.barrage.bean;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;

import com.zpf.barrage.interfaces.IDanmakuTypeBean;
import com.zpf.barrage.interfaces.IDataParser;
import com.zpf.barrage.model.DrawInfo;
import com.zpf.barrage.util.IconBitmapParseUtil;

import java.util.List;
import java.util.Random;

public class DanmakuNetBeanParser implements IDataParser {
    private Random random = new Random();
    private Context appContext;

    public DanmakuNetBeanParser(Context context) {
        if (context instanceof Application) {
            appContext = context;
        } else {
            appContext = context.getApplicationContext();
        }
    }

    @Override
    public DrawInfo parseData(IDanmakuTypeBean source) {
        if (!(source instanceof DanmakuNetBean)) {
            return null;
        }
        DanmakuNetBean netBean = (DanmakuNetBean) source;
        DrawInfo result = new DrawInfo();
        result.content = netBean.content;
        result.bgRadius = netBean.bgRadius;
        result.underLine = netBean.underLine;
        result.linkUrl = netBean.linkUrl;
        if (netBean.startIconPath != null) {
            result.startBitmap = IconBitmapParseUtil.parseBitmap(appContext, netBean.startIconPath);
        }
        if (netBean.endIconPath != null) {
            result.endBitmap = IconBitmapParseUtil.parseBitmap(appContext, netBean.endIconPath);
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
        }
        List<String> fontColors = netBean.fontColors;
        if (fontColors == null) {
            result.colors = null;
        } else {
            int[] ic = new int[fontColors.size()];
            for (int i = 0; i < fontColors.size(); i++) {
                try {
                    ic[i] = Color.parseColor(fontColors.get(i));
                } catch (Exception e) {
                    ic[i] = Color.WHITE;
                }
            }
            result.colors = ic;
        }
        result.speed = (1.2f + 0.08f * random.nextInt(4)) * appContext.getResources().getDisplayMetrics().density;
        return result;
    }
}
