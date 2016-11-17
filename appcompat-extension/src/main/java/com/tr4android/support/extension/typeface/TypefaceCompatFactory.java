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
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tr4android.appcompat.extension.R;

/**
 * This Class is a custom {@link LayoutInflaterFactory} that catches all (sub)classes of TextView
 * during inflation and then takes care of reading the fontFamily and style attribute
 * and loading the associated typeface. This needs to be installed in your
 * {@link android.app.Activity#onCreate(Bundle) onCreate()} method <b>before</b> the call to super.onCreate().
 *
 * @since 0.1.1
 */
public class TypefaceCompatFactory implements LayoutInflaterFactory {

    private LayoutInflaterFactory mBaseFactory;

    private TypefaceCompatFactory(Context context, boolean typefaceDetectionEnabled) {
        TypefaceCompat.setTypefaceDetectionEnabled(typefaceDetectionEnabled);
        try {
            this.mBaseFactory = (LayoutInflaterFactory) ((AppCompatActivity) context).getDelegate();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    /**
     * Installs the factory to the given context prior to API level 21.
     * It will use AppCompat's layout inflater to inflate views and
     * set a proper Roboto typeface to the view. Roboto fonts are also used
     * when user is using a custom font.
     *
     * @param context A context.
     * @see #installViewFactory(Context, boolean)
     * @since 0.1.1
     */
    public static void installViewFactory(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            LayoutInflaterCompat.setFactory(LayoutInflater.from(context),
                    new TypefaceCompatFactory(context, false));
        }
    }

    /**
     * Installs the factory to the given context prior to API level 21.
     * It will use AppCompat's layout inflater to inflate views and
     * set a proper typeface to the view if needed.
     * If typeface detection is enabled the factory automatically detects the used system typeface
     * and adjust its behavior properly.
     * This makes sure that the newer Roboto typefaces are only used if no custom typefaces are applied by the system.
     * <p>
     * <b>Note:</b> Typeface detection only works starting with API level 14 and comes with a small performance penalty.
     *
     * @param context                  A context.
     * @param typefaceDetectionEnabled True if the factory should automatically detect the used system typeface and adjust its behavior properly.
     *                                 This makes sure that the newer Roboto typefaces are only used if no custom typefaces are applied by the system.
     * @since 0.1.1
     */
    public static void installViewFactory(Context context, boolean typefaceDetectionEnabled) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            LayoutInflaterCompat.setFactory(LayoutInflater.from(context),
                    new TypefaceCompatFactory(context, typefaceDetectionEnabled));
        }
    }

    /**
     * This method is responsible for creating the correct subclass of View given the xml element name
     * via AppCompat's layout inflater and afterwards sets the correct typeface if needed.
     *
     * @param parent  The future parent of the returned view. Note that this may be null.
     * @param name    The fully qualified class name of the View to be create.
     * @param context The context the view is being created in.
     * @param attrs   An AttributeSet of attributes to apply to the View.
     * @return The newly created view.
     * @since 0.1.1
     */
    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View result = null;
        // Allow base factory to try and create a view first
        if (mBaseFactory != null) {
            result = mBaseFactory.onCreateView(parent, name, context, attrs);
        }
        if (result instanceof TextView) {
            TextView textView = (TextView) result;
            String fontFamily = null;
            int style = 0;
            Resources.Theme theme = context.getTheme();
            TypedArray a = theme.obtainStyledAttributes(attrs, R.styleable.TextViewAppearance, 0, 0);
            TypedArray appearance = null;
            int ap = a.getResourceId(R.styleable.TextViewAppearance_android_textAppearance, -1);
            a.recycle();
            if (ap != -1) {
                appearance = theme.obtainStyledAttributes(ap, R.styleable.TextAppearance);
            }
            if (appearance != null) {
                fontFamily = appearance.getString(R.styleable.TextAppearance_android_fontFamily);
                style = appearance.getInt(R.styleable.TextAppearance_android_textStyle, 0);
                appearance.recycle();
            }
            a = theme.obtainStyledAttributes(attrs, R.styleable.TextAppearance, 0, 0);
            if (a.hasValue(R.styleable.TextAppearance_android_fontFamily)) {
                fontFamily = a.getString(R.styleable.TextAppearance_android_fontFamily);
                style = a.getInt(R.styleable.TextAppearance_android_textStyle, 0);
            }
            a.recycle();
            if (fontFamily != null && TypefaceCompat.isSupported(fontFamily)) {
                Typeface tf = TypefaceCompat.create(textView.getContext(), fontFamily, style);
                if (tf != null) {
                    textView.setTypeface(tf);
                }
            }
        }
        return result;
    }
}
