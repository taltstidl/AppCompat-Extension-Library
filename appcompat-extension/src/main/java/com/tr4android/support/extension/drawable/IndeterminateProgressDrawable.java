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

package com.tr4android.support.extension.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.view.animation.Interpolator;

import com.tr4android.appcompat.extension.R;
import com.tr4android.support.extension.animation.AnimationUtils;
import com.tr4android.support.extension.animation.ValueAnimatorCompat;
import com.tr4android.support.extension.utils.ThemeUtils;

public class IndeterminateProgressDrawable extends Drawable implements Animatable {
    private static final String TAG = "Indeterminate Progress";

    // Interpolator used for path start
    private static final Interpolator PATH_START_INTERPOLATOR;
    static {
        Path mPathStart = new Path();
        mPathStart.lineTo(0.5f, 0);
        mPathStart.cubicTo(0.7f, 0, 0.6f, 1, 1, 1);
        PATH_START_INTERPOLATOR = PathInterpolatorCompat.create(mPathStart);
    }

    // Interpolator used for path end
    private static final Interpolator PATH_END_INTERPOLATOR;
    static {
        Path mPathEnd = new Path();
        mPathEnd.cubicTo(0.2f, 0, 0.1f, 1, 0.5f, 1);
        mPathEnd.lineTo(1, 1);
        PATH_END_INTERPOLATOR = PathInterpolatorCompat.create(mPathEnd);
    }

    // Paint
    private Paint mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mArcStrokeWidth = -1f; // auto
    private float mArcPadding = -1f; // auto
    private RectF mArcRect = new RectF();

    // Values that determine the current drawing state
    private int mRotationCount = 0;
    private float mRotation;
    private float mOffset;
    private float mStart;
    private float mEnd;

    // Animator
    private ValueAnimatorCompat mAnimator;

    public IndeterminateProgressDrawable(Context context, @ColorInt int color, float stroke, float padding) {
        mArcStrokeWidth = stroke;
        mArcPadding = padding;

        // The paint used to draw the spinning wheel
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeCap(Paint.Cap.SQUARE);
        mArcPaint.setStrokeJoin(Paint.Join.MITER);
        mArcPaint.setColor(color);

        // The animator used to animate the spinning wheel (works back to API 7)
        mAnimator = AnimationUtils.createAnimator();
        mAnimator.setFloatValues(0f, 0.25f);
        mAnimator.setDuration(1333);
        mAnimator.setInterpolator(AnimationUtils.LINEAR_INTERPOLATOR);
        mAnimator.setRepeatCount(ValueAnimatorCompat.INFINITE);
        mAnimator.setUpdateListener(new ValueAnimatorCompat.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimatorCompat animator) {
                mOffset = animator.getAnimatedFloatValue();
                float fraction = animator.getAnimatedFraction();
                mStart = PATH_START_INTERPOLATOR.getInterpolation(fraction) * 0.75f;
                mEnd = PATH_END_INTERPOLATOR.getInterpolation(fraction) * 0.75f;
                mRotation = (mRotationCount * 144) + mOffset * 576;
                invalidateSelf();
            }
        });
        mAnimator.setListener(new ValueAnimatorCompat.AnimatorListener() {
            @Override
            public void onAnimationStart(ValueAnimatorCompat animator) {
            }

            @Override
            public void onAnimationEnd(ValueAnimatorCompat animator) {
            }

            @Override
            public void onAnimationCancel(ValueAnimatorCompat animator) {
            }

            @Override
            public void onAnimationRepeat(ValueAnimatorCompat animator) {
                // Update rotation count (rotation does 1 cycle for every 5 animator cycles)
                mRotationCount = (mRotationCount + 1) % 5;
            }
        });
    }

    @Override
    public void draw(Canvas canvas) {
        int saveCount = canvas.save();
        canvas.rotate(mRotation, mArcRect.centerX(), mArcRect.centerY());

        float startAngle = -90 + 360 * (mOffset + mStart);
        float sweepAngle = 360 * (mEnd - mStart);
        canvas.drawArc(mArcRect, startAngle, sweepAngle, false, mArcPaint);

        canvas.restoreToCount(saveCount);
    }

    @Override
    public void setBounds(Rect bounds) {
        calculateArcMetrics(bounds);
        super.setBounds(bounds);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        calculateArcMetrics(new Rect(left, top, right, bottom));
        super.setBounds(left, top, right, bottom);
    }

    @Override
    public void setAlpha(int i) {
        mArcPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mArcPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void start() {
        mAnimator.start();
    }

    @Override
    public void stop() {
        mAnimator.cancel();
    }

    @Override
    public boolean isRunning() {
        return mAnimator.isRunning();
    }

    /**
     * Helper that calculates the bounds and the stroke width of the progress arc
     *
     * @param bounds the bounds of the drawable
     */
    private void calculateArcMetrics(Rect bounds) {
        float size = Math.min(bounds.height(), bounds.width());
        float yOffset = (bounds.height() - size) / 2f;
        float xOffset = (bounds.width() - size) / 2f;

        float strokeWidth;
        float padding;
        if (mArcStrokeWidth == -1f && mArcPadding == -1f) {
            // auto calculate bounds
            strokeWidth = 4f / 48f * size;
            padding = 5f / 48f * size;
        } else if (mArcStrokeWidth == -1f) {
            // auto calculate stroke width
            strokeWidth = 4f / 48f * size;
            padding = mArcPadding + strokeWidth / 2;
        } else if (mArcPadding == -1f) {
            // auto calculate padding
            strokeWidth = mArcStrokeWidth;
            padding = 3f / 48f * size + strokeWidth / 2;
        } else {
            strokeWidth = mArcStrokeWidth;
            padding = mArcPadding + strokeWidth / 2;
        }
        mArcPaint.setStrokeWidth(strokeWidth);
        mArcRect.set(bounds.left + padding + xOffset, bounds.top + padding + yOffset,
                bounds.right - padding - xOffset, bounds.bottom - padding - yOffset);
    }

    public static class Builder {
        private Context mContext;
        private int mColor;
        private float mPadding;
        private float mStrokeWidth;

        public Builder(Context context) {
            mContext = context;
            // Default values
            mColor = ThemeUtils.getThemeAttrColor(mContext, R.attr.colorAccent);
            mPadding = -1f; // auto calculates
            mStrokeWidth = -1f; //auto calculates
        }

        public Builder setColor(@ColorInt int color) {
            mColor = color;
            return this;
        }

        public Builder setPadding(float padding) {
            mPadding = padding;
            return this;
        }

        public Builder setStrokeWidth(float strokeWidth) {
            mStrokeWidth = strokeWidth;
            return this;
        }

        public IndeterminateProgressDrawable build() {
            return new IndeterminateProgressDrawable(mContext, mColor, mStrokeWidth, mPadding);
        }
    }
}
