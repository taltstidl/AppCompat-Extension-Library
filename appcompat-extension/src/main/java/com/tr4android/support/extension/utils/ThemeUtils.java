package com.tr4android.support.extension.utils;

import android.content.Context;
import android.content.res.TypedArray;

/**
 * Created by Athos on 01/12/2015.
 */
public class ThemeUtils {

    public static int getThemeAttrColor(Context context, int attr) {
        int[] TEMP_ARRAY = new int[1];
        TEMP_ARRAY[0] = attr;
        TypedArray a = context.obtainStyledAttributes(null, TEMP_ARRAY);
        try {
            return a.getColor(0, 0);
        } finally {
            a.recycle();
        }
    }
}
