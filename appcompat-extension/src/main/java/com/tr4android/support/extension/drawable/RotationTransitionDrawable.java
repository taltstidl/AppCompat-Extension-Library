/*
 * Copyright (C) 2014 Jerzy Chalupski
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

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

/**
 * A drawable that animates the rotation and alpha value of the wrapped drawable
 */
public class RotationTransitionDrawable extends LayerDrawable {
    private float mRotation;
    private float mMaxRotation;

    private boolean mHasSecondDrawable;

    public RotationTransitionDrawable(Drawable drawable, Drawable closeDrawable) {
        super(closeDrawable == null ? new Drawable[]{drawable} : new Drawable[]{drawable, closeDrawable});
        mHasSecondDrawable = closeDrawable != null;
    }

    public float getRotation() {
        return mRotation;
    }

    public void setRotation(float rotation) {
        mRotation = rotation;
        invalidateSelf();
    }

    public float getMaxRotation() {
        return mMaxRotation;
    }

    public void setMaxRotation(float rotation) {
        mMaxRotation = rotation;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        if (mHasSecondDrawable) {
            int alpha = Math.min(Math.max(0, Math.round(mRotation / mMaxRotation * 255)), 255);
            canvas.rotate(mRotation, getBounds().centerX(), getBounds().centerY());
            getDrawable(0).setAlpha(255 - alpha);
            getDrawable(0).draw(canvas);
            canvas.rotate(-mMaxRotation, getBounds().centerX(), getBounds().centerY());
            getDrawable(1).setAlpha(alpha);
            getDrawable(1).draw(canvas);
        } else {
            canvas.rotate(mRotation, getBounds().centerX(), getBounds().centerY());
            super.draw(canvas);
        }
        canvas.restore();
    }
}
