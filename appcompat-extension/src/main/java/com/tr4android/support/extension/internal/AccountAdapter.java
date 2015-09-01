package com.tr4android.support.extension.internal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tr4android.appcompat.extension.R;
import com.tr4android.support.extension.widget.CircleImageView;

import java.util.ArrayList;

/**
 * Adapter for accounts
 */
public class AccountAdapter extends ArrayAdapter<Account> {
    private boolean mShowAccountAdd;
    private boolean mShowAccountManage;


    private static class AccountViewHolder {
        CircleImageView iconView;
        TextView nameView;
    }

    public AccountAdapter(Context context, boolean showAccountAdd, boolean showAccountManage) {
        super(context, R.layout.appcompat_extension_account_list_item, new ArrayList<Account>());
        mShowAccountAdd = showAccountAdd;
        mShowAccountManage = showAccountManage;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AccountViewHolder accountViewHolder;
        if (convertView == null) {
            accountViewHolder = new AccountViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.appcompat_extension_account_list_item, parent, false);
            accountViewHolder.iconView = (CircleImageView) convertView.findViewById(R.id.icon);
            accountViewHolder.nameView = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(accountViewHolder);
        } else {
            accountViewHolder = (AccountViewHolder) convertView.getTag();
        }

        position ++; // Skips the first account
        if (position < getAccountCount()) {
            Account account = getItem(position);
            // apply the account to the list item
            account.applyAccountIcon(accountViewHolder.iconView);
            account.applyAccountEmail(accountViewHolder.nameView);
        } else if (position == getCount() && mShowAccountManage) {
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

        return convertView;
    }

    @Override
    public int getCount() {
        // Additional items for "Add account" and "Manage account"
        return super.getCount() - 1 + (mShowAccountAdd ? 1 : 0) + (mShowAccountManage ? 1 : 0);
    }

    public int getAccountCount() {
        return super.getCount();
    }

    public boolean isAccountAddEnabled() {
        return mShowAccountAdd;
    }

    public boolean isAccountManageEnabled() {
        return mShowAccountManage;
    }
}
