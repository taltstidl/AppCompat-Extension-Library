/*
 * Copyright (C) 2016 Thomas Robert Altstidl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tr4android.support.extension.picker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.TypedValue;
import android.widget.ImageButton;

import com.tr4android.appcompat.extension.R;
import com.tr4android.support.extension.utils.ThemeUtils;

/**
 * Since Android versions prior to Lollipop don't support references in color resources
 * (e.g. `?attr/colorAccent`) theming must be done at runtime.
 * <p/>
 * This helper class has methods to easily create all the ColorStateLists needed for
 * the AppCompatDatePickerDialog and the AppCompatTimePickerDialog.
 */
public class PickerThemeUtils {

    public static ColorStateList getHeaderTextColorStateList(Context context) {
        return new ColorStateList(new int[][]{ // states
                new int[]{android.R.attr.state_selected},
                new int[]{} // state_default
        }, new int[]{ // colors
                ContextCompat.getColor(context, R.color.abc_primary_text_material_dark),
                ContextCompat.getColor(context, R.color.abc_secondary_text_material_dark)
        });
    }

    public static Drawable getHeaderBackground(Context context, @ColorInt int color) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // Create a background drawable with 2dp corners
            GradientDrawable drawable = (GradientDrawable)
                    ContextCompat.getDrawable(context, R.drawable.picker_header_background);
            drawable.setColor(color);
            return drawable;
        } else {
            return new ColorDrawable(color);
        }
    }

    public static ColorStateList getTextColorPrimaryActivatedStateList(Context context) {
        final float disabledAlpha = getDisabledAlpha(context);
        final int textColor = ThemeUtils.getThemeAttrColor(context, android.R.attr.textColorPrimary);
        final int textColorActivated = ContextCompat.getColor(context, R.color.abc_primary_text_material_dark);
        return new ColorStateList(new int[][]{ // states
                new int[]{-android.R.attr.state_enabled, android.R.attr.state_selected},
                new int[]{-android.R.attr.state_enabled},
                new int[]{android.R.attr.state_selected},
                new int[]{} // state_default
        }, new int[]{ // colors
                setAlphaComponent(textColorActivated, disabledAlpha),
                setAlphaComponent(textColor, disabledAlpha),
                textColorActivated,
                textColor
        });
    }

    public static ColorStateList getTextColorSecondaryActivatedStateList(Context context) {
        final float disabledAlpha = getDisabledAlpha(context);
        final int textColor = ThemeUtils.getThemeAttrColor(context, android.R.attr.textColorSecondary);
        final int textColorActivated = ContextCompat.getColor(context, R.color.abc_secondary_text_material_dark);
        return new ColorStateList(new int[][]{ // states
                new int[]{-android.R.attr.state_enabled, android.R.attr.state_selected},
                new int[]{-android.R.attr.state_enabled},
                new int[]{android.R.attr.state_selected},
                new int[]{} // state_default
        }, new int[]{ // colors
                setAlphaComponent(textColorActivated, disabledAlpha),
                setAlphaComponent(textColor, disabledAlpha),
                textColorActivated,
                textColor
        });
    }

    public static float getDisabledAlpha(Context context) {
        final TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.disabledAlpha, outValue, true);
        return outValue.getFloat();
    }

    public static int setAlphaComponent(int color, float alpha) {
        final int srcRgb = color & 0xFFFFFF;
        final int srcAlpha = (color >> 24) & 0xFF;
        final int dstAlpha = (int) (srcAlpha * alpha + 0.5f);
        return srcRgb | (dstAlpha << 24);
    }

    public static ColorStateList getNavButtonColorStateList(Context context) {
        return new ColorStateList(new int[][]{ // states
                new int[]{android.R.attr.state_pressed},
                new int[]{android.R.attr.state_focused},
                new int[]{} // state_default
        }, new int[]{ //colors
                ThemeUtils.getThemeAttrColor(context, R.attr.colorControlHighlight),
                ThemeUtils.getThemeAttrColor(context, R.attr.colorControlHighlight),
                ContextCompat.getColor(context, android.R.color.transparent)
        });
    }

    public static Drawable getNavButtonBackground(Context context) {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.date_picker_nav_background);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // Proxy the highlight color into the background drawable
            drawable = DrawableCompat.wrap(drawable.mutate());
            DrawableCompat.setTintList(drawable, getNavButtonColorStateList(context));
        }
        return drawable;
    }

    public static void setNavButtonDrawable(Context context, ImageButton left, ImageButton right,
                                            int monthTextAppearanceResId) {
        // Retrieve the previous and next drawables dependent on layout direction
        Drawable prevDrawable = ContextCompat.getDrawable(context, R.drawable.ic_chevron_left);
        Drawable nextDrawable = ContextCompat.getDrawable(context, R.drawable.ic_chevron_right);

        // Proxy the month text color into the previous and next drawables.
        final TypedArray ta = context.obtainStyledAttributes(null,
                new int[]{android.R.attr.textColor}, 0, monthTextAppearanceResId);
        final ColorStateList monthColor = ta.getColorStateList(0);
        if (monthColor != null) {
            DrawableCompat.setTint(DrawableCompat.wrap(prevDrawable), monthColor.getDefaultColor());
            DrawableCompat.setTint(DrawableCompat.wrap(nextDrawable), monthColor.getDefaultColor());
        }
        ta.recycle();

        // Set the previous and next drawables
        left.setImageDrawable(prevDrawable);
        right.setImageDrawable(nextDrawable);
    }
}
