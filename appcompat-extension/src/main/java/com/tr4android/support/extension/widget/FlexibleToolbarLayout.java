/*
 * Copyright (C) 2015 The Android Open Source Project
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
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.tr4android.appcompat.extension.R;
import com.tr4android.support.extension.animation.AnimationUtils;
import com.tr4android.support.extension.animation.ValueAnimatorCompat;
import com.tr4android.support.extension.internal.ViewOffsetHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * FlexibleToolbarLayout is a wrapper for {@link Toolbar} which implements a collapsing app bar.
 * It is designed to be used as a direct child of a {@link AppBarLayout}.
 * FlexibleToolbarLayout contains the following features:
 * <p/>
 * <h3>Collapsing title</h3>
 * A title which is larger when the layout is fully visible but collapses and becomes smaller as
 * the layout is scrolled off screen. You can set the title to display via
 * {@link #setTitle(CharSequence)}. The title appearance can be tweaked via the
 * {@code titleCollapsedTextAppearance} and {@code titleExpandedTextAppearance} attributes.
 * <p/>
 * <h3>Collapsing subtitle</h3>
 * A subtitle which can be larger when the layout is fully visible but collapses and becomes smaller as
 * the layout is scrolled off screen. You can set the subtitle to display via
 * {@link #setSubtitle(CharSequence)}. The subtitle appearance can be tweaked via the
 * {@code subtitleCollapsedTextAppearance} and {@code subtitleExpandedTextAppearance} attributes.
 * <p/>
 * <h3>Collapsing title</h3>
 * An icon which is larger when the layout is fully visible but collapses and becomes smaller as
 * the layout is scrolled off screen. You can set the icon to display via
 * {@link #setIcon(Drawable)}. The icon size can be tweaked via the
 * {@code iconCollapsedSize} and {@code iconExpandedSize} attributes.
 * <p/>
 * <h3>Content scrim</h3>
 * A full-bleed scrim which is show or hidden when the scroll position has hit a certain threshold.
 * You can change this via {@link #setContentScrim(Drawable)}.
 * <p/>
 * <h3>Status bar scrim</h3>
 * A scrim which is show or hidden behind the status bar when the scroll position has hit a certain
 * threshold. You can change this via {@link #setStatusBarScrim(Drawable)}. This only works
 * on {@link Build.VERSION_CODES#LOLLIPOP LOLLIPOP} devices when we set to fit system windows.
 * <p/>
 * <h3>Parallax scrolling children</h3>
 * Child views can opt to be scrolled within this layout in a parallax fashion.
 * See {@link LayoutParams#COLLAPSE_MODE_PARALLAX} and
 * {@link LayoutParams#setParallaxMultiplier(float)}.
 * <p/>
 * <h3>Pinned position children</h3>
 * Child views can opt to be pinned in space globally. This is useful when implementing a
 * collapsing as it allows the {@link Toolbar} to be fixed in place even though this layout is
 * moving. See {@link LayoutParams#COLLAPSE_MODE_PIN}.
 *
 * @attr ref android.support.design.R.styleable#FlexibleToolbarLayout_titleCollapsedTextAppearance
 * @attr ref android.support.design.R.styleable#FlexibleToolbarLayout_titleExpandedTextAppearance
 * @attr ref android.support.design.R.styleable#FlexibleToolbarLayout_subtitleCollapsedTextAppearance
 * @attr ref android.support.design.R.styleable#FlexibleToolbarLayout_subtitleExpandedTextAppearance
 * @attr ref android.support.design.R.styleable#FlexibleToolbarLayout_iconCollapsedSize
 * @attr ref android.support.design.R.styleable#FlexibleToolbarLayout_iconExpandedSize
 * @attr ref android.support.design.R.styleable#FlexibleToolbarLayout_contentScrimColor
 * @attr ref android.support.design.R.styleable#FlexibleToolbarLayout_expandedMargin
 * @attr ref android.support.design.R.styleable#FlexibleToolbarLayout_expandedMarginStart
 * @attr ref android.support.design.R.styleable#FlexibleToolbarLayout_expandedMarginEnd
 * @attr ref android.support.design.R.styleable#FlexibleToolbarLayout_expandedMarginBottom
 * @attr ref android.support.design.R.styleable#FlexibleToolbarLayout_statusBarScrimColor
 * @attr ref android.support.design.R.styleable#FlexibleToolbarLayout_toolbarRefId
 */
public class FlexibleToolbarLayout extends FrameLayout {

    private static final int SCRIM_ANIMATION_DURATION = 600;

    private boolean mRefreshToolbar = true;
    private int mToolbarId;
    private Toolbar mToolbar;
    private View mDummyView;

    private int mExpandedMarginLeft;
    private int mExpandedMarginTop;
    private int mExpandedMarginRight;
    private int mExpandedMarginBottom;

    private final Rect mTmpRect = new Rect();
    private final Rect mExpandedBounds = new Rect();
    private boolean mDrawTitles;
    private int mSpaceTitleSubtitle;
    private int mSpaceIconTitles;

    private final CollapsingTextHelper mTitleCollapsingTextHelper;
    private boolean mCollapsingTitleEnabled;

    private final CollapsingTextHelper mSubtitleCollapsingTextHelper;
    private boolean mCollapsingSubtitleEnabled;

    private final CollapsingDrawableHelper mIconCollapsingHelper;
    private boolean mCollapsingIconEnabled;

