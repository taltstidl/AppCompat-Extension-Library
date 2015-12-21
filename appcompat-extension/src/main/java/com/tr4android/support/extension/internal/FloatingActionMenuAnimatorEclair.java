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

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;

import com.tr4android.support.extension.drawable.RotationTransitionDrawable;
import com.tr4android.support.extension.widget.FloatingActionMenu;

import java.util.HashMap;

/**
 * An FloatingActionMenu animator built for pre-Honeycomb devices
 * Note: this one is more limited because it doesn't have access to ObjectAnimator
 */
public class FloatingActionMenuAnimatorEclair implements FloatingActionMenuAnimator {
    // AnimatorSets used for expand and collapse animation
    private HashMap<View, Animation> mExpandAnimation = new HashMap<>();
    private HashMap<View, Animation> mCollapseAnimation = new HashMap<>();

    // The rotation transition drawable
    private RotationTransitionDrawable mRotationTransitionDrawable;

    // The transition drawable used for dimming;
    private TransitionDrawable mDimmingTransitionDrawable;

    @Override
    public void startExpandAnimation(boolean animate) {
        mDimmingTransitionDrawable.startTransition(ANIMATION_DURATION);
        mRotationTransitionDrawable.setRotation(mRotationTransitionDrawable.getMaxRotation());
        for (HashMap.Entry<View, Animation> entry : mExpandAnimation.entrySet()) {
            View view = entry.getKey();
            Animation animation = entry.getValue();
            animation.setDuration(animate ? ANIMATION_DURATION : 0);
            view.clearAnimation();
            view.startAnimation(animation);
        }
    }

    @Override
    public void startCollapseAnimation(boolean animate) {
        mDimmingTransitionDrawable.reverseTransition(ANIMATION_DURATION);
        mRotationTransitionDrawable.setRotation(0f);
        for (HashMap.Entry<View, Animation> entry : mCollapseAnimation.entrySet()) {
            View view = entry.getKey();
            Animation animation = entry.getValue();
            animation.setDuration(animate ? ANIMATION_DURATION : 0);
            view.clearAnimation();
            view.startAnimation(animation);
        }
    }

    @Override
    public void buildAnimationForView(View view, int direction, float expandedTranslation, float collapsedTranslation) {
        // AnimationSets for alpha and translation
        AnimationSet mExpandAnimationSet = new AnimationSet(false);
        mExpandAnimationSet.setFillAfter(true);
        AnimationSet mCollapseAnimationSet = new AnimationSet(false);
        mCollapseAnimationSet.setFillAfter(true);

        // Alpha animations
        AlphaAnimation mExpandAlpha = new AlphaAnimation(0f, 1f);
        mExpandAlpha.setInterpolator(EXPAND_ALPHA_INTERPOLATOR);
        mExpandAnimationSet.addAnimation(mExpandAlpha);
        AlphaAnimation mCollapseAlpha = new AlphaAnimation(1f, 0f);
        mCollapseAlpha.setInterpolator(COLLAPSE_INTERPOLATOR);
        mCollapseAnimationSet.addAnimation(mCollapseAlpha);

        // Translation animations
        TranslateAnimation mExpandTranslation;
        TranslateAnimation mCollapseTranslation;
        switch (direction) {
            case FloatingActionMenu.EXPAND_UP:
            case FloatingActionMenu.EXPAND_DOWN:
                mExpandTranslation = new TranslateAnimation(0, 0, collapsedTranslation, expandedTranslation);
                mCollapseTranslation = new TranslateAnimation(0, 0, expandedTranslation, collapsedTranslation);
                break;
            case FloatingActionMenu.EXPAND_LEFT:
            case FloatingActionMenu.EXPAND_RIGHT:
                mExpandTranslation = new TranslateAnimation(collapsedTranslation, expandedTranslation, 0, 0);
                mCollapseTranslation = new TranslateAnimation(expandedTranslation, collapsedTranslation, 0, 0);
                break;
            default:
                // Note: this should never be called if everything works as expected
                mExpandTranslation = new TranslateAnimation(0, 0, 0, 0);
                mCollapseTranslation = new TranslateAnimation(0, 0, 0, 0);
        }
        mExpandTranslation.setInterpolator(EXPAND_TRANSLATION_INTERPOLATOR);
        mExpandAnimationSet.addAnimation(mExpandTranslation);
        mCollapseTranslation.setInterpolator(COLLAPSE_INTERPOLATOR);
        mCollapseAnimationSet.addAnimation(mCollapseTranslation);

        // Add animations to HashMaps
        mExpandAnimation.put(view, mExpandAnimationSet);
        mCollapseAnimation.put(view, mCollapseAnimationSet);
    }

    @Override
    public void prepareView(View view, float expandedTranslation, float collapsedTranslation, boolean expanded, boolean expandsHorizontally) {
        if (expandsHorizontally) {
            setTranslationX(view, expanded ? expandedTranslation : collapsedTranslation);
            setAlpha(view, expanded ? 1f : 0f);
        } else {
            setTranslationY(view, expanded ? expandedTranslation : collapsedTranslation);
            setAlpha(view, expanded ? 1f : 0f);
        }
    }

    @Override
    public void buildAnimationForDrawable(FloatingActionButton button, float angle, Drawable drawable) {
        mRotationTransitionDrawable = new RotationTransitionDrawable(button.getDrawable(), drawable);
        mRotationTransitionDrawable.setMaxRotation(angle);
        button.setImageDrawable(mRotationTransitionDrawable);
    }

    @Override
    public void prepareDrawable(FloatingActionButton button, float angle, boolean expanded) {
        if (mRotationTransitionDrawable != null) {
            mRotationTransitionDrawable.setRotation(expanded ? angle : 0f);
        }
    }

    @Override
    public void buildAnimationForDimming(View dimmingView, int dimmingColor) {
        mDimmingTransitionDrawable = new TransitionDrawable(new Drawable[]{new ColorDrawable(Color.TRANSPARENT), new ColorDrawable(dimmingColor)});
        mDimmingTransitionDrawable.setCrossFadeEnabled(true);
        dimmingView.setBackgroundDrawable(mDimmingTransitionDrawable);
    }

    @Override
    public void prepareDimming(View dimmingView, int dimmingColor, boolean expanded) {
        if (expanded) {
            mDimmingTransitionDrawable.startTransition(0);
        } else {
            mDimmingTransitionDrawable.reverseTransition(0);
        }
    }

    private void setTranslationX(View view, float translation) {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, translation, 0, 0);
        translateAnimation.setDuration(0);
        translateAnimation.setFillAfter(true);
        view.startAnimation(translateAnimation);
    }

    private void setTranslationY(View view, float translation) {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, translation);
        translateAnimation.setDuration(0);
        translateAnimation.setFillAfter(true);
        view.startAnimation(translateAnimation);
    }

    private void setAlpha(View view, float alpha) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, alpha);
        alphaAnimation.setDuration(0);
        alphaAnimation.setFillAfter(true);
        view.startAnimation(alphaAnimation);
    }
}
