package com.zpf.myplayer.view;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;

import com.zpf.myplayer.view.bean.DanmakuNetBean;
import com.zpf.myplayer.view.bean.DrawInfo;
import com.zpf.myplayer.view.drawer.DrawInfoDataLoader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class DrawInfoDispatcher implements DrawInfoDataLoader {
    private ArrayList<LinkedList<DanmakuNetBean>> typeDataList;
    private Random random = new Random();
    private Context appContext;

    public DrawInfoDispatcher(Context context) {
        if (context instanceof Application) {
            appContext = context;
        } else {
            appContext = context.getApplicationContext();
        }
        typeDataList = new ArrayList<>();
        typeDataList.add(new LinkedList<>());
        typeDataList.add(new LinkedList<>());
        typeDataList.add(new LinkedList<>());
        typeDataList.add(new LinkedList<>());
    }

    @Override
    public DrawInfo pollByType(int type) {
        DrawInfo result = null;
        if (type > 0 && type - 1 < typeDataList.size()) {
            DanmakuNetBean netBean = typeDataList.get(type - 1).pollFirst();
            result = parseData(netBean);
        }
        return result;
    }

    private DrawInfo parseData(DanmakuNetBean netBean) {
        if (netBean == null) {
            return null;
        }
        DrawInfo result = new DrawInfo();
        result.content = netBean.content;
        result.bgRadius = netBean.bgRadius;
        result.underLine = netBean.underLine;
        result.linkUrl = netBean.linkUrl;
        if (netBean.startIconPath != null) {
            result.startBitmap = IconParser.parseBitmap(appContext, netBean.startIconPath);
        }
        if (netBean.endIconPath != null) {
            result.endBitmap = IconParser.parseBitmap(appContext, netBean.endIconPath);
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

    public void clearDataList() {
        for (List<DanmakuNetBean> list : typeDataList) {
            if (list != null) {
                list.clear();
            }
        }
    }

    public void addDataList(List<DanmakuNetBean> list) {
        if (list != null) {
            for (DanmakuNetBean bean : list) {
                if (bean != null) {
                    int index = bean.type - 1;
                    if (index >= 0 && index < typeDataList.size()) {
                        typeDataList.get(index).add(bean);
                    }
                }
            }
        }
    }

    public void addData(DanmakuNetBean bean, boolean insertEnd) {
        if (bean == null) {
            return;
        }
        int index = bean.type - 1;
        if (index >= 0 && index < typeDataList.size()) {
            if (insertEnd) {
                typeDataList.get(index).add(bean);
            } else {
                typeDataList.get(index).add(0, bean);
            }
        }
    }
}
