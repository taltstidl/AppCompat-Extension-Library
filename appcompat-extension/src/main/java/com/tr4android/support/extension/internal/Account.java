/*
 * Copyright (C) 2016 Thomas Robert Altstidl
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

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tr4android.support.extension.widget.AccountHeaderView;
import com.tr4android.support.extension.widget.CircleImageView;

/**
 * Class representing an account for displaying in the {@link AccountHeaderView}
 */
public class Account implements IAccount {
    // Icon
    private Bitmap mIconBitmap;
    private Drawable mIconDrawable;
    private int mIconResource;
    private Uri mIconUri;

    // Full name
    private String mName;

    // Email address
    private String mEmail;

    // Extra info
    private Drawable mInfoIconDrawable;
    private int mInfoIconResource;
    private String mInfoText;

    // Placeholder
    private boolean mPlaceholderIconEnabled;
    private int mPlaceholderCircleColor = -1;

    // Checked
    private boolean mChecked;

    public Account() {
    }

    public Account setIconBitmap(Bitmap mIconBitmap) {
        this.mIconBitmap = mIconBitmap;
        return this;
    }

    public Account setIconDrawable(Drawable mIconDrawable) {
        this.mIconDrawable = mIconDrawable;
        return this;
    }

    public Account setIconResource(@DrawableRes int mIconResource) {
        this.mIconResource = mIconResource;
        return this;
    }

    public Account setIconUri(Uri mIconUri) {
        this.mIconUri = mIconUri;
        return this;
    }

    public Account setName(String mName) {
        this.mName = mName;
        return this;
    }

    public Account setEmail(String mEmail) {
        this.mEmail = mEmail;
        return this;
    }

    public Account setInfoIconDrawable(Drawable mInfoIconDrawable) {
        this.mInfoIconDrawable = mInfoIconDrawable;
        return this;
    }

    public Account setInfoIconResource(@DrawableRes int mInfoIconResource) {
        this.mInfoIconResource = mInfoIconResource;
        return this;
    }

    public Account setInfoText(String mInfoText) {
        this.mInfoText = mInfoText;
        return this;
    }

    public Account setPlaceholderIconEnabled(boolean mPlaceholderIconEnabled) {
        this.mPlaceholderIconEnabled = mPlaceholderIconEnabled;
        return this;
    }

    public Account setPlaceholderCircleColor(@ColorInt int mPlaceholderCircleColor) {
        this.mPlaceholderCircleColor = mPlaceholderCircleColor;
        return this;
    }

    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
    }

    public Bitmap getIconBitmap() {
        return mIconBitmap;
    }

    @Override
    public Drawable getIconDrawable() {
        return mIconDrawable;
    }

    @DrawableRes
    public int getIconResource() {
        return mIconResource;
    }

    public Uri getIconUri() {
        return mIconUri;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public String getEmail() {
        return mEmail;
    }

    @Override
    public Drawable getInfoIconDrawable() {
        return mInfoIconDrawable;
    }

    @DrawableRes
    public int getInfoIconResource() {
        return mInfoIconResource;
    }

    @Override
    public String getInfoText() {
        return mInfoText;
    }

    public boolean getPlaceholderIconEnabled() {
        return mPlaceholderIconEnabled;
    }

    public int getPlaceholderCircleColor() {
        return mPlaceholderCircleColor;
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    public void applyAccountName(TextView tv) {
        AccountUtils.applyAccountName(this, tv);
    }

    public void applyAccountEmail(TextView tv) {
        AccountUtils.applyAccountEmail(this, tv);
    }

    public void applyAccountIcon(CircleImageView iv) {
        AccountUtils.applyAccountIcon(this, iv);
    }

    public void applyAccountInfo(ViewGroup layout, ImageView iv, TextView tv) {
        AccountUtils.applyAccountInfo(this, layout, iv, tv);
    }
}