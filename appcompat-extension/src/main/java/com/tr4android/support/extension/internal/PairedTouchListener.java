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
