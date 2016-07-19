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

package com.tr4android.support.extension.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v4.graphics.ColorUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.tr4android.support.extension.animation.AnimationUtils;
import com.tr4android.support.extension.animation.ValueAnimatorCompat;

/**
 * A drawable that animates between two color values
 */
public class ColorTransitionDrawable extends Drawable {
    private float mColorRatio;
    private int mColor;
    private int mColorStart;
    private int mColorEnd;

    // Animator
    private ValueAnimatorCompat mAnimator;
    private Interpolator mStartInterpolator = new LinearInterpolator();
    private Interpolator mReverseInterpolator = mStartInterpolator;

    public ColorTransitionDrawable(@ColorInt int start, @ColorInt int end) {
        mColorRatio = 0f;
        mColor = start;
        mColorStart = start;
        mColorEnd = end;

        // The animator used to animate the transition
        mAnimator = AnimationUtils.createAnimator();
        mAnimator.setUpdateListener(new ValueAnimatorCompat.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimatorCompat animator) {
                setColorRatio(animator.getAnimatedFloatValue());
            }
        });
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(mColor);
    }

    @Override
    public void setAlpha(int i) {
        // non-functional
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        // non-functional
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public float getColorRatio() {
        return mColorRatio;
    }

    public void setColorRatio(float ratio) {
        mColorRatio = Math.max(0f, Math.min(ratio, 1f));
        mColor = ColorUtils.blendARGB(mColorStart, mColorEnd, mColorRatio);
        invalidateSelf();
    }

    public void startTransition(int duration) {
        mAnimator.cancel();
        mAnimator.setFloatValues(mColorRatio, 1f);
        mAnimator.setDuration(duration);
        mAnimator.setInterpolator(mStartInterpolator);
        mAnimator.start();
    }

    public void reverseTransition(int duration) {
        mAnimator.cancel();
        mAnimator.setFloatValues(mColorRatio, 0f);
        mAnimator.setDuration(duration);
        mAnimator.setInterpolator(mReverseInterpolator);
        mAnimator.start();
    }
}
