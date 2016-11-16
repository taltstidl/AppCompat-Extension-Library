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

package com.tr4android.support.extension.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.design.internal.NavigationMenuView;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tr4android.appcompat.extension.R;
import com.tr4android.support.extension.animation.AnimationUtils;
import com.tr4android.support.extension.drawable.RotationTransitionDrawable;
import com.tr4android.support.extension.internal.AccountAdapter;
import com.tr4android.support.extension.internal.AccountUtils;
import com.tr4android.support.extension.internal.IAccount;
import com.tr4android.support.extension.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * An account header layout build specifically for the AppCompat Design Library NavigationView
 */
public class AccountHeaderView extends RelativeLayout {
    private static final String LOG_TAG = "AccountHeaderView";

    // The RecyclerView subclass used in the AppCompat Design Library NavigationView
    // Note: this is prone to changes in the official support library
    private NavigationMenuView mNavigationMenuView;

    // The parent of this view (for removing and adding itself)
    private ViewGroup mParent;

    // The menu adapter used by the NavigationView
    private RecyclerView.Adapter mMenuAdapter;

    // The account list adapter
    private AccountAdapter mAccountListAdapter;

    // Whether or not the account list is currently shown
    private boolean mIsShowingAccountList;

    // The click listener for showing the account list
    private OnClickListener mShowAccountClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mIsShowingAccountList) {
                hideAccountList();
            } else {
                showAccountList();
            }
        }
    };

    // The listener for account related actions
    private OnAccountSelectedListener mListener;

    // The CircleImageViews used
    private CircleImageView mPrimaryIconView;
    private CircleImageView mSecondaryFirstIconView;
    private CircleImageView mSecondarySecondIconView;

    // The TextVies used
    private TextView mNameView;
    private TextView mEmailView;

    // The ImageView used for toggling the account list
    private ImageView mDropdownView;
    private final RotationTransitionDrawable mDropdownDrawable;

    public AccountHeaderView(Context context) {
        this(context, null);
    }

    public AccountHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AccountHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(new ContextThemeWrapper(context, R.style.Widget_Design_AccountHeaderView), attrs, defStyleAttr);

        // Provide default background if none is set
        if (getBackground() == null) {
            setBackgroundColor(ThemeUtils.getThemeAttrColor(context, R.attr.colorPrimary));
        }

        // Retrieve the style attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AccountHeaderView, defStyleAttr, 0);
        boolean mAddAccountEnabled = a.getBoolean(R.styleable.AccountHeaderView_accountHeaderAddEnabled, true);
        boolean mManageAccountEnabled = a.getBoolean(R.styleable.AccountHeaderView_accountHeaderManageEnabled, true);
        boolean mCheckableAccountsEnabled = a.getBoolean(R.styleable.AccountHeaderView_accountHeaderCheckableAccountsEnabled, false);
        a.recycle();

        // Inflate the layout
        LayoutInflater.from(context).inflate(R.layout.appcompat_extension_account_header, this, true);

        // Create an empty adapter (for "Add account" and "Manage accounts" entries)
        mAccountListAdapter = new AccountAdapter(new ArrayList<IAccount>(), this,
                mAddAccountEnabled, mManageAccountEnabled, mCheckableAccountsEnabled);

        // Retrieve and setup the CircleImageViews used
        mPrimaryIconView = (CircleImageView) findViewById(R.id.account_header_icon_primary);
        mSecondaryFirstIconView = (CircleImageView) findViewById(R.id.account_header_icon_secondary_first);
        mSecondaryFirstIconView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAccountClick(1);
            }
        });
        mSecondarySecondIconView = (CircleImageView) findViewById(R.id.account_header_icon_secondary_second);
        mSecondarySecondIconView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAccountClick(2);
            }
        });

        // Retrieve the TextViews used
        mNameView = (TextView) findViewById(R.id.account_header_text_name);
        mEmailView = (TextView) findViewById(R.id.account_header_text_email);

        // Retrieve and setup the ImageView
        mDropdownView = (ImageView) findViewById(R.id.account_header_dropdown);
        mDropdownDrawable = new RotationTransitionDrawable(mDropdownView.getDrawable());
        mDropdownDrawable.setMaxRotation(-180f);
        mDropdownDrawable.setStartInterpolator(AnimationUtils.ACCELERATE_DECELERATE_INTERPOLATOR);
        mDropdownDrawable.setReverseInterpolator(AnimationUtils.ACCELERATE_DECELERATE_INTERPOLATOR);
        mDropdownView.setImageDrawable(mDropdownDrawable);
        mDropdownView.setOnClickListener(mShowAccountClickListener);
    }

    // Handles an account click passed on by the account list adapter
    public void handleAccountClick(int position) {
        hideAccountList();
        int count = mAccountListAdapter.getItemCount();
        if (position < mAccountListAdapter.getAccountCount()) {
            IAccount selectedAccount = mAccountListAdapter.get(position);
            boolean makePrimary = true;
            if (mListener != null) makePrimary = mListener.onAccountSelected(selectedAccount);
            if (makePrimary) switchPrimaryAccount(selectedAccount);
        } else if (position == count - 1 && mAccountListAdapter.isAccountManageEnabled()) {
            if (mListener != null) mListener.onAccountManageSelected();
        } else {
            if (mListener != null) mListener.onAccountAddSelected();
        }
    }

    // Handles an account check passed on by the account list adapter
    public void handleAccountCheck(int position, boolean isChecked) {
        IAccount checkedAccount = mAccountListAdapter.get(position);
        if (mListener != null) mListener.onAccountChecked(checkedAccount, isChecked);
    }

    public void addAccounts(IAccount... accounts) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            for (IAccount account : accounts) {
                mAccountListAdapter.add(account);
            }
        } else {
            mAccountListAdapter.addAll(accounts);
        }
        updateAccountHeader();
    }

    public void addAccount(IAccount account) {
        mAccountListAdapter.add(account);
        updateAccountHeader();
    }

    public void removeAccount(IAccount account) {
        mAccountListAdapter.remove(account);
        updateAccountHeader();
    }

    public void insertAccount(IAccount account, int position) {
        mAccountListAdapter.insert(account, position);
        updateAccountHeader();
    }

    public void clearAccounts() {
        mAccountListAdapter.clear();
        updateAccountHeader();
    }

    public List<IAccount> getAccounts() {
        return mAccountListAdapter.getAccounts();
    }

    public void setAccounts(List<IAccount> accounts) {
        mAccountListAdapter.setAccounts(accounts);
        updateAccountHeader();
    }

    public void checkAccount(IAccount account, boolean checked) {
        mAccountListAdapter.setChecked(mAccountListAdapter.indexOf(account), checked);
    }

    public ArrayList<IAccount> getCheckedAccounts() {
        return mAccountListAdapter.getChecked();
    }

    public void setAccountSelectedListener(OnAccountSelectedListener listener) {
        mListener = listener;
    }

    public boolean isShowingAccountList() {
        return mIsShowingAccountList;
    }

    public void showAccountList() {
        if (!mIsShowingAccountList) {
            mDropdownDrawable.startTransition(200);
            // cache menu adapter for later
            removeViewFromParent();
            mMenuAdapter = mNavigationMenuView.getAdapter();
            mNavigationMenuView.setAdapter(mAccountListAdapter);
        }
        mIsShowingAccountList = true;
    }

    public void hideAccountList() {
        if (mIsShowingAccountList) {
            mDropdownDrawable.reverseTransition(200);
            // reset previously cached menu adapter
            mNavigationMenuView.setAdapter(mMenuAdapter);
            mMenuAdapter = null;
            addViewToParent();
        }
        mIsShowingAccountList = false;
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0 && mNavigationMenuView == null)
            mNavigationMenuView = (NavigationMenuView) getParent().getParent();
    }

    private void switchPrimaryAccount(IAccount newPrimaryAccount) {
        mAccountListAdapter.move(newPrimaryAccount, 0);
        updateAccountHeader();
    }

    private void updateAccountHeader() {
        int accountCount = mAccountListAdapter.getAccountCount();
        // primary account
        if (accountCount == 0) {
            mPrimaryIconView.setVisibility(GONE);
            mNameView.setVisibility(GONE);
            mEmailView.setVisibility(GONE);
        } else {
            IAccount primaryAccount = mAccountListAdapter.get(0);
            AccountUtils.applyAccountIcon(primaryAccount, mPrimaryIconView);
            AccountUtils.applyAccountName(primaryAccount, mNameView);
            AccountUtils.applyAccountEmail(primaryAccount, mEmailView);
            mPrimaryIconView.setVisibility(VISIBLE);
            mNameView.setVisibility(VISIBLE);
            mEmailView.setVisibility(VISIBLE);
        }
        // first secondary account
        if (accountCount <= 1) {
            mSecondaryFirstIconView.setVisibility(GONE);
        } else {
            AccountUtils.applyAccountIcon(mAccountListAdapter.get(1), mSecondaryFirstIconView);
            mSecondaryFirstIconView.setVisibility(VISIBLE);
        }
        // second secondary account
        if (accountCount <= 2) {
            mSecondarySecondIconView.setVisibility(GONE);
        } else {
            AccountUtils.applyAccountIcon(mAccountListAdapter.get(2), mSecondarySecondIconView);
            mSecondarySecondIconView.setVisibility(VISIBLE);
        }
    }

    private void removeViewFromParent() {
        getViewParent().removeView(this);
    }

    private void addViewToParent() {
        getViewParent().addView(this);
    }

    private ViewGroup getViewParent() {
        if (mParent == null) {
            mParent = (ViewGroup) getParent();
        }
        return mParent;
    }

    /**
     * Listener for handling events on accounts
     */
    public interface OnAccountSelectedListener {

        /**
         * Called when an account in the account header is selected.
         *
         * @param account The selected account
         * @return true to display the selected account as the primary account
         */
        boolean onAccountSelected(IAccount account);

        /**
         * Called when an account in the account header is checked
         *
         * @param account   The checked account
         * @param isChecked True if the account is now checked
         */
        void onAccountChecked(IAccount account, boolean isChecked);

        /**
         * Called when the "Add account" item is selected.
         */
        void onAccountAddSelected();

        /**
         * Called when the "Manage accounts" item is selected.
         */
        void onAccountManageSelected();
    }

    /**
     * Adapter class for listener
     */
    public static class OnAccountSelectedListenerAdapter implements OnAccountSelectedListener {
        @Override
        public boolean onAccountSelected(IAccount account) {
            return true;
        }

        @Override
        public void onAccountChecked(IAccount account, boolean isChecked) {
        }

        @Override
        public void onAccountAddSelected() {
        }

        @Override
        public void onAccountManageSelected() {
        }
    }
}
