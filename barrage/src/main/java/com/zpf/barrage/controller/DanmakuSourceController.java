package com.zpf.barrage.controller;

import android.util.SparseArray;

import com.zpf.barrage.bean.DanmakuNetBean;
import com.zpf.barrage.interfaces.IDanmakuTypeBean;
import com.zpf.barrage.interfaces.IDataDispatcher;
import com.zpf.barrage.interfaces.IDataLoader;
import com.zpf.barrage.interfaces.IDrawTimeChecker;
import com.zpf.barrage.model.DanmakuItemInfo;
import com.zpf.barrage.util.DataParserUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * 数据暂存，更新，解析，查找
 */
public class DanmakuSourceController implements IDataDispatcher, IDataLoader<DanmakuItemInfo> {
    private long lastDataRefreshTime = 0L;
    private SparseArray<LinkedList<DanmakuItemInfo>> typeDataArray = new SparseArray<>();
    private boolean autoClearStaleData = false;
    private IDrawTimeChecker drawTimeChecker;

    @Override
    public DanmakuItemInfo pollByType(int type) {
        DanmakuItemInfo result = null;
        LinkedList<DanmakuItemInfo> list = typeDataArray.get(type);
        if (list != null) {
            result = list.pollFirst();
        }
        if (result == null) {
            return null;
        }
        if (drawTimeChecker != null) {
            if (drawTimeChecker.checkShowTime(result)) {
                return result;
            } else {
                list.add(0, result);
                return null;
            }
        } else if (result.showTime <= System.currentTimeMillis()) {
            return result;
        } else {
            list.add(0, result);
            return null;
        }
    }

    @Override
    public void addDataList(List<? extends IDanmakuTypeBean> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        if (lastDataRefreshTime > 0 && autoClearStaleData && System.currentTimeMillis() - lastDataRefreshTime > 30 * 1000) {
            clearDataList();
        }
        lastDataRefreshTime = System.currentTimeMillis();
        for (IDanmakuTypeBean bean : list) {
            if (bean == null) {
                continue;
            }
            DanmakuItemInfo info = parseData(bean);
            if (info == null) {
                continue;
            }
            int type = bean.getType();
            LinkedList<DanmakuItemInfo> typeInfoList = typeDataArray.get(type);
            if (typeInfoList == null) {
                typeInfoList = new LinkedList<>();
                typeDataArray.put(type, typeInfoList);
            }
            typeInfoList.add(info);
        }
    }

    @Override
    public void clearDataList() {
        typeDataArray.clear();
    }

    @Override
    public void addData(IDanmakuTypeBean data, boolean insertEnd) {
        if (data == null) {
            return;
        }
        DanmakuItemInfo info = parseData(data);
        if (info == null) {
            return;
        }
        int type = data.getType();
        LinkedList<DanmakuItemInfo> typeInfoList = typeDataArray.get(type);
        if (typeInfoList == null) {
            typeInfoList = new LinkedList<>();
            typeDataArray.put(type, typeInfoList);
        }
        if (insertEnd) {
            typeInfoList.add(info);
        } else {
            typeInfoList.add(0, info);
        }
    }

    @Override
    public void setDrawTimeChecker(IDrawTimeChecker checker) {
        drawTimeChecker = checker;
    }

    public void setAutoClearStaleData(boolean autoClearStaleData) {
        this.autoClearStaleData = autoClearStaleData;
    }

    private DanmakuItemInfo parseData(IDanmakuTypeBean data) {
        if (data instanceof DanmakuNetBean) {
            return DataParserUtil.parseData(data);
        }
        return null;
    }
}
