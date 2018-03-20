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

import android.graphics.drawable.Drawable;

import com.tr4android.support.extension.widget.AccountHeaderView;

/**
 * Interface representing an account for displaying in the {@link AccountHeaderView}
 *
 * @since 0.4.1
 */
public interface IAccount {
    /**
     * Get the Icon.
     *
     * @return The icon as a drawable.
     * @since 0.4.1
     */
    Drawable getIconDrawable();

    /**
     * Get the name. Note that the name is only shown for the primary account.
     *
     * @return The name.
     * @since 0.4.1
     */
    String getName();

    /**
     * Get the email address. This is the subheader of the account and will be shown below the name.
     * It also is shown in the list.
     *
     * @return The email address or any other subheader.
     * @since 0.4.1
     */
    String getEmail();

    /**
     * Get the info icon drawable. The icon is shown to the right of the email address and to the left of the info text.
     *
     * @return The info icon drawable.
     * @since 0.4.1
     */
    Drawable getInfoIconDrawable();

    /**
     * Get the info text. The text is shown to the right of its icon.
     *
     * @return The info text.
     * @since 0.4.1
     */
    String getInfoText();

    /**
     * Check if the account is checked.
     *
     * @return True if the account is checked. False otherwise.
     * @since 0.4.1
     */
    boolean isChecked();

    /**
     * Set whether account is checked or not.
     *
     * @param checked True if the account should be checked. False otherwise.
     * @since 0.4.1
     */
    void setChecked(boolean checked);
}