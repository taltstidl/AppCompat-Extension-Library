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
package com.tr4android.support.extension.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.tr4android.appcompat.extension.R;
import com.tr4android.support.extension.animation.AnimationUtils;
import com.tr4android.support.extension.drawable.RoundRectDrawable;
import com.tr4android.support.extension.drawable.ShadowDrawableWrapper;
import com.tr4android.support.extension.utils.ViewCompatUtils;

/**
 * A TextView subclass that supports elevation and touch feedback pre-Lollipop
 * and has easy-to-use animations
 */
public class LabelView extends TextView {
    private static final int SHOW_HIDE_ANIM_DURATION = 200;

    private int mBackgroundColor;
    private int mRippleColor;
    private float mCornerRadius;
    private float mElevation;

    private boolean mIsHiding;
    private float mAnimationOffset;

    public LabelView(Context context) {
        this(context, null);
    }

    public LabelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray attr =  context.obtainStyledAttributes(
                attrs, R.styleable.LabelView, defStyleAttr, 0);
        // Background values
        mBackgroundColor = attr.getColor(R.styleable.LabelView_labelBackgroundColor, 0);
        mRippleColor = attr.getColor(R.styleable.LabelView_labelRippleColor, 0);
        mCornerRadius = attr.getDimension(R.styleable.LabelView_labelCornerRadius, 0f);
        mElevation = attr.getDimension(R.styleable.LabelView_labelElevation, 0f);
        // Padding values
        int paddingHorizontal = attr.getDimensionPixelSize(
                R.styleable.LabelView_labelPaddingHorizontal, 0);
        int paddingVertical = attr.getDimensionPixelSize(
                R.styleable.LabelView_labelPaddingVertical, 0);
        attr.recycle();

        setFocusable(true);
        setClickable(true);
        initBackground();
        setCompatPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
    }

    /**
     * Sets the translation distance used for the show and hide animations
     * @param offset the offset for animations
     */
    public void setAnimationOffset(float offset) {
        mAnimationOffset = offset;
    }

    /**
     * Gets the translation distance used for the show and hide animations
     * @return the offset for animations
     */
    public float getAnimationOffset() {
        return mAnimationOffset;
    }

    /**
     * Compatibility method for properly setting padding
     * This takes into account the additional padding required by the shadow on pre-Lollipop devices
     */
    public void setCompatPadding(int left, int top, int right, int bottom) {
        // Adjust padding by shadow
        final Rect shadowPadding = new Rect();
        getBackground().getPadding(shadowPadding);
        setPadding(left + shadowPadding.left, top + shadowPadding.top,
                right + shadowPadding.right, bottom + shadowPadding.bottom);
    }

    /**
     * Hides the label using a translation animation
     */
    public void hide() {
        if (mIsHiding || getVisibility() != VISIBLE) {
            // Already hiding or hidden, skip the call
            return;
        }
        if (!ViewCompat.isLaidOut(this)) {
            // View isn't laid out yet
            setVisibility(GONE);
        }
        // Use platform dependent animations
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            hideIceCreamSandwich();
        } else {
            hideEclairMr1();
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void hideIceCreamSandwich() {
        animate().cancel();
        animate().translationX(mAnimationOffset).alpha(0f)
                .setDuration(SHOW_HIDE_ANIM_DURATION)
                .setInterpolator(AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR)
                .setListener(new AnimatorListenerAdapter() {
                    private boolean mCancelled;

                    @Override
                    public void onAnimationStart(Animator animation) {
                        mIsHiding = true;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        mIsHiding = false;
                        mCancelled = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mIsHiding = false;
                        // cancelled animations don't change visibility
                        if (!mCancelled) {
                            setVisibility(GONE);
                        }
                    }
                });
    }

    private void hideEclairMr1() {
        Animation anim = createAnimationSet(1.0f, 0.0f, 0, Math.round(mAnimationOffset));
        anim.setInterpolator(AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR);
        anim.setDuration(SHOW_HIDE_ANIM_DURATION);
        anim.setAnimationListener(new AnimationUtils.AnimationListenerAdapter() {
            @Override
            public void onAnimationStart(Animation animation) {
                mIsHiding = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsHiding = false;
                setVisibility(GONE);
            }
        });
        startAnimation(anim);
    }

    /**
     * Shows the label using a translation animation
     */
    public void show() {
        if (!mIsHiding && getVisibility() == VISIBLE) {
            // Already showing or shown, skip the call
            return;
        }
        if (!ViewCompat.isLaidOut(this)) {
            // View isn't laid out yet
            return;
        }
        // Use platform dependent animations
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            showIceCreamSandwich();
        } else {
            showEclairMr1();
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void showIceCreamSandwich() {
        animate().cancel();
        if (getVisibility() != VISIBLE) {
            setTranslationX(mAnimationOffset);
            setAlpha(0f);
        }
        animate().translationX(0f).alpha(1f)
                .setDuration(SHOW_HIDE_ANIM_DURATION)
                .setInterpolator(AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        setVisibility(VISIBLE);
                    }
                });
    }

    private void showEclairMr1() {
        clearAnimation();
        setVisibility(VISIBLE);
        Animation anim = createAnimationSet(0.0f, 1.0f, Math.round(mAnimationOffset), 0);
        anim.setDuration(SHOW_HIDE_ANIM_DURATION);
        anim.setInterpolator(AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR);
        startAnimation(anim);
    }

    private void initBackground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            initBackgroundLollipop();
        } else {
            initBackgroundEclairMr1();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initBackgroundLollipop() {
        RoundRectDrawable shapeDrawable = new RoundRectDrawable(mBackgroundColor, mCornerRadius);
        RippleDrawable rippleDrawable = new RippleDrawable(ColorStateList.valueOf(mRippleColor),
                shapeDrawable, null);

        setElevation(mElevation);
        ViewCompatUtils.setBackground(this, rippleDrawable);
    }

    private void initBackgroundEclairMr1() {
        RoundRectDrawable shapeDrawable = new RoundRectDrawable(mBackgroundColor, mCornerRadius);
        shapeDrawable.setRoundRectTintList(createColorStateList(mRippleColor, mBackgroundColor));

        // Shadow drawable that wraps the above drawable
        ShadowDrawableWrapper shadowDrawable = new ShadowDrawableWrapper(
                getResources(),
                shapeDrawable,
                mCornerRadius,
                mElevation, mElevation);
        shadowDrawable.setAddPaddingForCorners(false);
        ViewCompatUtils.setBackground(this, shadowDrawable);
    }

    // Helper that creates an AnimationSet with the specified translation
    private static Animation createAnimationSet(float fromAlpha, float toAlpha, int fromXDelta, int toXDelta) {
        AnimationSet anim = new AnimationSet(true);
        anim.addAnimation(new AlphaAnimation(fromAlpha, toAlpha));
        anim.addAnimation(new TranslateAnimation(fromXDelta, toXDelta, 0, 0));
        return anim;
    }

    // Helper that creates a ColorStateList with the selected ripple color
    private static ColorStateList createColorStateList(int rippleColor, int backgroundColor) {
        final int compositeColor = ColorUtils.compositeColors(rippleColor, backgroundColor);
        return new ColorStateList(new int[][]{ // states
                new int[]{android.R.attr.state_focused},
                new int[]{android.R.attr.state_pressed},
                new int[]{} // state_default
        }, new int[]{ // colors
                compositeColor,
                compositeColor,
                backgroundColor
        });
    }
}
