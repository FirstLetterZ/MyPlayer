package com.zpf.barrage.interfaces;

public interface IDataParser<F, T> {
    T parseData(F source);
}