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

import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Base interface for the platform dependent animation implementations
 */
public interface FloatingActionMenuAnimator {
    // Interpolators used for the animations
    Interpolator EXPAND_TRANSLATION_INTERPOLATOR = new OvershootInterpolator();
    Interpolator EXPAND_ALPHA_INTERPOLATOR = new DecelerateInterpolator();
    Interpolator COLLAPSE_INTERPOLATOR = new DecelerateInterpolator(3f);
    Interpolator DRAWABLE_INTERPOLATOR = new OvershootInterpolator();
    // Duration used for the animations
    int ANIMATION_DURATION = 300;

    /**
     * Called to start the expand animation implementation
     * Note: this should also cancel a running collapse animation
     * @param animate whether the expansion should be animated or not
     */
    void startExpandAnimation(boolean animate);

    /**
     * Called to start the collapse animation implementation
     * Note: this should also cancel a running expand animation
     * @param animate whether the collapse should be animated or not
     */
    void startCollapseAnimation(boolean animate);

    /**
     * Called to add an animation for a specific view (floating action button or label)
     * @param view the view for which to build an animation
     * @param direction the direction in which the view animates
     * @param expandedTranslation the translation of the view in expanded state
     * @param collapsedTranslation the translation of the view in collapsed state
     */
    void buildAnimationForView(View view, int direction, float expandedTranslation, float collapsedTranslation);

    /**
     * Called to prepare a view (floating action button or label) during a layout pass
     * @param view the view to prepare
     * @param expandedTranslation the translation of the view in expanded state
     * @param collapsedTranslation the translation of the view in collapsed state
     * @param expanded the state used in layout
     * @param expandsHorizontally the orientation in which the view translates
     */
    void prepareView(View view, float expandedTranslation, float collapsedTranslation, boolean expanded, boolean expandsHorizontally);

    /**
     * Called to add an animation for a specific drawable
     * @param button the floating action button for which to animate the drawable
     * @param angle the angle of the drawable in expanded state
     * @param drawable the (optional) drawable in expanded state
     */
    void buildAnimationForDrawable(FloatingActionButton button, float angle, Drawable drawable);

    /**
     * Called to prepare a drawable
     * @param button the floating action button with the drawable to prepare
     * @param angle the angle of the drawable in expanded state
     * @param expanded the state used in layout
     */
    void prepareDrawable(FloatingActionButton button, float angle, boolean expanded);

    /**
     * Called to add an animation for dimming
     * @param dimmingView the view for which to animate the dimming
     * @param dimmingColor the color to use for dimming
     */
    void buildAnimationForDimming(View dimmingView, int dimmingColor);

    /**
     * Called to prepare the dimming
     * @param dimmingView the view for which to prepare the dimming
     * @param dimmingColor the color to use for dimming
     * @param expanded the state used in layout
     */
    void prepareDimming(View dimmingView, int dimmingColor, boolean expanded);
}
