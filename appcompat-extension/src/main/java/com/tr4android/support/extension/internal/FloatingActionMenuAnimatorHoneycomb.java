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

package com.tr4android.support.extension.internal;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.tr4android.support.extension.drawable.RotationTransitionDrawable;
import com.tr4android.support.extension.widget.FloatingActionMenu;

/**
 * An FloatingActionMenu animator built for Honeycomb devices and above
 * Note: this one has full access to ObjectAnimator introduced in this API level
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FloatingActionMenuAnimatorHoneycomb implements FloatingActionMenuAnimator {
    // AnimatorSets used for expand and collapse animation
    private AnimatorSet mExpandAnimation = new AnimatorSet().setDuration(ANIMATION_DURATION);
    private AnimatorSet mCollapseAnimation = new AnimatorSet().setDuration(ANIMATION_DURATION);

    @Override
    public void startExpandAnimation(boolean animate) {
        mCollapseAnimation.cancel();
        mExpandAnimation.setDuration(animate ? ANIMATION_DURATION : 0);
        mExpandAnimation.start();
    }

    @Override
    public void startCollapseAnimation(boolean animate) {
        mCollapseAnimation.setDuration(animate ? ANIMATION_DURATION : 0);
        mCollapseAnimation.start();
        mExpandAnimation.cancel();
    }

    @Override
    public void buildAnimationForView(View view, int direction, float expandedTranslation, float collapsedTranslation) {
        // Alpha animations
        ObjectAnimator mExpandAlpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        mExpandAlpha.setInterpolator(EXPAND_ALPHA_INTERPOLATOR);
        ObjectAnimator mCollapseAlpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        mCollapseAlpha.setInterpolator(COLLAPSE_INTERPOLATOR);

        // Translation animations
        ObjectAnimator mExpandTranslation = new ObjectAnimator();
        mExpandTranslation.setInterpolator(EXPAND_TRANSLATION_INTERPOLATOR);
        ObjectAnimator mCollapseTranslation = new ObjectAnimator();
        mCollapseTranslation.setInterpolator(COLLAPSE_INTERPOLATOR);
        switch (direction) {
            case FloatingActionMenu.EXPAND_UP:
            case FloatingActionMenu.EXPAND_DOWN:
                mExpandTranslation.setPropertyName("translationY");
                mCollapseTranslation.setPropertyName("translationY");
                break;
            case FloatingActionMenu.EXPAND_LEFT:
            case FloatingActionMenu.EXPAND_RIGHT:
                mExpandTranslation.setPropertyName("translationX");
                mCollapseTranslation.setPropertyName("translationX");
                break;
        }
        mExpandTranslation.setFloatValues(collapsedTranslation, expandedTranslation);
        mCollapseTranslation.setFloatValues(expandedTranslation, collapsedTranslation);
        mExpandTranslation.setTarget(view);
        mCollapseTranslation.setTarget(view);

        // Add animations to AnimationSet
        mExpandAnimation.play(mExpandAlpha);
        mExpandAnimation.play(mExpandTranslation);
        mCollapseAnimation.play(mCollapseAlpha);
        mCollapseAnimation.play(mCollapseTranslation);
    }

    @Override
    public void prepareView(View view, float expandedTranslation, float collapsedTranslation, boolean expanded, boolean expandsHorizontally) {
        if (expandsHorizontally) {
            view.setTranslationX(expanded ? expandedTranslation : collapsedTranslation);
            view.setAlpha(expanded ? 1f : 0f);
        } else {
            view.setTranslationY(expanded ? expandedTranslation : collapsedTranslation);
            view.setAlpha(expanded ? 1f : 0f);
        }
    }

    @Override
    public void buildAnimationForDrawable(FloatingActionButton button, float angle, Drawable drawable) {
        RotationTransitionDrawable mDrawable = new RotationTransitionDrawable(button.getDrawable(), drawable);
        mDrawable.setMaxRotation(angle);
        button.setImageDrawable(mDrawable);

        ObjectAnimator mExpandDrawableRotation = ObjectAnimator.ofFloat(mDrawable, "rotation", 0f, angle);
        mExpandDrawableRotation.setInterpolator(DRAWABLE_INTERPOLATOR);
        ObjectAnimator mCollapseDrawableRotation = ObjectAnimator.ofFloat(mDrawable, "rotation", angle, 0f);
        mCollapseDrawableRotation.setInterpolator(DRAWABLE_INTERPOLATOR);

        mExpandAnimation.play(mExpandDrawableRotation);
        mCollapseAnimation.play(mCollapseDrawableRotation);
    }

    @Override
    public void prepareDrawable(FloatingActionButton button, float angle, boolean expanded) {
        RotationTransitionDrawable mRotatingDrawable = (RotationTransitionDrawable) button.getDrawable();
        if (mRotatingDrawable != null) {
            mRotatingDrawable.setRotation(expanded ? angle : 0f);
        }
    }

    @Override
    public void buildAnimationForDimming(View dimmingView, int dimmingColor) {
        ObjectAnimator mExpandBackgroundColor = ObjectAnimator.ofInt(dimmingView, "backgroundColor", Color.TRANSPARENT, dimmingColor);
        mExpandBackgroundColor.setEvaluator(new ArgbEvaluator());
        ObjectAnimator mCollapseBackgroundColor = ObjectAnimator.ofInt(dimmingView, "backgroundColor", dimmingColor, Color.TRANSPARENT);
        mCollapseBackgroundColor.setEvaluator(new ArgbEvaluator());

        mExpandAnimation.play(mExpandBackgroundColor);
        mCollapseAnimation.play(mCollapseBackgroundColor);
    }

    @Override
    public void prepareDimming(View dimmingView, int dimmingColor, boolean expanded) {
        dimmingView.setBackgroundColor(expanded ? dimmingColor : Color.TRANSPARENT);
    }
}
