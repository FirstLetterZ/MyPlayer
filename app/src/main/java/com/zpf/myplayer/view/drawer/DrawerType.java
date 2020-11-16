package com.zpf.myplayer.view.drawer;

import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@IntDef(value = {
        DrawerType.DRAW_ROLL,
        DrawerType.DRAW_TOP,
        DrawerType.DRAW_BOTTOM,
        DrawerType.DRAW_FONT
})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER,ElementType.FIELD})
public @interface DrawerType {
    int DRAW_ROLL = 1;
    int DRAW_TOP = 2;
    int DRAW_BOTTOM = 3;
    int DRAW_FONT = 4;
}
