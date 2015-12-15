package com.tr4android.support.extension.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;

import com.tr4android.support.extension.animation.AnimationUtils;
import com.tr4android.support.extension.animation.ValueAnimatorCompat;

public class MediaControlDrawable extends Drawable {
    private static final String TAG = "Media Control";

    public enum State {
        PLAY, PAUSE, STOP
    }

    private State mCurrentState = State.PLAY;
    private State mTargetState = State.PLAY;

    // Paint
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mPadding = 0f;
    private RectF mInternalBounds = new RectF();

    // Values that determine the current drawing state
    private float mRotation;
    private Path mPrimaryPath = new Path();
    private Path mSecondaryPath = new Path();

    // Cached metrics
    private float mCenter;
    private float mSize;
    private float mPlayTipOffset;
    private float mPlayBaseOffset;

    // Animator
    private ValueAnimatorCompat mAnimator;

    public MediaControlDrawable(Context context, @ColorInt int color, float padding, State state) {
        mPadding = padding;
        mCurrentState = state;
        mTargetState = state;

        // The paint used to draw the icons
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(color);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();

        int saveCount = canvas.save();
        canvas.rotate(mRotation, bounds.centerX(), bounds.centerY());

        // Draw the previously calculated paths
        canvas.drawPath(mPrimaryPath, mPaint);
        canvas.drawPath(mSecondaryPath, mPaint);

        canvas.restoreToCount(saveCount);
    }

    @Override
    public void setBounds(Rect bounds) {
        calculateTrimArea(bounds);
        super.setBounds(bounds);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        calculateTrimArea(new Rect(left, top, right, bottom));
        super.setBounds(left, top, right, bottom);
    }

    @Override
    public void setAlpha(int i) {
        mPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }


    private void setTransitionState(float rotation, float fraction) {
        if (fraction == 1f) mCurrentState = mTargetState;
        if (mCurrentState == mTargetState) rotation = fraction = 0f;
        // Calculate current drawable metrics
        mRotation = rotation;

        mPrimaryPath.reset();
        mSecondaryPath.reset();
        if (mCurrentState == State.PLAY && (mTargetState == State.STOP
                || mTargetState == State.PLAY)) {
            // Transition between play and stop icon
            float offset = (1f - fraction) * mPlayTipOffset;
            float offsetBase = (1f - fraction) * mPlayBaseOffset;
            mPrimaryPath.moveTo(mInternalBounds.right + offset,
                    interpolate(mCenter, mInternalBounds.bottom, fraction));
            mPrimaryPath.lineTo(mInternalBounds.left + offset,
                    mInternalBounds.bottom + offsetBase);
            mPrimaryPath.lineTo(mInternalBounds.left + offset,
                    interpolate(mCenter, mInternalBounds.top, fraction));
            mPrimaryPath.lineTo(interpolate(mInternalBounds.left, mInternalBounds.right, fraction) + offset,
                    mInternalBounds.top - offsetBase);
        } else if (mCurrentState == State.STOP && mTargetState == State.PAUSE) {
            // Transition between stop and pause icon
            float primaryBottom = mCenter - fraction * 3f / 20f * mSize;
            float secondaryTop = mCenter + fraction * 3f / 20f * mSize;
            mPrimaryPath.moveTo(mInternalBounds.right, mInternalBounds.top);
            mPrimaryPath.lineTo(mInternalBounds.right, primaryBottom);
            mPrimaryPath.lineTo(mInternalBounds.left, primaryBottom);
            mPrimaryPath.lineTo(mInternalBounds.left, mInternalBounds.top);
            mSecondaryPath.moveTo(mInternalBounds.right, mInternalBounds.bottom);
            mSecondaryPath.lineTo(mInternalBounds.right, secondaryTop);
            mSecondaryPath.lineTo(mInternalBounds.left, secondaryTop);
            mSecondaryPath.lineTo(mInternalBounds.left, mInternalBounds.bottom);
        } else if (mCurrentState == State.PAUSE && mTargetState == State.PLAY) {
            // Transition between pause and play icon
            float offset = fraction * mPlayTipOffset;
            float offsetBase = fraction * mPlayBaseOffset;
            float primaryRight = mCenter - Math.max(0f, -2f * fraction + 1f) * 3f / 20f * mSize;
            float primaryBottomLeft = mInternalBounds.left + fraction * (mSize / 2f);
            float secondaryLeft = mCenter + Math.max(0f, -2f * fraction + 1f) * 3f / 20f * mSize;
            float secondaryBottomRight = mInternalBounds.right - fraction * (mSize / 2f);
            mPrimaryPath.moveTo(mInternalBounds.left - offsetBase, mInternalBounds.bottom - offset);
            mPrimaryPath.lineTo(primaryRight, mInternalBounds.bottom - offset);
            mPrimaryPath.lineTo(primaryRight, mInternalBounds.top - offset);
            mPrimaryPath.lineTo(primaryBottomLeft, mInternalBounds.top - offset);
            mSecondaryPath.moveTo(mInternalBounds.right + offsetBase, mInternalBounds.bottom - offset);
            mSecondaryPath.lineTo(secondaryLeft, mInternalBounds.bottom - offset);
            mSecondaryPath.lineTo(secondaryLeft, mInternalBounds.top - offset);
            mSecondaryPath.lineTo(secondaryBottomRight, mInternalBounds.top - offset);
        } else if (mCurrentState == State.PLAY && mTargetState == State.PAUSE) {
            // Transition between play and pause icon
            float offset = (1f - fraction) * mPlayTipOffset;
            float offsetBase = (1f - fraction) * mPlayBaseOffset;
            float primaryBottom = mCenter - Math.max(0f, 2f * fraction - 1f) * 3f / 20f * mSize;
            float primaryLeftTop = mInternalBounds.left + (1f - fraction) * (mSize / 2f);
            float secondaryTop = mCenter + Math.max(0f, 2f * fraction - 1f) * 3f / 20f * mSize;
            float secondaryLeftBottom = mInternalBounds.right - (1f - fraction) * (mSize / 2f);
            mPrimaryPath.moveTo(mInternalBounds.left + offset, mInternalBounds.top - offsetBase);
            mPrimaryPath.lineTo(mInternalBounds.left + offset, primaryBottom);
            mPrimaryPath.lineTo(mInternalBounds.right + offset, primaryBottom);
            mPrimaryPath.lineTo(mInternalBounds.right + offset, primaryLeftTop);
            mSecondaryPath.moveTo(mInternalBounds.left + offset, mInternalBounds.bottom + offsetBase);
            mSecondaryPath.lineTo(mInternalBounds.left + offset, secondaryTop);
            mSecondaryPath.lineTo(mInternalBounds.right + offset, secondaryTop);
            mSecondaryPath.lineTo(mInternalBounds.right + offset, secondaryLeftBottom);
        } else if (mCurrentState == State.PAUSE && (mTargetState == State.STOP
                || mTargetState == State.PAUSE)) {
            // Transition between pause and stop icon
            float primaryRight = mCenter - (1f - fraction) * 3f / 20f * mSize;
            float secondaryLeft = mCenter + (1f - fraction) * 3f / 20f * mSize;
            mPrimaryPath.moveTo(mInternalBounds.left, mInternalBounds.top);
            mPrimaryPath.lineTo(primaryRight, mInternalBounds.top);
            mPrimaryPath.lineTo(primaryRight, mInternalBounds.bottom);
            mPrimaryPath.lineTo(mInternalBounds.left, mInternalBounds.bottom);
            mSecondaryPath.moveTo(mInternalBounds.right, mInternalBounds.top);
            mSecondaryPath.lineTo(secondaryLeft, mInternalBounds.top);
            mSecondaryPath.lineTo(secondaryLeft, mInternalBounds.bottom);
            mSecondaryPath.lineTo(mInternalBounds.right, mInternalBounds.bottom);
        } else if (mCurrentState == State.STOP && (mTargetState == State.PLAY
                || mTargetState == State.STOP)) {
            // Transition between stop and play icon
            float offset = fraction * mPlayTipOffset;
            float offsetBase = fraction * mPlayBaseOffset;
            mPrimaryPath.moveTo(interpolate(mInternalBounds.left, mCenter, fraction),
                    mInternalBounds.top - offset);
            mPrimaryPath.lineTo(mInternalBounds.left - offsetBase,
                    mInternalBounds.bottom - offset);
            mPrimaryPath.lineTo(interpolate(mInternalBounds.right, mCenter, fraction),
                    mInternalBounds.bottom - offset);
            mPrimaryPath.lineTo(mInternalBounds.right + offsetBase,
                    interpolate(mInternalBounds.top, mInternalBounds.bottom, fraction) - offset);
        }

        invalidateSelf();
    }

