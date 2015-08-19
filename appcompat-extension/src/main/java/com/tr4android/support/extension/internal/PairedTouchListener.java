package com.tr4android.support.extension.internal;

import android.view.MotionEvent;
import android.view.View;

/**
 * An OnTouchListener that allows pairing the TouchEvents of one view to another view
 */
public class PairedTouchListener implements View.OnTouchListener {
    // The view the OnTouchListener is paired to
    private View mPairedView;

    public PairedTouchListener(View pairedView) {
        mPairedView = pairedView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.onTouchEvent(event);
        // put the location in the middle of the paired view
        event.setLocation(mPairedView.getWidth()/2, mPairedView.getHeight()/2);
        mPairedView.onTouchEvent(event);
        return true;
    }
}
