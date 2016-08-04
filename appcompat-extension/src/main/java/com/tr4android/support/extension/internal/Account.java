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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tr4android.appcompat.extension.R;
import com.tr4android.support.extension.widget.AccountHeaderView;
import com.tr4android.support.extension.widget.CircleImageView;

/**
 * Class representing an account for displaying in the {@link AccountHeaderView}
 */
public class Account {
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

    public Account() {}

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

    public Bitmap getIconBitmap() {
        return mIconBitmap;
    }

    public Drawable getIconDrawable() {
        return mIconDrawable;
    }

    public int getIconResource() {
        return mIconResource;
    }

    public Uri getIconUri() {
        return mIconUri;
    }

    public String getName() {
        return mName;
    }

    public String getEmail() {
        return mEmail;
    }

    public Drawable getInfoIconDrawable() {
        return mInfoIconDrawable;
    }

    public int getInfoIconResource() {
        return mInfoIconResource;
    }

    public String getInfoText() {
        return mInfoText;
    }

    public boolean getPlaceholderIconEnabled() {
        return mPlaceholderIconEnabled;
    }

    public int getPlaceholderCircleColor() {
        return mPlaceholderCircleColor;
    }

    public void applyAccountName(TextView tv) {
        tv.setText(mName);
    }

    public void applyAccountEmail(TextView tv) {
        tv.setText(mEmail);
    }

    public void applyAccountIcon(CircleImageView iv) {
        iv.setCircleImageEnabled(true);
        if (mIconBitmap != null) {
            iv.setImageBitmap(mIconBitmap);
        } else if (mIconResource != 0) {
            iv.setImageResource(mIconResource);
        } else if (mIconDrawable != null) {
            iv.setImageDrawable(mIconDrawable);
        } else if (mIconUri != null) {
            iv.setImageURI(mIconUri);
        } else if (mPlaceholderIconEnabled) {
            if (mPlaceholderCircleColor == -1) {
                iv.setPlaceholder(R.drawable.ic_person_black_24dp);
            } else {
                iv.setPlaceholder(R.drawable.ic_person_black_24dp, mPlaceholderCircleColor);
            }
        } else {
            if (mPlaceholderCircleColor == -1) {
                iv.setPlaceholder(CircleImageView.retrieveLetter(mName));
            } else {
                iv.setPlaceholder(CircleImageView.retrieveLetter(mName), mPlaceholderCircleColor);
            }
        }
    }

    public void applyAccountInfo(ViewGroup layout, ImageView iv, TextView tv) {
        boolean hasIcon = mInfoIconResource != 0 || mInfoIconDrawable != null;
        boolean hasText = mInfoText != null;
        layout.setVisibility((hasIcon || hasText) ? View.VISIBLE : View.GONE);
        iv.setVisibility(hasIcon ? View.VISIBLE : View.GONE);
        tv.setVisibility(hasText ? View.VISIBLE : View.GONE);
        if (hasIcon) {
            if (mInfoIconResource != 0) {
                iv.setImageResource(mInfoIconResource);
            } else if (mInfoIconDrawable != null) {
                iv.setImageDrawable(mInfoIconDrawable);
            }
        }
        if (hasText) {
            tv.setText(mInfoText);
        }
    }
}
