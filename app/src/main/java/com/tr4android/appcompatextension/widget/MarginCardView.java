package com.tr4android.appcompatextension.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

public class MarginCardView extends CardView {

    private boolean mAreMarginsAdjusted = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    private Rect mShadowPadding;

    public MarginCardView(Context context) {
        super(context);
    }

    public MarginCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarginCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * This makes sure that the CardView looks correct on pre-Lollipop devices
     */
    @Override
    public void setShadowPadding(int left, int top, int right, int bottom) {
        super.setShadowPadding(left, top, right, bottom);
        mShadowPadding = new Rect(left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Update the margins to clip the outer border on pre-Lollipop devices
        if (!mAreMarginsAdjusted) {
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
}

