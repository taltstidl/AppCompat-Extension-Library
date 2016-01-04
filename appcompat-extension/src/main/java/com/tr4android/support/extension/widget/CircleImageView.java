/*
 * Copyright (C) 2015 Thomas Robert Altstidl
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

package com.tr4android.support.extension.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.tr4android.appcompat.extension.R;
import com.tr4android.support.extension.drawable.PlaceholderDrawable;
import com.tr4android.support.extension.utils.ThemeUtils;

/**
 * A supercharged ImageView that displays images as circles and creates placeholder images
 */
public class CircleImageView extends ImageView {
    private static final String LOG_TAG = "CircleImageView";

    // whether the image should be clipped to a circle
    private boolean mIsCircleImageEnabled = true;

    // whether an image drawable is being resolved
    private boolean mIsResolvingDrawable;

    // the placeholder drawable used for drawing the placeholder
    private PlaceholderDrawable mPlaceholderDrawable;

    // default color for circle
    private int mPlaceholderCircleDefaultColor;

    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0);
        mIsCircleImageEnabled = a.getBoolean(R.styleable.CircleImageView_circleImageEnabled, true);
        mPlaceholderCircleDefaultColor = a.getColor(R.styleable.CircleImageView_placeholderCircleColor, ThemeUtils.getThemeAttrColor(getContext(), R.attr.colorAccent));
        int mPlaceholderTextSize = a.getDimensionPixelSize(R.styleable.CircleImageView_placeholderTextSize, getResources().getDimensionPixelSize(R.dimen.defaultPlaceholderTextSize));
        int mPlaceholderTextColor = a.getColor(R.styleable.CircleImageView_placeholderTextColor, ThemeUtils.getThemeAttrColor(getContext(), android.R.attr.textColorPrimaryInverse));
        int mPlaceholderImageSize = a.getDimensionPixelSize(R.styleable.CircleImageView_placeholderIconSize, getResources().getDimensionPixelSize(R.dimen.defaultPlaceholderImageSize));
        a.recycle();

        mPlaceholderDrawable = new PlaceholderDrawable(
                mPlaceholderTextSize, mPlaceholderTextColor, mPlaceholderImageSize);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (mIsCircleImageEnabled) {
            setImageDrawable(getCircleBitmapDrawable(getContext(), bm));
        } else {
            super.setImageBitmap(bm);
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        if (mIsCircleImageEnabled && drawable != null &&
                !(drawable instanceof RoundedBitmapDrawable) && !(drawable instanceof PlaceholderDrawable)) {
            setImageDrawable(getCircleBitmapDrawable(getContext(), getBitmapFromDrawable(drawable)));
        } else {
            super.setImageDrawable(drawable);
        }
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        if (mIsCircleImageEnabled) {
            mIsResolvingDrawable = true;
            super.setImageResource(resId);
            mIsResolvingDrawable = false;
            setImageDrawable(getCircleBitmapDrawable(getContext(), getBitmapFromDrawable(getDrawable())));
        } else {
            super.setImageResource(resId);
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        if (mIsCircleImageEnabled) {
            mIsResolvingDrawable = true;
            super.setImageURI(uri);
            mIsResolvingDrawable = false;
            setImageDrawable(getCircleBitmapDrawable(getContext(), getBitmapFromDrawable(getDrawable())));
        } else {
            super.setImageURI(uri);
        }
    }

    /**
     * Set whether the image should be transformed to a circle or not
     *
     * @param enabled pass true to enable, false to disable circular images
     */
    public void setCircleImageEnabled(boolean enabled) {
        mIsCircleImageEnabled = enabled;
    }

    /**
     * Check whether the image is transformed to a circle or not
     *
     * @return true if circular images are enabled, false if they are disabled
     */
    public boolean isCircleImageEnabled() {
        return mIsCircleImageEnabled;
    }

    /**
     * Set a placeholder with a text. This will use the default circle color.
     * <p/>
     * Note: This usually should be the first letter of a name. You can use the helper {@link #retrieveLetter(String)}
     * to extract the first letter of a given {@link String}.
     *
     * @param placeholderText Placeholder text to display in the middle of the circle
     */
    public void setPlaceholder(String placeholderText) {
        setPlaceholder(placeholderText, mPlaceholderCircleDefaultColor);
    }

    /**
     * Set a placeholder with a text and circle color.
     * <p/>
     * Note: This usually should be the first letter of a name. You can use the helper {@link #retrieveLetter(String)}
     * to extract the first letter of a given {@link String}.
     *
     * @param placeholderText Placeholder text to display in the middle of the circle
     * @param circleColor     Color to use for the circle
     */
    public void setPlaceholder(String placeholderText, @ColorInt int circleColor) {
        mPlaceholderDrawable.setPlaceholder(placeholderText, circleColor);
        setImageDrawable(mPlaceholderDrawable);
    }

    /**
     * Set a placeholder with an image. This will use the default circle color.
     *
     * @param resId The resource id of the placeholder drawable
     */
    public void setPlaceholder(@DrawableRes int resId) {
        setPlaceholder(ContextCompat.getDrawable(getContext(), resId), mPlaceholderCircleDefaultColor);
    }

    /**
     * Set a placeholder with an image. This will use the default circle color.
     *
     * @param drawable The placeholder drawable
     */
    public void setPlaceholder(Drawable drawable) {
        setPlaceholder(drawable, mPlaceholderCircleDefaultColor);
    }

    /**
     * Set a placeholder with an image and circle color.
     *
     * @param resId       The resource id of the placeholder drawable
     * @param circleColor Color to use for the circle
     */
    public void setPlaceholder(@DrawableRes int resId, @ColorInt int circleColor) {
        setPlaceholder(ContextCompat.getDrawable(getContext(), resId), circleColor);
    }

    /**
     * Set a placeholder with an image and circle color.
     *
     * @param drawable    The placeholder drawable
     * @param circleColor Color to use for the circle
     */
    public void setPlaceholder(Drawable drawable, @ColorInt int circleColor) {
        mPlaceholderDrawable.setPlaceholder(drawable, circleColor);
        setImageDrawable(mPlaceholderDrawable);
    }

    /**
     * Small Helper for extracting the first letter of a {@link String}.
     *
     * @param text the full text representing e.g. a name
     * @return the first letter uppercase
     */
    public static String retrieveLetter(String text) {
        return String.valueOf(text.charAt(0)).toUpperCase();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mIsResolvingDrawable) return;
        super.onDraw(canvas);
    }

    /**
     * Helper for creating a circle bitmap drawable using the {@link android.support.v4.graphics.drawable.RoundedBitmapDrawable}
     *
     * @param bitmap The bitmap which should be converted to a circle bitmap drawable
     * @return the {@link android.support.v4.graphics.drawable.RoundedBitmapDrawable} containing the bitmap
     */
    public static RoundedBitmapDrawable getCircleBitmapDrawable(Context context, Bitmap bitmap) {
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
        drawable.setCornerRadius(Math.max(bitmap.getWidth() / 2, bitmap.getHeight() / 2));
        drawable.setAntiAlias(true);
        return drawable;
    }

    /**
     * Helper for creating a bitmap from a drawable
     *
     * @param drawable The drawable which should be converted to a bitmap
     * @return the bitmap containing the drawable
     */
    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) return null;
        if (drawable instanceof BitmapDrawable) {
            Log.w(LOG_TAG, "For better performance consider using setImageBitmap() instead!");
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            Bitmap bitmap = Bitmap.createBitmap(Math.max(2, drawable.getIntrinsicWidth()), Math.max(2, drawable.getIntrinsicHeight()), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }
}
