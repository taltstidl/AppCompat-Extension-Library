package com.tr4android.support.extension.internal;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.widget.TextView;

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
        } else {
            iv.setPlaceholder(CircleImageView.retrieveLetter(mName));
        }
    }
}
