/*
 * Copyright (C) 2014 The Android Open Source Project
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

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * Very simple drawable that draws a rounded rectangle background with arbitrary corners and also
 * reports proper outline for Lollipop.
 * <p>
 * Simpler and uses less resources compared to GradientDrawable or ShapeDrawable.
 */
public class RoundRectDrawable extends Drawable {
    private boolean mHasJellybeanMr1 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    private float mRadius;
    private final Paint mPaint;
    private ColorStateList mTint;
    private final RectF mCornerRect;
    private final RectF mBoundsF;
    private final Rect mBoundsI;
    private float mPadding;

    public RoundRectDrawable(int backgroundColor, float radius) {
        mRadius = radius;
        // increment it to account for half pixels.
        if (mHasJellybeanMr1 && mRadius >= 1f)
            mRadius += .5f;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(backgroundColor);
        mCornerRect = new RectF();
        mBoundsF = new RectF();
        mBoundsI = new Rect();
    }

    public void setPadding(float padding) {
        if (padding == mPadding) {
            return;
        }
        mPadding = padding;
        updateBounds(null);
        invalidateSelf();
    }

    public float getPadding() {
        return mPadding;
    }

    @Override
    public void draw(Canvas canvas) {
        if (mHasJellybeanMr1) {
            canvas.drawRoundRect(mBoundsF, mRadius, mRadius, mPaint);
        } else {
            drawRoundRectPath(canvas, mPaint);
        }
    }

    private void drawRoundRectPath(Canvas canvas, Paint paint) {
        final float twoRadius = mRadius * 2;
        final float innerWidth = mBoundsF.width() - twoRadius - 1;
        final float innerHeight = mBoundsF.height() - twoRadius - 1;
        if (mRadius >= 1f) {
            mCornerRect.set(-mRadius, -mRadius, mRadius, mRadius);
            int saved = canvas.save();
            canvas.translate(mBoundsF.left + mRadius, mBoundsF.top + mRadius);
            canvas.drawArc(mCornerRect, 180, 90, true, paint);
            canvas.translate(innerWidth, 0);
            canvas.rotate(90);
            canvas.drawArc(mCornerRect, 180, 90, true, paint);
            canvas.translate(innerHeight, 0);
            canvas.rotate(90);
            canvas.drawArc(mCornerRect, 180, 90, true, paint);
            canvas.translate(innerWidth, 0);
            canvas.rotate(90);
            canvas.drawArc(mCornerRect, 180, 90, true, paint);
            canvas.restoreToCount(saved);
            // draw top and bottom pieces
            canvas.drawRect(mBoundsF.left + mRadius - 1f, mBoundsF.top,
                    mBoundsF.right - mRadius + 1f, mBoundsF.top + mRadius,
                    paint);
            canvas.drawRect(mBoundsF.left + mRadius - 1f,
                    mBoundsF.bottom - mRadius + 1f, mBoundsF.right - mRadius + 1f,
                    mBoundsF.bottom, paint);
        }
        // draw center
        canvas.drawRect(mBoundsF.left, mBoundsF.top + Math.max(0, mRadius - 1f),
                mBoundsF.right, mBoundsF.bottom - mRadius + 1f, paint);
    }

    private void updateBounds(Rect bounds) {
        if (bounds == null) {
            bounds = getBounds();
        }
        mBoundsF.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
        mBoundsI.set(bounds);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        updateBounds(bounds);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void getOutline(Outline outline) {
        outline.setRoundRect(mBoundsI, mRadius);
    }

    public void setRadius(float radius) {
        if (radius == mRadius) {
            return;
        }
        mRadius = radius;
        updateBounds(null);
        invalidateSelf();
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public float getRadius() {
        return mRadius;
    }

    public void setRoundRectTint(int color) {
        mPaint.setColor(color);
        invalidateSelf();
    }
    
    public void setRoundRectTintList(ColorStateList tint) {
        mTint = tint;
        if (mTint != null) {
            mPaint.setColor(mTint.getColorForState(getState(), mPaint.getColor()));
        }
        invalidateSelf();
    }

    @Override
    protected boolean onStateChange(int[] stateSet) {
        if (mTint != null) {
            final int currentColor = mPaint.getColor();
            final int newColor = mTint.getColorForState(stateSet, currentColor);
            if (newColor != currentColor) {
                mPaint.setColor(newColor);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isStateful() {
        return (mTint != null && mTint.isStateful()) || super.isStateful();
    }
}
