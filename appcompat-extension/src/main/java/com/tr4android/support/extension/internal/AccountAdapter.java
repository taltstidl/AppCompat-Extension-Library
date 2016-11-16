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

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tr4android.appcompat.extension.R;
import com.tr4android.support.extension.utils.ThemeUtils;
import com.tr4android.support.extension.utils.ViewCompatUtils;
import com.tr4android.support.extension.widget.AccountHeaderView;
import com.tr4android.support.extension.widget.CircleImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Adapter for accounts
 */
public class AccountAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ACCOUNT = 0;
    private static final int VIEW_TYPE_HEADER = 1;

    private List<IAccount> mAccounts;
    private AccountHeaderView mHeader;

    private boolean mShowAccountAdd;
    private boolean mShowAccountManage;
    private boolean mShowCheckBoxes;

    private final View.OnClickListener mAccountClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mHeader.handleAccountClick((Integer) view.getTag());
        }
    };

    private final CompoundButton.OnCheckedChangeListener mAccountCheckListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton view, boolean isChecked) {
            int position = (Integer) ((View) view.getParent()).getTag();
            mAccounts.get(position).setChecked(isChecked);
            mHeader.handleAccountCheck(position, isChecked);
        }
    };

    public AccountAdapter(ArrayList<IAccount> accounts, AccountHeaderView header, boolean showAccountAdd, boolean showAccountManage, boolean showCheckBoxes) {
        mAccounts = accounts;
        mHeader = header;
        mShowAccountAdd = showAccountAdd;
        mShowAccountManage = showAccountManage;
        mShowCheckBoxes = showCheckBoxes;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ACCOUNT:
                AccountViewHolder holder = new AccountViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.appcompat_extension_account_list_item, parent, false));
                holder.itemView.setOnClickListener(mAccountClickListener);
                return holder;
            case VIEW_TYPE_HEADER:
                return new HeaderViewHolder(mHeader);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AccountViewHolder) {
            final AccountViewHolder accountViewHolder = (AccountViewHolder) holder;
            holder.itemView.setTag(position);
            if (position < getAccountCount()) {
                IAccount account = mAccounts.get(position);
                if (mShowCheckBoxes) {
                    accountViewHolder.checkView.setOnCheckedChangeListener(null);
                    accountViewHolder.checkView.setChecked(account.isChecked());
                    accountViewHolder.checkView.setOnCheckedChangeListener(mAccountCheckListener);
                    accountViewHolder.checkView.setVisibility(View.VISIBLE);
                    accountViewHolder.iconView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            accountViewHolder.checkView.toggle();
                        }
                    });
                }
                // apply the account to the list item
                AccountUtils.applyAccountIcon(account, accountViewHolder.iconView);
                AccountUtils.applyAccountEmail(account, accountViewHolder.nameView);
                AccountUtils.applyAccountInfo(account, accountViewHolder.infoLayout,
                        accountViewHolder.infoIconView, accountViewHolder.infoTextView);
            } else if (position == getItemCount() - 1 && mShowAccountManage) {
                // Manage accounts item
                accountViewHolder.iconView.setCircleImageEnabled(false);
                accountViewHolder.checkView.setVisibility(View.GONE);
                accountViewHolder.iconView.setImageResource(R.drawable.ic_settings_black_24dp);
                accountViewHolder.nameView.setText(R.string.account_header_list_item_manage_accounts);
                accountViewHolder.infoLayout.setVisibility(View.GONE);
            } else {
                // Add account item
                accountViewHolder.iconView.setCircleImageEnabled(false);
                accountViewHolder.checkView.setVisibility(View.GONE);
                accountViewHolder.iconView.setImageResource(R.drawable.ic_add_black_24dp);
                accountViewHolder.nameView.setText(R.string.account_header_list_item_add_account);
                accountViewHolder.infoLayout.setVisibility(View.GONE);
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
        CheckBox checkView;
        TextView nameView;
        LinearLayout infoLayout;
        ImageView infoIconView;
        TextView infoTextView;

        public AccountViewHolder(View itemView) {
            super(itemView);
            iconView = (CircleImageView) itemView.findViewById(R.id.icon);
            checkView = (CheckBox) itemView.findViewById(R.id.checkbox);
            nameView = (TextView) itemView.findViewById(R.id.name);
            infoLayout = (LinearLayout) itemView.findViewById(R.id.info_layout);
            infoIconView = (ImageView) itemView.findViewById(R.id.info_icon);
            infoTextView = (TextView) itemView.findViewById(R.id.info_text);
            setupCheckBox(checkView);
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    // Function for styling the CheckBox for all Android version
    private static void setupCheckBox(CheckBox checkBox) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkBox.setButtonDrawable(R.drawable.btn_checkbox_circle);
            checkBox.setBackgroundResource(R.drawable.btn_checkbox_circle_background);
        } else {
            Context context = checkBox.getContext();
            AppCompatDrawableManager dm = AppCompatDrawableManager.get();

            StateListDrawable button = new StateListDrawable();
            button.addState(new int[]{android.R.attr.state_checked},
                    dm.getDrawable(context, R.drawable.ic_checkbox_circle_checked));
            button.addState(new int[]{},
                    dm.getDrawable(context, R.drawable.ic_checkbox_circle_unchecked));
            ColorStateList buttonTint = new ColorStateList(new int[][]{ // states
                    new int[]{android.R.attr.state_checked},
                    new int[]{} // state_default
            }, new int[]{ // colors
                    ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated),
                    ThemeUtils.getThemeAttrColor(context, R.attr.colorControlNormal)
            });
            Drawable buttonCompat = DrawableCompat.wrap(button);
            DrawableCompat.setTintList(buttonCompat, buttonTint);
            checkBox.setButtonDrawable(buttonCompat);

            ShapeDrawable background = new ShapeDrawable(new OvalShape());
            int backgroundTint = ThemeUtils.getThemeAttrColor(context, android.R.attr.colorBackground);
            Drawable backgroundCompat = DrawableCompat.wrap(background);
            DrawableCompat.setTint(backgroundCompat, backgroundTint);
            ViewCompatUtils.setBackground(checkBox, backgroundCompat);
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
    public void add(IAccount account) {
        mAccounts.add(account);
    }

    public void addAll(IAccount... accounts) {
        mAccounts.addAll(Arrays.asList(accounts));
    }

    public void remove(IAccount account) {
        int index = indexOf(account);
        mAccounts.remove(index);
    }

    public void insert(IAccount account, int position) {
        mAccounts.add(position, account);
    }

    public void clear() {
        mAccounts.clear();
    }

    public void move(IAccount account, int position) {
        int index = indexOf(account);
        mAccounts.remove(index);
        mAccounts.add(position, account);
    }

    public int indexOf(IAccount account) {
        return mAccounts.indexOf(account);
    }

    public IAccount get(int position) {
        return mAccounts.get(position);
    }

    @Deprecated
    public List<IAccount> getAll() {
        return getAccounts();
    }

    public List<IAccount> getAccounts() {
        return mAccounts;
    }

    public void setAccounts(List<IAccount> accounts) {
        mAccounts = accounts;
    }

    public void setChecked(int position, boolean checked) {
        mAccounts.get(position).setChecked(checked);
    }

    public boolean isChecked(int position) {
        return position == 0 || mAccounts.get(position).isChecked();
    }

    public ArrayList<IAccount> getChecked() {
        int size = mAccounts.size();
        ArrayList<IAccount> checked = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            if (isChecked(i)) checked.add(mAccounts.get(i));
        }
        return checked;
    }
}
