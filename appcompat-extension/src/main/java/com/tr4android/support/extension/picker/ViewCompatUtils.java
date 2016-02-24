package com.tr4android.support.extension.picker;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ViewCompatUtils {
    public static int getPaddingStart(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return view.getPaddingStart();
        } else {
            return view.getPaddingLeft();
        }
    }

    public static int getPaddingEnd(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return view.getPaddingEnd();
        } else {
            return view.getPaddingRight();
        }
    }

    public static void setBackground(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static void setTextAppearance(TextView view, int textAppearanceResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setTextAppearance(textAppearanceResId);
        } else {
            view.setTextAppearance(view.getContext(), textAppearanceResId);
        }
    }

    public static void announceForAccessibility(View view, CharSequence text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.announceForAccessibility(text);
        } // No-op on versions prior to Jellybean
    }

    public static boolean isLayoutRtl(View view) {
        return ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    public static int getRule(RelativeLayout.LayoutParams params, int verb) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return params.getRule(verb);
        } else {
            return params.getRules()[verb];
        }
    }

    public static void removeRule(RelativeLayout.LayoutParams params, int verb) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params.removeRule(verb);
        } else {
            params.addRule(verb, 0);
        }
    }
}
