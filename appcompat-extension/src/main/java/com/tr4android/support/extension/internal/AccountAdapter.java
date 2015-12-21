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

package com.tr4android.support.extension.internal;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tr4android.appcompat.extension.R;
import com.tr4android.support.extension.widget.AccountHeaderView;
import com.tr4android.support.extension.widget.CircleImageView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Adapter for accounts
 */
public class AccountAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ACCOUNT = 0;
    private static final int VIEW_TYPE_HEADER = 1;

    private ArrayList<Account> mAccounts;
    private AccountHeaderView mHeader;

    private boolean mShowAccountAdd;
    private boolean mShowAccountManage;

    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mHeader.handleAccountClick((Integer) view.getTag());
        }
    };

    public AccountAdapter(ArrayList<Account> accounts, AccountHeaderView header, boolean showAccountAdd, boolean showAccountManage) {
        mAccounts = accounts;
        mHeader = header;
        mShowAccountAdd = showAccountAdd;
        mShowAccountManage = showAccountManage;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ACCOUNT:
                return new AccountViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.appcompat_extension_account_list_item, parent, false), mClickListener);
            case VIEW_TYPE_HEADER:
                return new HeaderViewHolder(mHeader);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AccountViewHolder) {
            AccountViewHolder accountViewHolder = (AccountViewHolder) holder;
            holder.itemView.setTag(position);
            if (position < getAccountCount()) {
                Account account = mAccounts.get(position);
                // apply the account to the list item
                account.applyAccountIcon(accountViewHolder.iconView);
                account.applyAccountEmail(accountViewHolder.nameView);
            } else if (position == getItemCount() - 1 && mShowAccountManage) {
                // Manage accounts item
                accountViewHolder.iconView.setCircleImageEnabled(false);
                accountViewHolder.iconView.setImageResource(R.drawable.ic_settings_grey600_24dp);
                accountViewHolder.nameView.setText(R.string.account_header_list_item_manage_accounts);
            } else {
                // Add account item
                accountViewHolder.iconView.setCircleImageEnabled(false);
                accountViewHolder.iconView.setImageResource(R.drawable.ic_add_grey600_24dp);
                accountViewHolder.nameView.setText(R.string.account_header_list_item_add_account);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mAccounts.size() + (mShowAccountAdd ? 1 : 0) + (mShowAccountManage ? 1 : 0);
    }


    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_HEADER : VIEW_TYPE_ACCOUNT;
    }


    private static class AccountViewHolder extends RecyclerView.ViewHolder {
        CircleImageView iconView;
        TextView nameView;

        public AccountViewHolder(View itemView, View.OnClickListener listener) {
            super(itemView);
            iconView = (CircleImageView) itemView.findViewById(R.id.icon);
            nameView = (TextView) itemView.findViewById(R.id.name);
            itemView.setOnClickListener(listener);
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public int getAccountCount() {
        return mAccounts.size();
    }

    public boolean isAccountAddEnabled() {
        return mShowAccountAdd;
    }

    public boolean isAccountManageEnabled() {
        return mShowAccountManage;
    }

    // Functions for managing the accounts
    public void add(Account account) {
        mAccounts.add(account);
    }

    public void addAll(Account... accounts) {
        mAccounts.addAll(Arrays.asList(accounts));
    }

    public void remove(Account account) {
        mAccounts.remove(account);
    }

    public void insert(Account account, int position) {
        mAccounts.add(position, account);
    }

    public void clear() {
        mAccounts.clear();
    }

    public Account get(int position) {
        return mAccounts.get(position);
    }

    public ArrayList<Account> getAll() {
        return mAccounts;
    }
}
