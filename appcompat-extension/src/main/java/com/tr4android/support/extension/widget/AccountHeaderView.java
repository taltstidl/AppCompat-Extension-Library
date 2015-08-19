package com.tr4android.support.extension.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.design.internal.NavigationMenuView;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tr4android.appcompat.extension.R;
import com.tr4android.support.extension.internal.Account;
import com.tr4android.support.extension.internal.AccountAdapter;

import java.util.ArrayList;

/**
 * An account header layout build specifically for the AppCompat Design Library NavigationView
 */
public class AccountHeaderView extends RelativeLayout {
    private static final String LOG_TAG = "AccountHeaderView";

    // The ListView subclass used in the AppCompat Design Library NavigationView
    // Note: this is prone to changes in the official support library
    private NavigationMenuView mNavigationMenuView;

    // The menu adapter used by the NavigationView
    private ListAdapter mMenuAdapter;
    
    // The click listener used by the NavigationView
    private AdapterView.OnItemClickListener mMenuClickListener;

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
                mNavigationMenuView.setOnItemClickListener(mMenuClickListener);
                mMenuAdapter = null;
                mMenuClickListener = null;
            } else {
                // cache menu adapter for later
                mMenuAdapter = ((HeaderViewListAdapter) mNavigationMenuView.getAdapter()).getWrappedAdapter();
                mMenuClickListener = mNavigationMenuView.getOnItemClickListener();
                mNavigationMenuView.setAdapter(mAccountListAdapter);
                mNavigationMenuView.setOnItemClickListener(mAccountItemClickListener);
            }
            mIsShowingAccountList = !mIsShowingAccountList;
        }
    };

    // The item click listener for the account list items
    private AdapterView.OnItemClickListener mAccountItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) return; // disable header click
            hideAccountList();
            int count = mAccountListAdapter.getCount();
            if (position < mAccountListAdapter.getAccountCount()) {
                Account selectedAccount = mAccountListAdapter.getItem(position);
                switchPrimaryAccount(selectedAccount);
                if (mListener != null) mListener.onAccountSelected(selectedAccount);
            } else if (position == count && mAccountListAdapter.isAccountManageEnabled()) {
                if (mListener != null) mListener.onAccountManageSelected();
            } else {
                if (mListener != null) mListener.onAccountAddSelected();
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

    public AccountHeaderView(Context context) {
        this(context, null);
    }

    public AccountHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AccountHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(new ContextThemeWrapper(context, R.style.AccountHeaderView), attrs, defStyleAttr);

        // Retrieve the style attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AccountHeaderView, defStyleAttr, 0);
        boolean mAddAccountEnabled = a.getBoolean(R.styleable.AccountHeaderView_accountHeaderAddEnabled, true);
        boolean mManageAccountEnabled = a.getBoolean(R.styleable.AccountHeaderView_accountHeaderManageEnabled, true);
        a.recycle();

        // Inflate the layout
        LayoutInflater.from(context).inflate(R.layout.appcompat_extension_account_header, this, true);

        // Create an empty adapter (for "Add account" and "Manage accounts" entries)
        mAccountListAdapter = new AccountAdapter(context, mAddAccountEnabled, mManageAccountEnabled);

        // Retrieve and setup the CircleImageViews used
        mPrimaryIconView = (CircleImageView) findViewById(R.id.account_header_icon_primary);
        mSecondaryFirstIconView = (CircleImageView) findViewById(R.id.account_header_icon_secondary_first);
        mSecondaryFirstIconView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPrimaryAccount(mAccountListAdapter.getItem(1));
            }
        });
        mSecondarySecondIconView = (CircleImageView) findViewById(R.id.account_header_icon_secondary_second);
        mSecondarySecondIconView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPrimaryAccount(mAccountListAdapter.getItem(2));
            }
        });

        // Retrieve the TextViews used
        mNameView = (TextView) findViewById(R.id.account_header_text_name);
        mEmailView = (TextView) findViewById(R.id.account_header_text_email);

        findViewById(R.id.account_header_dropdown).setOnClickListener(mShowAccountClickListener);
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
        ArrayList<Account> allAccounts = new ArrayList<>();
        int count = mAccountListAdapter.getAccountCount();
        for (int i = 0; i < count; i++) {
            allAccounts.add(mAccountListAdapter.getItem(i));
        }
        return allAccounts;
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
            mMenuAdapter = ((HeaderViewListAdapter) mNavigationMenuView.getAdapter()).getWrappedAdapter();
            mMenuClickListener = mNavigationMenuView.getOnItemClickListener();
            mNavigationMenuView.setAdapter(mAccountListAdapter);
            mNavigationMenuView.setOnItemClickListener(mAccountItemClickListener);
        }
        mIsShowingAccountList = !mIsShowingAccountList;
    }

    public void hideAccountList() {
        if (mIsShowingAccountList) {
            // reset previously cached menu adapter
            mNavigationMenuView.setAdapter(mMenuAdapter);
            mNavigationMenuView.setOnItemClickListener(mMenuClickListener);
            mMenuAdapter = null;
            mMenuClickListener = null;
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
            Account primaryAccount = mAccountListAdapter.getItem(0);
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
            mAccountListAdapter.getItem(1).applyAccountIcon(mSecondaryFirstIconView);
            mSecondaryFirstIconView.setVisibility(VISIBLE);
        }
        // second secondary account
        if (accountCount <= 2) {
            mSecondarySecondIconView.setVisibility(GONE);
        } else {
            mAccountListAdapter.getItem(2).applyAccountIcon(mSecondarySecondIconView);
            mSecondarySecondIconView.setVisibility(VISIBLE);
        }
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
        public boolean onAccountSelected(Account account);

        /**
         * Called when the "Add account" item is selected.
         */
        public boolean onAccountAddSelected();

        /**
         * Called when the "Manage accounts" item is selected.
         */
        public boolean onAccountManageSelected();
    }
}
