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

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Interpolator;

import com.tr4android.support.extension.animation.AnimationUtils;

public class CollapsingDrawableHelper {
    private final View mView;

    private boolean mDrawIcon;
    private float mExpandedFraction;

    private final Rect mExpandedBounds;
    private final Rect mCollapsedBounds;
    private final RectF mCurrentBounds;

    private float mExpandedIconSize;
    private float mCollapsedIconSize;

    private Drawable mDrawable;
    
    private Interpolator mIconSizeInterpolator;
    private Interpolator mPositionInterpolator;
    
    public CollapsingDrawableHelper(View view) {
        mView = view;

        mCollapsedBounds = new Rect();
        mExpandedBounds = new Rect();
        mCurrentBounds = new RectF();
    }
    
    void setIconSizeInterpolator(Interpolator interpolator) {
        mIconSizeInterpolator = interpolator;
        recalculate();
    }

    void setExpandedIconSize(float iconSize) {
        if (mExpandedIconSize != iconSize) {
            mExpandedIconSize = iconSize;
            recalculate();
        }
    }

    void setCollapsedIconSize(float iconSize) {
        if (mCollapsedIconSize != iconSize) {
            mCollapsedIconSize = iconSize;
            recalculate();
        }
    }

    void setExpandedBounds(int left, int top, int right, int bottom) {
        if (!rectEquals(mExpandedBounds, left, top, right, bottom)) {
            mExpandedBounds.set(left, top, right, bottom);
            onBoundsChanged();
        }
    }

    void setCollapsedBounds(int left, int top, int right, int bottom) {
        if (!rectEquals(mCollapsedBounds, left, top, right, bottom)) {
            mCollapsedBounds.set(left, top, right, bottom);
            onBoundsChanged();
        }
    }
    
    void onBoundsChanged() {
        mDrawIcon = mCollapsedBounds.width() > 0 && mCollapsedBounds.height() > 0
                && mExpandedBounds.width() > 0 && mExpandedBounds.height() > 0;
    }

    /**
     * Set the value indicating the current scroll value. This decides how much of the
     * background will be displayed, as well as the title metrics/positioning.
     *
     * A value of {@code 0.0} indicates that the layout is fully expanded.
     * A value of {@code 1.0} indicates that the layout is fully collapsed.
     */
    void setExpansionFraction(float fraction) {
        fraction = constrain(fraction, 0f, 1f);

        if (fraction != mExpandedFraction) {
            mExpandedFraction = fraction;
            calculateCurrentOffsets();
        }
    }

    float getExpansionFraction() {
        return mExpandedFraction;
    }

    float getCollapsedIconSize() {
        return mCollapsedIconSize;
    }

    float getExpandedIconSize() {
        return mExpandedIconSize;
    }

    private void calculateCurrentOffsets() {
        calculateOffsets(mExpandedFraction);
    }

    private void calculateOffsets(final float fraction) {
        interpolateBounds(fraction);

        if (mDrawable != null) {
            // Set new bounds for the drawable
            mDrawable.setBounds(Math.round(mCurrentBounds.left), Math.round(mCurrentBounds.top),
                    Math.round(mCurrentBounds.right), Math.round(mCurrentBounds.bottom));
        }

        ViewCompat.postInvalidateOnAnimation(mView);
    }

    private void interpolateBounds(float fraction) {
        mCurrentBounds.left = lerp(mExpandedBounds.left, mCollapsedBounds.left,
                fraction, mPositionInterpolator);
        mCurrentBounds.top = lerp(mExpandedBounds.top, mCollapsedBounds.top,
                fraction, mPositionInterpolator);
        mCurrentBounds.right = lerp(mExpandedBounds.right, mCollapsedBounds.right,
                fraction, mPositionInterpolator);
        mCurrentBounds.bottom = lerp(mExpandedBounds.bottom, mCollapsedBounds.bottom,
                fraction, mPositionInterpolator);
    }

    public void draw(Canvas canvas) {
        final int saveCount = canvas.save();

        if (mDrawable != null && mDrawIcon) {
            // Let the drawable draw to the canvas (bounds are already set)
            mDrawable.draw(canvas);
        }

        canvas.restoreToCount(saveCount);
    }

    public void recalculate() {
        if (mView.getHeight() > 0 && mView.getWidth() > 0) {
            // If we've already been laid out, calculate everything now otherwise we'll wait
            // until a layout
            calculateCurrentOffsets();
        }
    }

    /**
     * Set the drawable to display
     *
     * @param drawable
     */
    void setDrawable(Drawable drawable) {
        if (drawable == null || !drawable.equals(mDrawable)) {
            mDrawable = drawable;
            recalculate();
        }
    }

    Drawable getDrawable() {
        return mDrawable;
    }

    private static float lerp(float startValue, float endValue, float fraction,
                              Interpolator interpolator) {
        if (interpolator != null) {
            fraction = interpolator.getInterpolation(fraction);
        }
        return AnimationUtils.lerp(startValue, endValue, fraction);
    }

    private static boolean rectEquals(Rect r, int left, int top, int right, int bottom) {
        return !(r.left != left || r.top != top || r.right != right || r.bottom != bottom);
    }

    private static float constrain(float amount, float low, float high) {
        return amount < low ? low : (amount > high ? high : amount);
    }
}
