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
import android.os.Environment;
import android.support.v4.util.LruCache;

import java.util.HashMap;

/**
 * Compatibility class that implements backwards support for the text styles recommended
 * in the Typography section of the Material Design guidelines on all devices back to API level 7.
 * If Typeface detection is enabled TypefaceCompat will respect custom system typefaces while still maintaining
 * the newest default typeface (Roboto) on all devices back to API level 14.
 *
 * @since 0.1.1
 */
public class TypefaceCompat {
    private static final HashMap<String, String> FONT_FAMILY_FILE_PREFIX = new HashMap<>();
    private static final String[] STYLE_SUFFIX = new String[]{"Regular", "Bold", "Italic", "BoldItalic"};
    private static final String TTF_SUFFIX = ".ttf";

    private static final int TYPEFACE_CACHE_MAX_SIZE = 8;
    private static final LruCache<String, Typeface> TYPEFACE_CACHE = new LruCache<>(TYPEFACE_CACHE_MAX_SIZE);

    private static final String SYSTEM_ROBOTO_REGULAR_FILE_PATH = Environment.getRootDirectory() + "/fonts/Roboto-Regular.ttf";

    private static boolean mIsUsingDefaultFont = true; // boolean indicating whether user wants the device to use its default font or not
    private static boolean mTypefaceDetectionEnabled = true;

    private static boolean mInitialized;

    static {
        FONT_FAMILY_FILE_PREFIX.put("sans-serif", "Roboto-");
        FONT_FAMILY_FILE_PREFIX.put("sans-serif-light", "Roboto-Light");
        FONT_FAMILY_FILE_PREFIX.put("sans-serif-thin", "Roboto-Thin");
        FONT_FAMILY_FILE_PREFIX.put("sans-serif-condensed", "RobotoCondensed-");
        FONT_FAMILY_FILE_PREFIX.put("sans-serif-medium", "Roboto-Medium");
        FONT_FAMILY_FILE_PREFIX.put("sans-serif-black", "Roboto-Black");
        FONT_FAMILY_FILE_PREFIX.put("sans-serif-condensed-light", "RobotoCondensed-Light");
    }

    private static void initialize() {
        if (mTypefaceDetectionEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Typeface roboto = Typeface.createFromFile(SYSTEM_ROBOTO_REGULAR_FILE_PATH);
            if (roboto != null) {
                mIsUsingDefaultFont = TypefaceUtils.sameAs(roboto, Typeface.SANS_SERIF);
            }
        }
        mInitialized = true;
    }

    /**
     * Use {@link #setTypefaceDetectionEnabled(boolean)} instead.
     *
     * @param typefaceDetectionEnabled True if the used system typeface should be automatically detected and behavior properly adjusted.
     *                                 This makes sure that the newer Roboto typefaces are only used if no custom typefaces are applied by the system.
     * @see #setTypefaceDetectionEnabled(boolean)
     * @since 0.1.1
     * @deprecated
     */
    @Deprecated
    public static void initialize(boolean typefaceDetectionEnabled) {
        setTypefaceDetectionEnabled(typefaceDetectionEnabled);
    }

    /**
     * Set whether the typeface detection should be enabled. By default typeface detection is enabled.
     * If typeface detection is enabled it will respect custom system typefaces.
     * <p>
     * <b>Note:</b> This only works starting with API level 14 and comes with a small performance penalty.
     *
     * @param typefaceDetectionEnabled True if the used system typeface should be automatically detected and behavior properly adjusted.
     *                                 This makes sure that the newer Roboto typefaces are only used if no custom typefaces are applied by the system.
     * @since 0.4.1
     */
    public static void setTypefaceDetectionEnabled(boolean typefaceDetectionEnabled) {
        mTypefaceDetectionEnabled = typefaceDetectionEnabled;
        initialize();
    }

    /**
     * Creates a typeface object that best matches the specified typeface and the specified style.
     * Use this call if you want to pick a new style from the same family of an typeface object.
     * If family is null, this selects from the default font's family.
     *
     * @param ctx        A context.
     * @param familyName May be null. The name of the font family.
     * @param style      The style (normal, bold, italic) of the typeface, e.g. NORMAL, BOLD, ITALIC, BOLD_ITALIC.
     * @return The best matching typeface.
     * @since 0.1.1
     */
    public static Typeface create(Context ctx, String familyName, int style) {
        if (!mInitialized) initialize();
        if (isSupported(familyName) || familyName == null) {
            boolean styleAfterwards = false;
            String fileName = FONT_FAMILY_FILE_PREFIX.get(familyName == null ? "sans-serif" : familyName);
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

    /**
     * Checks if a certain font family is supported.
     *
     * @param familyName The name of the font family.
     * @return True if the font family is supported. False otherwise.
     * @since 0.1.1
     */
    public static boolean isSupported(String familyName) {
        if (!mInitialized) initialize();
        return FONT_FAMILY_FILE_PREFIX.containsKey(familyName) && mIsUsingDefaultFont && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }
}