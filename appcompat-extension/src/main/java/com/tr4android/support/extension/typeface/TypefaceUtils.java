/*
 * Copyright (C) 2015 fountaingeyser
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

package com.tr4android.support.extension.typeface;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;

public class TypefaceUtils {
    private static final String TEXT = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Returns true if all letters of the English alphabet of the typefaces are the same.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public static boolean sameAs(Typeface typeface1, Typeface typeface2) {
        // Handle null as param.
        if (typeface1 == null) {
            return typeface2 == null;
        } else if (typeface2 == null) {
            return false; //result of typeface1 == null
        }

        // Check if the letters of the English alphabet of the typefaces are the same.
        Paint paint = new Paint();
        paint.setTypeface(typeface1);
        Rect bounds = new Rect();
        paint.getTextBounds(TEXT, 0, TEXT.length(), bounds);
        Bitmap bitmap1 = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(bitmap1);
        canvas.drawText(TEXT, 0, 0, paint);

        paint.setTypeface(typeface2);
        paint.getTextBounds(TEXT, 0, TEXT.length(), bounds);
        Bitmap bitmap2 = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ALPHA_8);
        canvas.setBitmap(bitmap2);
        canvas.drawText(TEXT, 0, 0, paint);

        return bitmap1.sameAs(bitmap2);
    }
}