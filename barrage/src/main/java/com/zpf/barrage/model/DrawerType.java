package com.zpf.barrage.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER,ElementType.FIELD})
public @interface DrawerType {
    int DRAW_ROLL = 1;
    int DRAW_TOP = 2;
    int DRAW_BOTTOM = 3;
}