    private Drawable mContentScrim;
    private Drawable mStatusBarScrim;
    private int mScrimAlpha;
    private boolean mScrimsAreShown;
    private ValueAnimatorCompat mScrimAnimator;

    private AppBarLayout.OnOffsetChangedListener mOnOffsetChangedListener;

    private int mCurrentOffset;

    private WindowInsetsCompat mLastInsets;

    public FlexibleToolbarLayout(Context context) {
        this(context, null);
    }

    public FlexibleToolbarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlexibleToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mTitleCollapsingTextHelper = new CollapsingTextHelper(this);
        mTitleCollapsingTextHelper.setTextSizeInterpolator(AnimationUtils.LINEAR_INTERPOLATOR);
        mSubtitleCollapsingTextHelper = new CollapsingTextHelper(this);
        mSubtitleCollapsingTextHelper.setTextSizeInterpolator(AnimationUtils.LINEAR_INTERPOLATOR);
        mIconCollapsingHelper = new CollapsingDrawableHelper(this);
        mIconCollapsingHelper.setIconSizeInterpolator(AnimationUtils.LINEAR_INTERPOLATOR);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.FlexibleToolbarLayout, defStyleAttr,
                R.style.Widget_Design_FlexibleToolbarLayout);

        int expandedVerticalGravity =
                a.getInt(R.styleable.FlexibleToolbarLayout_expandedGravity, Gravity.CENTER_VERTICAL);

        mTitleCollapsingTextHelper.setExpandedTextGravity(GravityCompat.START | expandedVerticalGravity);
        mTitleCollapsingTextHelper.setCollapsedTextGravity(GravityCompat.START | Gravity.CENTER_VERTICAL);
        mSubtitleCollapsingTextHelper.setExpandedTextGravity(GravityCompat.START | expandedVerticalGravity);
        mSubtitleCollapsingTextHelper.setCollapsedTextGravity(GravityCompat.START | Gravity.CENTER_VERTICAL);

        mExpandedMarginLeft = mExpandedMarginTop = mExpandedMarginRight = mExpandedMarginBottom =
                a.getDimensionPixelSize(R.styleable.FlexibleToolbarLayout_expandedMargin, 0);

        final boolean isRtl = ViewCompat.getLayoutDirection(this)
                == ViewCompat.LAYOUT_DIRECTION_RTL;
        if (a.hasValue(R.styleable.FlexibleToolbarLayout_expandedMarginStart)) {
            final int marginStart = a.getDimensionPixelSize(
                    R.styleable.FlexibleToolbarLayout_expandedMarginStart, 0);
            if (isRtl) {
                mExpandedMarginRight = marginStart;
            } else {
                mExpandedMarginLeft = marginStart;
            }
        }
        if (a.hasValue(R.styleable.FlexibleToolbarLayout_expandedMarginEnd)) {
            final int marginEnd = a.getDimensionPixelSize(
                    R.styleable.FlexibleToolbarLayout_expandedMarginEnd, 0);
            if (isRtl) {
                mExpandedMarginLeft = marginEnd;
            } else {
                mExpandedMarginRight = marginEnd;
            }
        }
        if (a.hasValue(R.styleable.FlexibleToolbarLayout_expandedMarginTop)) {
            mExpandedMarginTop = a.getDimensionPixelSize(
                    R.styleable.FlexibleToolbarLayout_expandedMarginTop, 0);
        }
        if (a.hasValue(R.styleable.FlexibleToolbarLayout_expandedMarginBottom)) {
            mExpandedMarginBottom = a.getDimensionPixelSize(
                    R.styleable.FlexibleToolbarLayout_expandedMarginBottom, 0);
        }

        mCollapsingTitleEnabled = a.getBoolean(
                R.styleable.FlexibleToolbarLayout_titleFlexibleEnabled, true);
        setTitle(a.getText(R.styleable.FlexibleToolbarLayout_title));
        mCollapsingSubtitleEnabled = a.getBoolean(
                R.styleable.FlexibleToolbarLayout_subtitleFlexibleEnabled, true);
        setSubtitle(a.getText(R.styleable.FlexibleToolbarLayout_subtitle));
        mCollapsingIconEnabled = a.getBoolean(
                R.styleable.FlexibleToolbarLayout_iconFlexibleEnabled, true);
        setIcon(a.getDrawable(R.styleable.FlexibleToolbarLayout_icon));

        // First load the default text appearances
        mTitleCollapsingTextHelper.setExpandedTextAppearance(
                R.style.TextAppearance_Design_FlexibleToolbarLayout_ExpandedTitle);
        mTitleCollapsingTextHelper.setCollapsedTextAppearance(
                R.style.TextAppearance_Design_FlexibleToolbarLayout_CollapsedTitle);
        mSubtitleCollapsingTextHelper.setExpandedTextAppearance(
                R.style.TextAppearance_Design_FlexibleToolbarLayout_Subtitle);
        mSubtitleCollapsingTextHelper.setCollapsedTextAppearance(
                R.style.TextAppearance_Design_FlexibleToolbarLayout_Subtitle);

        // Now overlay any custom text appearances
        if (a.hasValue(R.styleable.FlexibleToolbarLayout_titleExpandedTextAppearance)) {
            mTitleCollapsingTextHelper.setExpandedTextAppearance(
                    a.getResourceId(
                            R.styleable.FlexibleToolbarLayout_titleExpandedTextAppearance, 0));
        }
        if (a.hasValue(R.styleable.FlexibleToolbarLayout_titleCollapsedTextAppearance)) {
            mTitleCollapsingTextHelper.setCollapsedTextAppearance(
                    a.getResourceId(
                            R.styleable.FlexibleToolbarLayout_titleCollapsedTextAppearance, 0));

        }
        if (a.hasValue(R.styleable.FlexibleToolbarLayout_subtitleExpandedTextAppearance)) {
            mTitleCollapsingTextHelper.setExpandedTextAppearance(
                    a.getResourceId(
                            R.styleable.FlexibleToolbarLayout_titleExpandedTextAppearance, 0));
        }
        if (a.hasValue(R.styleable.FlexibleToolbarLayout_subtitleCollapsedTextAppearance)) {
            mTitleCollapsingTextHelper.setCollapsedTextAppearance(
                    a.getResourceId(
                            R.styleable.FlexibleToolbarLayout_titleCollapsedTextAppearance, 0));

        }

        // Load the icon sizes
        mIconCollapsingHelper.setCollapsedIconSize(a.getDimensionPixelSize(R.styleable.FlexibleToolbarLayout_iconCollapsedSize, 0));
        mIconCollapsingHelper.setExpandedIconSize(a.getDimensionPixelSize(R.styleable.FlexibleToolbarLayout_iconExpandedSize, 0));

        mSpaceTitleSubtitle = a.getDimensionPixelSize(R.styleable.FlexibleToolbarLayout_spaceTitleSubtitle, 0);
        mSpaceIconTitles = a.getDimensionPixelSize(R.styleable.FlexibleToolbarLayout_spaceIconTitles, 0);

        setContentScrim(a.getDrawable(R.styleable.FlexibleToolbarLayout_contentScrimColor));
        setStatusBarScrim(a.getDrawable(R.styleable.FlexibleToolbarLayout_statusBarScrimColor));

        mToolbarId = a.getResourceId(R.styleable.FlexibleToolbarLayout_toolbarRefId, -1);

        a.recycle();

        setWillNotDraw(false);

        ViewCompat.setOnApplyWindowInsetsListener(this,
                new android.support.v4.view.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsetsCompat onApplyWindowInsets(View v,
                                                                  WindowInsetsCompat insets) {
                        mLastInsets = insets;
                        requestLayout();
                        return insets.consumeSystemWindowInsets();
                    }
                });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Add an OnOffsetChangedListener if possible
        final ViewParent parent = getParent();
        if (parent instanceof AppBarLayout) {
            if (mOnOffsetChangedListener == null) {
                mOnOffsetChangedListener = new OffsetUpdateListener();
            }
            ((AppBarLayout) parent).addOnOffsetChangedListener(mOnOffsetChangedListener);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        // Remove our OnOffsetChangedListener if possible and it exists
        final ViewParent parent = getParent();
        if (mOnOffsetChangedListener != null && parent instanceof AppBarLayout) {
            ((AppBarLayout) parent).removeOnOffsetChangedListener(mOnOffsetChangedListener);
        }

        super.onDetachedFromWindow();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // If we don't have a toolbar, the scrim will be not be drawn in drawChild() below.
        // Instead, we draw it here, before our collapsing text.
        ensureToolbar();
        if (mToolbar == null && mContentScrim != null && mScrimAlpha > 0) {
            mContentScrim.mutate().setAlpha(mScrimAlpha);
            mContentScrim.draw(canvas);
        }

        // Let the collapsing text helper draw it's text
        if (mCollapsingTitleEnabled && mDrawTitles) {
            mTitleCollapsingTextHelper.draw(canvas);
        }
        if (mCollapsingSubtitleEnabled && mDrawTitles) {
            mSubtitleCollapsingTextHelper.draw(canvas);
        }
        // Let the collapsing drawable helper draw it's drawable
        if (mCollapsingIconEnabled && mDrawTitles) {
            mIconCollapsingHelper.draw(canvas);
        }

        // Now draw the status bar scrim
        if (mStatusBarScrim != null && mScrimAlpha > 0) {
            final int topInset = mLastInsets != null ? mLastInsets.getSystemWindowInsetTop() : 0;
            if (topInset > 0) {
                mStatusBarScrim.setBounds(0, -mCurrentOffset, getWidth(),
                        topInset - mCurrentOffset);
                mStatusBarScrim.mutate().setAlpha(mScrimAlpha);
                mStatusBarScrim.draw(canvas);
            }
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        // This is a little weird. Our scrim needs to be behind the Toolbar (if it is present),
        // but in front of any other children which are behind it. To do this we intercept the
        // drawChild() call, and draw our scrim first when drawing the toolbar
        ensureToolbar();
        if (child == mToolbar && mContentScrim != null && mScrimAlpha > 0) {
            mContentScrim.mutate().setAlpha(mScrimAlpha);
            mContentScrim.draw(canvas);
        }

        // Carry on drawing the child...
        return super.drawChild(canvas, child, drawingTime);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mContentScrim != null) {
            mContentScrim.setBounds(0, 0, w, h);
        }
    }

    private void ensureToolbar() {
        if (!mRefreshToolbar) {
            return;
        }

        Toolbar fallback = null, selected = null;

        for (int i = 0, count = getChildCount(); i < count; i++) {
            final View child = getChildAt(i);
            if (child instanceof Toolbar) {
                if (mToolbarId != -1) {
                    // There's a toolbar id set so try and find it...
                    if (mToolbarId == child.getId()) {
                        // We found the primary Toolbar, use it
                        selected = (Toolbar) child;
                        break;
                    }
                    if (fallback == null) {
                        // We'll record the first Toolbar as our fallback
                        fallback = (Toolbar) child;
                    }
                } else {
                    // We don't have a id to check for so just use the first we come across
                    selected = (Toolbar) child;
                    break;
                }
            }
        }

        if (selected == null) {
            // If we didn't find a primary Toolbar, use the fallback
            selected = fallback;
        }

        mToolbar = selected;
        updateDummyView();
        mRefreshToolbar = false;
    }

    private void updateDummyView() {
        if (!mCollapsingTitleEnabled && mDummyView != null) {
            // If we have a dummy view and we have our title disabled, remove it from its parent
            final ViewParent parent = mDummyView.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(mDummyView);
            }
        }
        if (mCollapsingTitleEnabled && mToolbar != null) {
            if (mDummyView == null) {
                mDummyView = new View(getContext());
            }
            if (mDummyView.getParent() == null) {
                mToolbar.addView(mDummyView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ensureToolbar();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // We only draw the title and subtitle if the dummy view is being displayed (Toolbar removes
        // views if there is no space)
        mDrawTitles = mDummyView != null && mDummyView.isShown();

        // This is the amount of horizontal offset needed for the title and subtitle
        int horizontalOffsetCollapsed = mCollapsingIconEnabled ?
                (int) mIconCollapsingHelper.getCollapsedIconSize() + mSpaceIconTitles : 0;
        int horizontalOffsetExpanded = mCollapsingIconEnabled ?
                (int) mIconCollapsingHelper.getExpandedIconSize() + mSpaceIconTitles : 0;

        // These are the expanded bounds needed for the icon, title and subtitle
        int expandedIconSize = (int) mIconCollapsingHelper.getExpandedIconSize();
        mExpandedBounds.set(mExpandedMarginLeft,
                mCollapsingIconEnabled
                        ? (bottom - top - mExpandedMarginBottom - expandedIconSize)
                        : (mTmpRect.bottom + mExpandedMarginTop),
                right - left - mExpandedMarginRight,
                bottom - top - mExpandedMarginBottom);

        // Update the collapsed bounds by getting it's transformed bounds. This needs to be done
        // before the children are offset below
        if (mCollapsingTitleEnabled && mDrawTitles) {
            ViewGroupUtils.getDescendantRect(this, mDummyView, mTmpRect);
            // Update the collapsed bounds
            mTitleCollapsingTextHelper.setCollapsedBounds(mTmpRect.left + horizontalOffsetCollapsed,
                    bottom - mTmpRect.height(), mTmpRect.right, bottom);
            // Update the expanded bounds
            mTitleCollapsingTextHelper.setExpandedBounds(
                    mExpandedBounds.left + horizontalOffsetExpanded,
                    mExpandedBounds.top, mExpandedBounds.right, mExpandedBounds.bottom);
            // Adjust the offset when subtitle is present
            if (mCollapsingSubtitleEnabled) {
                mTitleCollapsingTextHelper.setCollapsedTextOffsetBottom(
                        mSpaceTitleSubtitle + mSubtitleCollapsingTextHelper.getCollapsedTextHeight());
                mTitleCollapsingTextHelper.setExpandedTextOffsetBottom(
                        mSpaceTitleSubtitle + mSubtitleCollapsingTextHelper.getExpandedTextHeight());
            }
            // Now recalculate using the new bounds
            mTitleCollapsingTextHelper.recalculate();
        }
        if (mCollapsingSubtitleEnabled && mDrawTitles) {
            ViewGroupUtils.getDescendantRect(this, mDummyView, mTmpRect);
            // Update the collapsed bounds
            mSubtitleCollapsingTextHelper.setCollapsedBounds(mTmpRect.left + horizontalOffsetCollapsed,
                    bottom - mTmpRect.height(), mTmpRect.right, bottom);
            // Update the expanded bounds
            mSubtitleCollapsingTextHelper.setExpandedBounds(
                    mExpandedBounds.left + horizontalOffsetExpanded,
                    mExpandedBounds.top, mExpandedBounds.right, mExpandedBounds.bottom);
            // Adjust the offset when title is present
            if (mCollapsingTitleEnabled) {
                mSubtitleCollapsingTextHelper.setCollapsedTextOffsetTop(
                        mSpaceTitleSubtitle + mTitleCollapsingTextHelper.getCollapsedTextHeight());
                mSubtitleCollapsingTextHelper.setExpandedTextOffsetTop(
                        mSpaceTitleSubtitle + mTitleCollapsingTextHelper.getExpandedTextHeight());
            }
            // Now recalculate using the new bounds
            mSubtitleCollapsingTextHelper.recalculate();
        }
        if (mCollapsingIconEnabled && mDrawTitles) {
            ViewGroupUtils.getDescendantRect(this, mDummyView, mTmpRect);
            // Update the collapsed bounds
            int collapsedIconSize = (int) mIconCollapsingHelper.getCollapsedIconSize();
            int collapsedTop = bottom - mTmpRect.height() + Math.round((mTmpRect.height() - collapsedIconSize) / 2f);
            mIconCollapsingHelper.setCollapsedBounds(mTmpRect.left, collapsedTop,
                    mTmpRect.left + collapsedIconSize, collapsedTop + collapsedIconSize);
            // Update the expanded bounds
            mIconCollapsingHelper.setExpandedBounds(
                    mExpandedBounds.left, mExpandedBounds.top,
                    mExpandedMarginLeft + expandedIconSize, mExpandedBounds.bottom);
            // Now recalculate using the new bounds
            mIconCollapsingHelper.recalculate();
        }

        // Update our child view offset helpers
        for (int i = 0, z = getChildCount(); i < z; i++) {
            final View child = getChildAt(i);

            if (mLastInsets != null && !ViewCompat.getFitsSystemWindows(child)) {
                final int insetTop = mLastInsets.getSystemWindowInsetTop();
                if (child.getTop() < insetTop) {
                    // If the child isn't set to fit system windows but is drawing within the inset
                    // offset it down
                    child.offsetTopAndBottom(insetTop);
                }
            }

            getViewOffsetHelper(child).onViewLayout();
        }

        // Finally, set our minimum height to enable proper AppBarLayout collapsing
        if (mToolbar != null) {
            if (mCollapsingTitleEnabled && TextUtils.isEmpty(mTitleCollapsingTextHelper.getText())) {
                // If we do not currently have a title, try and grab it from the Toolbar
                mTitleCollapsingTextHelper.setText(mToolbar.getTitle());
            }
            setMinimumHeight(mToolbar.getHeight());
        }
    }

    private static ViewOffsetHelper getViewOffsetHelper(View view) {
        ViewOffsetHelper offsetHelper = (ViewOffsetHelper) view.getTag(R.id.view_offset_helper);
        if (offsetHelper == null) {
            offsetHelper = new ViewOffsetHelper(view);
            view.setTag(R.id.view_offset_helper, offsetHelper);
        }
        return offsetHelper;
    }

    /**
     * Collapses the {@link FlexibleToolbarLayout}.
     * This will only have an effect if the {@link FlexibleToolbarLayout}
     * is used as a child of {@link AppBarLayout}.
     */
    public void collapse() {
        // Passes call to AppBarLayout if possible
        ViewParent parent = getParent();
        if (parent instanceof AppBarLayout) {
            ((AppBarLayout) parent).setExpanded(false);
        }
    }

    /**
     * Collapses the FlexibleToolbarLayout.
     * This will only have an effect if the {@link FlexibleToolbarLayout}
     * is used as a child of {@link AppBarLayout}.
     *
     * @param animate Whether or not the collapse should be animated
     */
    public void collapse(boolean animate) {
        // Passes call to AppBarLayout if possible
        ViewParent parent = getParent();
        if (parent instanceof AppBarLayout) {
            ((AppBarLayout) parent).setExpanded(false, animate);
        }
    }

    /**
     * Expands the {@link FlexibleToolbarLayout}.
     * This will only have an effect if the {@link FlexibleToolbarLayout}
     * is used as a child of {@link AppBarLayout}.
     */
    public void expand() {
        // Passes call to AppBarLayout if possible
        ViewParent parent = getParent();
        if (parent instanceof AppBarLayout) {
            ((AppBarLayout) parent).setExpanded(true);
        }
    }

    /**
     * Expands the FlexibleToolbarLayout.
     * This will only have an effect if the {@link FlexibleToolbarLayout}
     * is used as a child of {@link AppBarLayout}.
     *
     * @param animate Whether or not the expansion should be animated
     */
    public void expand(boolean animate) {
        // Passes call to AppBarLayout if possible
        ViewParent parent = getParent();
        if (parent instanceof AppBarLayout) {
            ((AppBarLayout) parent).setExpanded(true, animate);
        }
    }

    /**
     * Sets the title to be displayed by this view, if enabled.
     *
     * @attr ref R.styleable#FlexibleToolbarLayout_title
     * @see #setTitleEnabled(boolean)
     * @see #getTitle()
     */
    public void setTitle(@Nullable CharSequence title) {
        mTitleCollapsingTextHelper.setText(title);
    }

    /**
     * Returns the title currently being displayed by this view. If the title is not enabled, then
     * this will return {@code null}.
     *
     * @attr ref R.styleable#FlexibleToolbarLayout_title
     */
    @Nullable
    public CharSequence getTitle() {
        return mCollapsingTitleEnabled ? mTitleCollapsingTextHelper.getText() : null;
    }

    /**
     * Sets whether this view should display its own title.
     * <p/>
     * <p>The title displayed by this view will shrink and grow based on the scroll offset.</p>
     *
     * @attr ref R.styleable#FlexibleToolbarLayout_titleFlexibleEnabled
     * @see #setTitle(CharSequence)
     * @see #isTitleEnabled()
     */
    public void setTitleEnabled(boolean enabled) {
        if (enabled != mCollapsingTitleEnabled) {
            mCollapsingTitleEnabled = enabled;
            updateDummyView();
            requestLayout();
        }
    }

    /**
     * Returns whether this view is currently displaying its own title.
     *
     * @attr ref R.styleable#FlexibleToolbarLayout_titleFlexibleEnabled
     * @see #setTitleEnabled(boolean)
     */
    public boolean isTitleEnabled() {
        return mCollapsingTitleEnabled;
    }

    /**
     * Sets the subtitle to be displayed by this view, if enabled.
     *
     * @attr ref R.styleable#FlexibleToolbarLayout_subtitle
     * @see #setSubtitleEnabled(boolean)
     * @see #getSubtitle()
     */
    public void setSubtitle(@Nullable CharSequence subtitle) {
        mSubtitleCollapsingTextHelper.setText(subtitle);
    }

    /**
     * Returns the subtitle currently being displayed by this view. If the subtitle is not enabled, then
     * this will return {@code null}.
     *
     * @attr ref R.styleable#FlexibleToolbarLayout_subtitle
     */
    @Nullable
    public CharSequence getSubtitle() {
        return mCollapsingSubtitleEnabled ? mSubtitleCollapsingTextHelper.getText() : null;
    }

    /**
     * Sets whether this view should display its own subtitle.
     * <p/>
     * <p>The subtitle displayed by this view will shrink and grow based on the scroll offset.</p>
     *
     * @attr ref R.styleable#FlexibleToolbarLayout_subtitleFlexibleEnabled
     * @see #setSubtitle(CharSequence)
     * @see #isSubtitleEnabled()
     */
    public void setSubtitleEnabled(boolean enabled) {
        if (enabled != mCollapsingSubtitleEnabled) {
            mCollapsingSubtitleEnabled = enabled;
            updateDummyView();
            requestLayout();
        }
    }

    /**
     * Returns whether this view is currently displaying its own subtitle.
     *
     * @attr ref R.styleable#FlexibleToolbarLayout_subtitleFlexibleEnabled
     * @see #setSubtitleEnabled(boolean)
     */
    public boolean isSubtitleEnabled() {
        return mCollapsingSubtitleEnabled;
    }

    /**
     * Sets the icon to be displayed by this view, if enabled.
     *
     * @attr ref R.styleable#FlexibleToolbarLayout_icon
     * @see #setIconEnabled(boolean)
     * @see #getIcon()
     */
    public void setIcon(@Nullable Drawable icon) {
        mIconCollapsingHelper.setDrawable(icon);
    }

    /**
     * Returns the icon currently being displayed by this view. If the icon is not enabled, then
     * this will return {@code null}.
     *
     * @attr ref R.styleable#FlexibleToolbarLayout_icon
     */
    @Nullable
    public Drawable getIcon() {
        return mCollapsingIconEnabled ? mIconCollapsingHelper.getDrawable() : null;
    }

    /**
     * Sets whether this view should display its own icon.
     * <p/>
     * <p>The icon displayed by this view will shrink and grow based on the scroll offset.</p>
     *
     * @attr ref R.styleable#FlexibleToolbarLayout_iconFlexibleEnabled
     * @see #setIcon(Drawable)
     * @see #isIconEnabled()
     */
    public void setIconEnabled(boolean enabled) {
        if (enabled != mCollapsingIconEnabled) {
            mCollapsingIconEnabled = enabled;
            requestLayout();
        }
    }

    /**
     * Returns whether this view is currently displaying its own icon.
     *
     * @attr ref R.styleable#FlexibleToolbarLayout_iconFlexibleEnabled
     * @see #setIconEnabled(boolean)
     */
    public boolean isIconEnabled() {
        return mCollapsingIconEnabled;
    }

    /**
     * Set whether the content scrim and/or status bar scrim should be shown or not. Any change
     * in the vertical scroll may overwrite this value. Any visibility change will be animated if
     * this view has already been laid out.
     *
     * @param shown whether the scrims should be shown
     * @see #getStatusBarScrim()
     * @see #getContentScrim()
     */
    public void setScrimsShown(boolean shown) {
        setScrimsShown(shown, ViewCompat.isLaidOut(this) && !isInEditMode());
    }

    /**
     * Set whether the content scrim and/or status bar scrim should be shown or not. Any change
     * in the vertical scroll may overwrite this value.
     *
     * @param shown   whether the scrims should be shown
     * @param animate whether to animate the visibility change
     * @see #getStatusBarScrim()
     * @see #getContentScrim()
     */
    public void setScrimsShown(boolean shown, boolean animate) {
        if (mScrimsAreShown != shown) {
            if (animate) {
                animateScrim(shown ? 0xFF : 0x0);
            } else {
                setScrimAlpha(shown ? 0xFF : 0x0);
            }
            mScrimsAreShown = shown;
        }
    }

    private void animateScrim(int targetAlpha) {
        ensureToolbar();
        if (mScrimAnimator == null) {
            mScrimAnimator = AnimationUtils.createAnimator();
            mScrimAnimator.setDuration(SCRIM_ANIMATION_DURATION);
            mScrimAnimator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
            mScrimAnimator.setUpdateListener(new ValueAnimatorCompat.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimatorCompat animator) {
                    setScrimAlpha(animator.getAnimatedIntValue());
                }
            });
        } else if (mScrimAnimator.isRunning()) {
            mScrimAnimator.cancel();
        }

        mScrimAnimator.setIntValues(mScrimAlpha, targetAlpha);
        mScrimAnimator.start();
    }

    private void setScrimAlpha(int alpha) {
        if (alpha != mScrimAlpha) {
            final Drawable contentScrim = mContentScrim;
            if (contentScrim != null && mToolbar != null) {
                ViewCompat.postInvalidateOnAnimation(mToolbar);
            }
            mScrimAlpha = alpha;
            ViewCompat.postInvalidateOnAnimation(FlexibleToolbarLayout.this);
        }
    }

    /**
     * Set the drawable to use for the content scrim from resources. Providing null will disable
     * the scrim functionality.
     *
     * @param drawable the drawable to display
     * @attr ref R.styleable#FlexibleToolbarLayout_contentScrimColor
     * @see #getContentScrim()
     */
    public void setContentScrim(@Nullable Drawable drawable) {
        if (mContentScrim != drawable) {
            if (mContentScrim != null) {
                mContentScrim.setCallback(null);
            }
            if (drawable != null) {
                mContentScrim = drawable.mutate();
                drawable.setBounds(0, 0, getWidth(), getHeight());
                drawable.setCallback(this);
                drawable.setAlpha(mScrimAlpha);
            } else {
                mContentScrim = null;
            }
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * Set the color to use for the content scrim.
     *
     * @param color the color to display
     * @attr ref R.styleable#FlexibleToolbarLayout_contentScrimColor
     * @see #getContentScrim()
     */
    public void setContentScrimColor(@ColorInt int color) {
        setContentScrim(new ColorDrawable(color));
    }

    /**
     * Set the drawable to use for the content scrim from resources.
     *
     * @param resId drawable resource id
     * @attr ref R.styleable#FlexibleToolbarLayout_contentScrimColor
     * @see #getContentScrim()
     */
    public void setContentScrimResource(@DrawableRes int resId) {
        setContentScrim(ContextCompat.getDrawable(getContext(), resId));

    }

    /**
     * Returns the drawable which is used for the foreground scrim.
     *
     * @attr ref R.styleable#FlexibleToolbarLayout_contentScrimColor
     * @see #setContentScrim(Drawable)
     */
    public Drawable getContentScrim() {
        return mContentScrim;
    }

    /**
     * Set the drawable to use for the status bar scrim from resources.
     * Providing null will disable the scrim functionality.
     * <p/>
     * <p>This scrim is only shown when we have been given a top system inset.</p>
     *
     * @param drawable the drawable to display
     * @attr ref R.styleable#FlexibleToolbarLayout_statusBarScrimColor
     * @see #getStatusBarScrim()
     */
    public void setStatusBarScrim(@Nullable Drawable drawable) {
        if (mStatusBarScrim != drawable) {
            if (mStatusBarScrim != null) {
                mStatusBarScrim.setCallback(null);
            }

            mStatusBarScrim = drawable;
            drawable.setCallback(this);
            drawable.mutate().setAlpha(mScrimAlpha);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * Set the color to use for the status bar scrim.
     * <p/>
     * <p>This scrim is only shown when we have been given a top system inset.</p>
     *
     * @param color the color to display
     * @attr ref R.styleable#FlexibleToolbarLayout_statusBarScrimColor
     * @see #getStatusBarScrim()
     */
    public void setStatusBarScrimColor(@ColorInt int color) {
        setStatusBarScrim(new ColorDrawable(color));
    }

    /**
     * Set the drawable to use for the content scrim from resources.
     *
     * @param resId drawable resource id
     * @attr ref R.styleable#FlexibleToolbarLayout_statusBarScrimColor
     * @see #getStatusBarScrim()
     */
    public void setStatusBarScrimResource(@DrawableRes int resId) {
        setStatusBarScrim(ContextCompat.getDrawable(getContext(), resId));
    }

    /**
     * Returns the drawable which is used for the status bar scrim.
     *
     * @attr ref R.styleable#FlexibleToolbarLayout_statusBarScrimColor
     * @see #setStatusBarScrim(Drawable)
     */
    public Drawable getStatusBarScrim() {
        return mStatusBarScrim;
    }

    /**
     * Sets the text color and size for the collapsed title from the specified
     * TextAppearance resource.
     *
     * @attr ref android.support.design.R.styleable#FlexibleToolbarLayout_titleCollapsedTextAppearance
     */
    public void setCollapsedTitleTextAppearance(@StyleRes int resId) {
        mTitleCollapsingTextHelper.setCollapsedTextAppearance(resId);
    }

    /**
     * Sets the text color of the collapsed title.
     *
     * @param color The new text color in ARGB format
     */
    public void setCollapsedTitleTextColor(@ColorInt int color) {
        mTitleCollapsingTextHelper.setCollapsedTextColor(color);
    }

    /**
     * Sets the text color and size for the expanded title from the specified
     * TextAppearance resource.
     *
     * @attr ref android.support.design.R.styleable#FlexibleToolbarLayout_titleExpandedTextAppearance
     */
    public void setExpandedTitleTextAppearance(@StyleRes int resId) {
        mTitleCollapsingTextHelper.setExpandedTextAppearance(resId);
    }

    /**
     * Sets the text color of the expanded title.
     *
     * @param color The new text color in ARGB format
     */
    public void setExpandedTitleColor(@ColorInt int color) {
        mTitleCollapsingTextHelper.setExpandedTextColor(color);
    }

    /**
     * The additional offset used to define when to trigger the scrim visibility change.
     */
    final int getScrimTriggerOffset() {
        return 2 * ViewCompat.getMinimumHeight(this);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(super.generateDefaultLayoutParams());
    }

    @Override
    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected FrameLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {

        private static final float DEFAULT_PARALLAX_MULTIPLIER = 0.5f;

        /**
         * @hide
         */
        @IntDef({
                COLLAPSE_MODE_OFF,
                COLLAPSE_MODE_PIN,
                COLLAPSE_MODE_PARALLAX
        })
        @Retention(RetentionPolicy.SOURCE)
        @interface CollapseMode {
        }

        /**
         * The view will act as normal with no collapsing behavior.
         */
        public static final int COLLAPSE_MODE_OFF = 0;

        /**
         * The view will pin in place until it reaches the bottom of the
         * {@link FlexibleToolbarLayout}.
         */
        public static final int COLLAPSE_MODE_PIN = 1;

        /**
         * The view will scroll in a parallax fashion. See {@link #setParallaxMultiplier(float)}
         * to change the multiplier used.
         */
        public static final int COLLAPSE_MODE_PARALLAX = 2;

        int mCollapseMode = COLLAPSE_MODE_OFF;
        float mParallaxMult = DEFAULT_PARALLAX_MULTIPLIER;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray a = c.obtainStyledAttributes(attrs,
                    R.styleable.CollapsingAppBarLayout_LayoutParams);
            mCollapseMode = a.getInt(
                    R.styleable.CollapsingAppBarLayout_LayoutParams_layout_collapseMode,
                    COLLAPSE_MODE_OFF);
            setParallaxMultiplier(a.getFloat(
                    R.styleable.CollapsingAppBarLayout_LayoutParams_layout_collapseParallaxMultiplier,
                    DEFAULT_PARALLAX_MULTIPLIER));
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(FrameLayout.LayoutParams source) {
            super(source);
        }

        /**
         * Set the collapse mode.
         *
         * @param collapseMode one of {@link #COLLAPSE_MODE_OFF}, {@link #COLLAPSE_MODE_PIN}
         *                     or {@link #COLLAPSE_MODE_PARALLAX}.
         */
        public void setCollapseMode(@CollapseMode int collapseMode) {
            mCollapseMode = collapseMode;
        }

        /**
         * Returns the requested collapse mode.
         *
         * @return the current mode. One of {@link #COLLAPSE_MODE_OFF}, {@link #COLLAPSE_MODE_PIN}
         * or {@link #COLLAPSE_MODE_PARALLAX}.
         */
        @CollapseMode
        public int getCollapseMode() {
            return mCollapseMode;
        }

        /**
         * Set the parallax scroll multiplier used in conjunction with
         * {@link #COLLAPSE_MODE_PARALLAX}. A value of {@code 0.0} indicates no movement at all,
         * {@code 1.0f} indicates normal scroll movement.
         *
         * @param multiplier the multiplier.
         * @see #getParallaxMultiplier()
         */
        public void setParallaxMultiplier(float multiplier) {
            mParallaxMult = multiplier;
        }

        /**
         * Returns the parallax scroll multiplier used in conjunction with
         * {@link #COLLAPSE_MODE_PARALLAX}.
         *
         * @see #setParallaxMultiplier(float)
         */
        public float getParallaxMultiplier() {
            return mParallaxMult;
        }
    }

    private class OffsetUpdateListener implements AppBarLayout.OnOffsetChangedListener {
        @Override
        public void onOffsetChanged(AppBarLayout layout, int verticalOffset) {
            mCurrentOffset = verticalOffset;

            final int insetTop = mLastInsets != null ? mLastInsets.getSystemWindowInsetTop() : 0;
            final int scrollRange = layout.getTotalScrollRange();

            boolean hasImmersiveImage = false;

            for (int i = 0, z = getChildCount(); i < z; i++) {
                final View child = getChildAt(i);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                final ViewOffsetHelper offsetHelper = getViewOffsetHelper(child);

                if (child instanceof ImageView && lp.width == LayoutParams.MATCH_PARENT)
                    hasImmersiveImage = true;

                switch (lp.mCollapseMode) {
                    case LayoutParams.COLLAPSE_MODE_PIN:
                        if (getHeight() - insetTop + verticalOffset >= child.getHeight()) {
                            offsetHelper.setTopAndBottomOffset(-verticalOffset);
                        }
                        break;
                    case LayoutParams.COLLAPSE_MODE_PARALLAX:
                        offsetHelper.setTopAndBottomOffset(
                                Math.round(-verticalOffset * lp.mParallaxMult));
                        break;
                }
            }

            // Show or hide the scrims if needed
            if (mContentScrim != null || mStatusBarScrim != null) {
                setScrimsShown(getHeight() + verticalOffset < getScrimTriggerOffset() + insetTop);
            }

            if (mStatusBarScrim != null && insetTop > 0) {
                ViewCompat.postInvalidateOnAnimation(FlexibleToolbarLayout.this);
            }

            // Update the collapsing text's fraction
            final int expandRange = getHeight() - ViewCompat.getMinimumHeight(
                    FlexibleToolbarLayout.this) - insetTop;
            float fraction = Math.abs(verticalOffset) / (float) expandRange;
            mTitleCollapsingTextHelper.setExpansionFraction(fraction);
            mSubtitleCollapsingTextHelper.setExpansionFraction(fraction);
            mIconCollapsingHelper.setExpansionFraction(fraction);

            // TODO: Better handling of elevation here (only set it to 0 if image is used)
            if (Math.abs(verticalOffset) == scrollRange) {
                // If we have some pinned children, and we're offset to only show those views,
                // we want to be elevate
                ViewCompat.setElevation(layout, layout.getTargetElevation());
            } else if (hasImmersiveImage) {
                // Otherwise, we're inline with the content
                ViewCompat.setElevation(layout, 0f);
            }
        }
    }
}

