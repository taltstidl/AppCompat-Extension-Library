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
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tr4android.appcompat.extension.R;

public class TypefaceCompatFactory implements LayoutInflaterFactory {

    private LayoutInflaterFactory mBaseFactory;

    private TypefaceCompatFactory(Context context) {
        try {
            this.mBaseFactory = (LayoutInflaterFactory) ((AppCompatActivity) context).getDelegate();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    public static void installViewFactory(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            LayoutInflaterCompat.setFactory(LayoutInflater.from(context), new TypefaceCompatFactory(context));
        }
    }

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
