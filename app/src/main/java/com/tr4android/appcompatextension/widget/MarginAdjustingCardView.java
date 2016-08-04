package com.tr4android.appcompatextension.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

public class MarginAdjustingCardView extends CardView {

    private boolean mAreMarginsAdjusted = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    private Rect mShadowPadding;

    public MarginAdjustingCardView(Context context) {
        super(context);
    }

    public MarginAdjustingCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarginAdjustingCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * The code below makes sure that the CardView looks correct on pre-Lollipop devices
     * by adjusting the CardView margin
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Update the margins on pre-Lollipop devices
        if (!mAreMarginsAdjusted) {
            calculateShadowPadding();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec)
                    + mShadowPadding.left + mShadowPadding.right, MeasureSpec.getMode(widthMeasureSpec));
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec)
                    + mShadowPadding.top + mShadowPadding.bottom, MeasureSpec.getMode(heightMeasureSpec));
            MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
            params.setMargins(params.leftMargin - mShadowPadding.left,
                    params.topMargin - mShadowPadding.top,
                    params.rightMargin - mShadowPadding.right,
                    params.bottomMargin - mShadowPadding.bottom);
            requestLayout();
            mAreMarginsAdjusted = true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void calculateShadowPadding() {
        int left = getPaddingLeft() - getContentPaddingLeft();
        int top = getPaddingTop() - getContentPaddingTop();
        int right = getPaddingRight() - getContentPaddingRight();
        int bottom = getPaddingBottom() - getContentPaddingBottom();
        mShadowPadding = new Rect(left, top, right, bottom);
    }
}