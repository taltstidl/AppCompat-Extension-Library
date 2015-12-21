/*
 * Copyright (C) 2015 Thomas Robert Altstidl
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tr4android.support.extension.internal.Account;
import com.tr4android.support.extension.internal.AccountAdapter;

import com.tr4android.appcompat.extension.R;
import com.tr4android.support.extension.utils.ThemeUtils;

import java.util.ArrayList;

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
                // reset previously cached menu adapter
                mNavigationMenuView.setAdapter(mMenuAdapter);
                mMenuAdapter = null;
                addViewToParent();
            } else {
                // cache menu adapter for later
                removeViewFromParent();
                mMenuAdapter = mNavigationMenuView.getAdapter();
                mNavigationMenuView.setAdapter(mAccountListAdapter);
            }
            mIsShowingAccountList = !mIsShowingAccountList;
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
        a.recycle();

        // Inflate the layout
        LayoutInflater.from(context).inflate(R.layout.appcompat_extension_account_header, this, true);

        // Create an empty adapter (for "Add account" and "Manage accounts" entries)
        mAccountListAdapter = new AccountAdapter(new ArrayList<Account>(), this, mAddAccountEnabled, mManageAccountEnabled);

        // Retrieve and setup the CircleImageViews used
        mPrimaryIconView = (CircleImageView) findViewById(R.id.account_header_icon_primary);
        mSecondaryFirstIconView = (CircleImageView) findViewById(R.id.account_header_icon_secondary_first);
        mSecondaryFirstIconView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Account selectedAccount = mAccountListAdapter.get(1);
                switchPrimaryAccount(selectedAccount);
                if (mListener != null) mListener.onAccountSelected(selectedAccount);
            }
        });
        mSecondarySecondIconView = (CircleImageView) findViewById(R.id.account_header_icon_secondary_second);
        mSecondarySecondIconView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Account selectedAccount = mAccountListAdapter.get(2);
                switchPrimaryAccount(selectedAccount);
                if (mListener != null) mListener.onAccountSelected(selectedAccount);
            }
        });

        // Retrieve the TextViews used
        mNameView = (TextView) findViewById(R.id.account_header_text_name);
        mEmailView = (TextView) findViewById(R.id.account_header_text_email);

        findViewById(R.id.account_header_dropdown).setOnClickListener(mShowAccountClickListener);
    }

    // Handles a click passed on by the account list adapter
    public void handleAccountClick(int position) {
        hideAccountList();
        int count = mAccountListAdapter.getItemCount();
        if (position < mAccountListAdapter.getAccountCount()) {
            Account selectedAccount = mAccountListAdapter.get(position);
            switchPrimaryAccount(selectedAccount);
            if (mListener != null) mListener.onAccountSelected(selectedAccount);
        } else if (position == count - 1 && mAccountListAdapter.isAccountManageEnabled()) {
            if (mListener != null) mListener.onAccountManageSelected();
        } else {
            if (mListener != null) mListener.onAccountAddSelected();
        }
    }

    public void addAccounts(Account... accounts) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            for (Account account : accounts) {
                mAccountListAdapter.add(account);
            }
        } else {
            mAccountListAdapter.addAll(accounts);
        }
        updateAccountHeader();
    }

    public void addAccount(Account account) {
        mAccountListAdapter.add(account);
        updateAccountHeader();
    }

    public void removeAccount(Account account) {
        mAccountListAdapter.remove(account);
        updateAccountHeader();
    }

    public void insertAccount(Account account, int position) {
        mAccountListAdapter.insert(account, position);
        updateAccountHeader();
    }

    public void clearAccounts() {
        mAccountListAdapter.clear();
        updateAccountHeader();
    }

    public ArrayList<Account> getAccounts() {
        return mAccountListAdapter.getAll();
    }

    public void setAccountSelectedListener(OnAccountSelectedListener listener) {
        mListener = listener;
    }

    public boolean isShowingAccountList() {
        return mIsShowingAccountList;
    }

    public void showAccountList() {
        if (!mIsShowingAccountList) {
            // cache menu adapter for later
            removeViewFromParent();
            mMenuAdapter = mNavigationMenuView.getAdapter();
            mNavigationMenuView.setAdapter(mAccountListAdapter);
        }
        mIsShowingAccountList = !mIsShowingAccountList;
    }

    public void hideAccountList() {
        if (mIsShowingAccountList) {
            // reset previously cached menu adapter
            mNavigationMenuView.setAdapter(mMenuAdapter);
            mMenuAdapter = null;
            addViewToParent();
        }
        mIsShowingAccountList = !mIsShowingAccountList;
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0 && mNavigationMenuView == null)
            mNavigationMenuView = (NavigationMenuView) getParent().getParent();
    }

    private void switchPrimaryAccount(Account newPrimaryAccount) {
        mAccountListAdapter.remove(newPrimaryAccount);
        mAccountListAdapter.insert(newPrimaryAccount, 0);
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
            Account primaryAccount = mAccountListAdapter.get(0);
            primaryAccount.applyAccountIcon(mPrimaryIconView);
            primaryAccount.applyAccountName(mNameView);
            primaryAccount.applyAccountEmail(mEmailView);
            mPrimaryIconView.setVisibility(VISIBLE);
            mNameView.setVisibility(VISIBLE);
            mEmailView.setVisibility(VISIBLE);
        }
        // first secondary account
        if (accountCount <= 1) {
            mSecondaryFirstIconView.setVisibility(GONE);
        } else {
            mAccountListAdapter.get(1).applyAccountIcon(mSecondaryFirstIconView);
            mSecondaryFirstIconView.setVisibility(VISIBLE);
        }
        // second secondary account
        if (accountCount <= 2) {
            mSecondarySecondIconView.setVisibility(GONE);
        } else {
            mAccountListAdapter.get(2).applyAccountIcon(mSecondarySecondIconView);
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
         * @param account The selected item
         */
        void onAccountSelected(Account account);

        /**
         * Called when the "Add account" item is selected.
         */
        void onAccountAddSelected();

        /**
         * Called when the "Manage accounts" item is selected.
         */
        void onAccountManageSelected();
    }
}
