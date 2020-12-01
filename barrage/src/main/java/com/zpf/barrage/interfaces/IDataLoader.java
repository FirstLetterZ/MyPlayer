package com.zpf.barrage.interfaces;

public interface IDataLoader<T> {
    T pollByType(int type);
}
