package com.zpf.barrage.interfaces;

import java.util.List;

public interface IDataDispatcher {
    void addDataList(List<? extends IDanmakuTypeBean> list);

    void clearDataList();

    void addData(IDanmakuTypeBean data, boolean insertEnd);

    void setDrawTimeChecker(IDrawTimeChecker checker);

}
