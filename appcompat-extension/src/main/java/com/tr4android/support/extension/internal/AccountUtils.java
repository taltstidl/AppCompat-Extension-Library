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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tr4android.appcompat.extension.R;
import com.tr4android.support.extension.widget.CircleImageView;

/**
 * A helper class for {@link AccountAdapter} and {@link com.tr4android.support.extension.widget.AccountHeaderView}.
 * It contains methods that apply data of an {@link IAccount} or {@link Account} on views.
 */

public class AccountUtils {

    /**
     * Applies the account's name to the given TextView.
     *
     * @param account The account that should be used.
     * @param tv      The TextView the account's name should be applied on.
     */
    public static void applyAccountName(IAccount account, TextView tv) {
        tv.setText(account.getName());
    }

    /**
     * Applies the account's email address (or subheader) to the given TextView.
     *
     * @param account The account that should be used.
     * @param tv      The TextView the account' email address should be applied on.
     */
    public static void applyAccountEmail(IAccount account, TextView tv) {
        tv.setText(account.getEmail());
    }

    /**
     * Applies the account's icon to the given ImageView.
     *
     * @param account The account that should be used.
     * @param iv      The CircleImageView the account' icon should be applied on.
     */
    public static void applyAccountIcon(IAccount account, CircleImageView iv) {
        iv.setCircleImageEnabled(true);
        Drawable drawable = account.getIconDrawable();
        if (drawable != null) {
            iv.setImageDrawable(drawable);
        } else if (account instanceof Account) {
            Account a = (Account) account;
            Bitmap iconBitmap = a.getIconBitmap();
            int iconResource = a.getIconResource();
            Uri iconUri = a.getIconUri();
            boolean placeholderIconEnabled = a.getPlaceholderIconEnabled();
            int placeholderCircleColor = a.getPlaceholderCircleColor();
            if (iconBitmap != null) {
                iv.setImageBitmap(iconBitmap);
            } else if (iconResource != 0) {
                iv.setImageResource(iconResource);
            } else if (iconUri != null) {
                iv.setImageURI(iconUri);
            } else if (placeholderIconEnabled) {
                if (placeholderCircleColor == -1) {
                    iv.setPlaceholder(R.drawable.ic_person_black_24dp);
                } else {
                    iv.setPlaceholder(R.drawable.ic_person_black_24dp, placeholderCircleColor);
                }
            } else {
                if (placeholderCircleColor == -1) {
                    iv.setPlaceholder(CircleImageView.retrieveLetter(account.getName()));
                } else {
                    iv.setPlaceholder(CircleImageView.retrieveLetter(account.getName()), placeholderCircleColor);
                }
            }
        } else {
            // fallback
            iv.setPlaceholder(CircleImageView.retrieveLetter(account.getName()));
        }
    }

    /**
     * Applies the account's info to the given views.
     *
     * @param account The account that should be used.
     * @param layout  The layout containing the ImageView {@param iv} and the TextView {@param tv}.
     * @param iv      The ImageView the account's info icon should be applied on.
     * @param tv      The TextView the account's info text should be applied on.
     */
    public static void applyAccountInfo(IAccount account, ViewGroup layout, ImageView iv, TextView tv) {
        Drawable infoIconDrawable = account.getInfoIconDrawable();
        int infoIconResource = account instanceof Account ? ((Account) account).getInfoIconResource() : 0;
        String infoText = account.getInfoText();
        boolean hasIcon = infoIconResource != 0 || infoIconDrawable != null;
        boolean hasText = infoText != null;
        layout.setVisibility((hasIcon || hasText) ? View.VISIBLE : View.GONE);
        iv.setVisibility(hasIcon ? View.VISIBLE : View.GONE);
        tv.setVisibility(hasText ? View.VISIBLE : View.GONE);
        if (hasIcon) {
            if (infoIconResource != 0) {
                iv.setImageResource(infoIconResource);
            } else {
                iv.setImageDrawable(infoIconDrawable);
            }
        }
        if (hasText) {
            tv.setText(infoText);
        }
    }
}