    /**
     * This calculates the trim area for the icon as specified in the guidelines
     *
     * @param bounds
     * @see <a href="https://www.google.com/design/spec/style/icons.html#icons-system-icons">
     * Google design guidelines - Icon - Style - System Icons</a>
     */
    private void calculateTrimArea(Rect bounds) {
        float padding = mPadding + (bounds.height() - 2f * mPadding) * 1f / 6f;
        mInternalBounds.set(bounds.left + padding, bounds.top + padding,
                bounds.right - padding, bounds.bottom - padding);
        mCenter = mInternalBounds.centerX();
        mSize = mInternalBounds.width();
        mPlayTipOffset = 1f / 6f * mSize;
        mPlayBaseOffset = 0.07735f * mSize;
        setTransitionState(0f, 0f);
    }

    /**
     * Helper for interpolating between two values (ultimately points)
     * This is only used for the more complex play/stop animations
     *
     * @param start    The value of the coordinate at the start of the animation
     * @param end      The value of the coordinate at the end of the animation
     * @param fraction The current fraction of the animation
     * @return The interpolated value
     */
    private float interpolate(float start, float end, float fraction) {
        return (1f - fraction) * start + fraction * end;
    }

    public void setMediaControlState(State state) {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.end();
        }
        mTargetState = state;
        mAnimator = AnimationUtils.createAnimator();
        mAnimator.setFloatValues(0f, 90f);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(AnimationUtils.ACCELERATE_DECELERATE_INTERPOLATOR);
        mAnimator.setUpdateListener(new ValueAnimatorCompat.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimatorCompat animator) {
                setTransitionState(animator.getAnimatedFloatValue(), animator.getAnimatedFraction());
            }
        });
        mAnimator.start();
    }

    public State getMediaControlState() {
        return mCurrentState;
    }

    public static class Builder {
        private Context mContext;
        private int mColor;
        private float mPadding;
        private State mInitialState;

        public Builder(Context context) {
            mContext = context;
            // Default values
            mColor = Color.WHITE;
            mPadding = 0f;
            mInitialState = State.PLAY;
        }

        public Builder setColor(@ColorInt int color) {
            mColor = color;
            return this;
        }

        public Builder setPadding(float padding) {
            mPadding = padding;
            return this;
        }

        public Builder setInitialState(State initialState) {
            mInitialState = initialState;
            return this;
        }

        public MediaControlDrawable build() {
            return new MediaControlDrawable(mContext, mColor, mPadding, mInitialState);
        }
    }
}
