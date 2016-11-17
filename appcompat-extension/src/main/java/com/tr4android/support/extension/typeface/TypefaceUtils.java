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


/**
 * A helper class for {@link TypefaceCompat}
 *
 * @since 0.1.1
 */
public class TypefaceUtils {
    private static final String TEXT = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Compares two typefaces and returns true if all letters of the English alphabet of the typefaces are the same.
     * If the method returns false it is likely that the typefaces are not the same, yet not certain.
     * <p>
     * NOTE: This only works starting with API level 14 and comes with a small performance penalty.
     * Only use if you have to and cache the result if you need it later.
     *
     * @param typeface1 The first typeface to be compared.
     * @param typeface2 The second typeface to be compared.
     * @return True if the typefaces are the same. False otherwise.
     * @since 0.1.1
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