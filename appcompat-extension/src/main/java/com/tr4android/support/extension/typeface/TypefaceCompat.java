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

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.util.LruCache;

import java.util.HashMap;

public class TypefaceCompat {
    private static final HashMap<String, String> FONT_FAMILY_FILE_PREFIX = new HashMap<>();
    private static final String[] STYLE_SUFFIX = new String[]{"Regular", "Bold", "Italic", "BoldItalic"};
    private static final String TTF_SUFFIX = ".ttf";

    private static final int TYPEFACE_CACHE_MAX_SIZE = 8;
    private static final LruCache<String, Typeface> TYPEFACE_CACHE = new LruCache<>(TYPEFACE_CACHE_MAX_SIZE);

    static {
        FONT_FAMILY_FILE_PREFIX.put("sans-serif", "Roboto-");
        FONT_FAMILY_FILE_PREFIX.put("sans-serif-light", "Roboto-Light");
        FONT_FAMILY_FILE_PREFIX.put("sans-serif-thin", "Roboto-Thin");
        FONT_FAMILY_FILE_PREFIX.put("sans-serif-condensed", "RobotoCondensed-");
        FONT_FAMILY_FILE_PREFIX.put("sans-serif-medium", "Roboto-Medium");
        FONT_FAMILY_FILE_PREFIX.put("sans-serif-black", "Roboto-Black");
        FONT_FAMILY_FILE_PREFIX.put("sans-serif-condensed-light", "RobotoCondensed-Light");
    }

    public static Typeface create(Context ctx, String familyName, int style) {
        if (isSupported(familyName)) {
            boolean styleAfterwards = false;
            String fileName = FONT_FAMILY_FILE_PREFIX.get(familyName);
            if (fileName.endsWith("-")) {
                // All styles are supported.
                fileName += STYLE_SUFFIX[style];
            } else {
                switch (style) {
                    case Typeface.NORMAL:
                        break;
                    case Typeface.BOLD:
                    case Typeface.BOLD_ITALIC:
                        // These styles are not supported by default. Therefore force style after retrieving normal font.
                        styleAfterwards = true;
                        break;
                    case Typeface.ITALIC:
                        fileName += STYLE_SUFFIX[style];
                        break;
                }
            }
            fileName += TTF_SUFFIX;
            // Retrieve Typeface from cache.
            Typeface tf = TYPEFACE_CACHE.get(fileName);
            if (tf == null) {
                // Create Typeface and cache it for later.
                String fontPath = "fonts/" + fileName;
                tf = Typeface.createFromAsset(ctx.getAssets(), fontPath);
                if (tf != null) {
                    TYPEFACE_CACHE.put(fileName, tf);
                }
            }
            if (tf != null) {
                return styleAfterwards ? Typeface.create(tf, style) : tf;
            }
        }
        // Let the default implementation of Typeface try.
        return Typeface.create(familyName, style);
    }

    public static boolean isSupported(String familyName) {
        return FONT_FAMILY_FILE_PREFIX.containsKey(familyName) && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }
}
