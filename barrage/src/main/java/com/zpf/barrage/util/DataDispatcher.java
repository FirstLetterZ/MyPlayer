package com.zpf.barrage.util;

import android.util.SparseArray;

import com.zpf.barrage.interfaces.IDanmakuTypeBean;
import com.zpf.barrage.interfaces.IDataLoader;
import com.zpf.barrage.model.DrawInfo;

import java.util.LinkedList;
import java.util.List;

public class DataDispatcher implements IDataLoader {
    private SparseArray<LinkedList<DrawInfo>> typeDataArray = new SparseArray<>();

    @Override
    public DrawInfo pollByType(int type) {
        DrawInfo result = null;
        LinkedList<DrawInfo> list = typeDataArray.get(type);
        if (list != null) {
            result = list.pollFirst();
        }
        return result;
    }

    public void addDataList(List<? extends IDanmakuTypeBean> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        for (IDanmakuTypeBean bean : list) {
            if (bean == null) {
                continue;
            }
            DrawInfo info = DataSourceParserUtil.parseData(bean);
            if (info == null) {
                continue;
            }
            int type = bean.getType();
            LinkedList<DrawInfo> typeInfoList = typeDataArray.get(type);
            if (typeInfoList == null) {
                typeInfoList = new LinkedList<>();
                typeDataArray.put(type, typeInfoList);
            }
             typeInfoList.add(info);
        }
    }

    public void clearDataList() {
        typeDataArray.clear();
    }

    public void addData(IDanmakuTypeBean data, boolean insertEnd) {
        if (data == null) {
            return;
        }
        DrawInfo info = DataSourceParserUtil.parseData(data);
        if (info == null) {
            return;
        }
        int type = data.getType();
        LinkedList<DrawInfo> typeInfoList = typeDataArray.get(type);
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
}
