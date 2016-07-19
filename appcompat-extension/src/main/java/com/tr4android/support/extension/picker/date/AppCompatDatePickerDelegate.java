/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.tr4android.support.extension.picker.date;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.tr4android.appcompat.extension.R;
import com.tr4android.support.extension.picker.DateFormatUtils;
import com.tr4android.support.extension.utils.ViewCompatUtils;
import com.tr4android.support.extension.picker.PickerThemeUtils;
import com.tr4android.support.extension.picker.date.DayPickerView.OnDaySelectedListener;
import com.tr4android.support.extension.picker.date.YearPickerView.OnYearSelectedListener;
import com.tr4android.support.extension.utils.ThemeUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A delegate for picking up a date (day / month / year).
 */
class AppCompatDatePickerDelegate extends AppCompatDatePicker.AbstractDatePickerDelegate {
    private static final int USE_LOCALE = 0;

    private static final int UNINITIALIZED = -1;
    private static final int VIEW_MONTH_DAY = 0;
    private static final int VIEW_YEAR = 1;

    private static final int DEFAULT_START_YEAR = 1900;
    private static final int DEFAULT_END_YEAR = 2100;

    private SimpleDateFormat mYearFormat;
    private SimpleDateFormat mMonthDayFormat;

    // Top-level container.
    private ViewGroup mContainer;

    // Header views.
    private TextView mHeaderYear;
    private TextView mHeaderMonthDay;

    // Picker views.
    private ViewAnimator mAnimator;
    private DayPickerView mDayPickerView;
    private YearPickerView mYearPickerView;

    // Accessibility strings.
    private String mSelectDay;
    private String mSelectYear;

    private AppCompatDatePicker.OnDateChangedListener mDateChangedListener;

    private int mCurrentView = UNINITIALIZED;

    private final Calendar mCurrentDate;
    private final Calendar mTempDate;
    private final Calendar mMinDate;
    private final Calendar mMaxDate;

    private int mFirstDayOfWeek = USE_LOCALE;

    public AppCompatDatePickerDelegate(AppCompatDatePicker delegator, Context context, AttributeSet attrs,
                                       int defStyleAttr, int defStyleRes) {
        super(delegator, context);

        final Locale locale = mCurrentLocale;
        mCurrentDate = Calendar.getInstance(locale);
        mTempDate = Calendar.getInstance(locale);
        mMinDate = Calendar.getInstance(locale);
        mMaxDate = Calendar.getInstance(locale);

        mMinDate.set(DEFAULT_START_YEAR, Calendar.JANUARY, 1);
        mMaxDate.set(DEFAULT_END_YEAR, Calendar.DECEMBER, 31);

        final Resources res = mDelegator.getResources();
        final TypedArray a = mContext.obtainStyledAttributes(attrs,
                R.styleable.DatePickerDialog, defStyleAttr, defStyleRes);
        final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        // Set up and attach container.
        mContainer = (ViewGroup) inflater.inflate(R.layout.date_picker_material, mDelegator, false);
        mDelegator.addView(mContainer);

        // Set up header views.
        final ViewGroup header = (ViewGroup) mContainer.findViewById(R.id.date_picker_header);
        mHeaderYear = (TextView) header.findViewById(R.id.date_picker_header_year);
        mHeaderYear.setOnClickListener(mOnHeaderClickListener);
        mHeaderMonthDay = (TextView) header.findViewById(R.id.date_picker_header_date);
        mHeaderMonthDay.setOnClickListener(mOnHeaderClickListener);

        // Set up header text color, if available.
        ColorStateList headerTextColor;
        if (a.hasValue(R.styleable.DatePickerDialog_headerTextColor)) {
            headerTextColor = a.getColorStateList(R.styleable.DatePickerDialog_headerTextColor);
        } else {
            headerTextColor = PickerThemeUtils.getHeaderTextColorStateList(mContext);
        }
        mHeaderYear.setTextColor(headerTextColor);
        mHeaderMonthDay.setTextColor(headerTextColor);

        // Set up header background, if available.
        ViewCompatUtils.setBackground(header, PickerThemeUtils.getHeaderBackground(mContext,
                a.getColor(R.styleable.DatePickerDialog_headerBackground,
                        ThemeUtils.getThemeAttrColor(mContext, R.attr.colorAccent))));

        a.recycle();

        // Set up picker container.
        mAnimator = (ViewAnimator) mContainer.findViewById(R.id.animator);

        // Set up day picker view.
        mDayPickerView = (DayPickerView) mAnimator.findViewById(R.id.date_picker_day_picker);
        mDayPickerView.setFirstDayOfWeek(mFirstDayOfWeek);
        mDayPickerView.setMinDate(mMinDate.getTimeInMillis());
        mDayPickerView.setMaxDate(mMaxDate.getTimeInMillis());
        mDayPickerView.setDate(mCurrentDate.getTimeInMillis());
        mDayPickerView.setOnDaySelectedListener(mOnDaySelectedListener);

        // Set up year picker view.
        mYearPickerView = (YearPickerView) mAnimator.findViewById(R.id.date_picker_year_picker);
        mYearPickerView.setRange(mMinDate, mMaxDate);
        mYearPickerView.setDate(mCurrentDate.getTimeInMillis());
        mYearPickerView.setOnYearSelectedListener(mOnYearSelectedListener);

        // Set up content descriptions.
        mSelectDay = res.getString(R.string.select_day);
        mSelectYear = res.getString(R.string.select_year);

        // Initialize for current locale. This also initializes the date, so no
        // need to call onDateChanged.
        onLocaleChanged(mCurrentLocale);

        setCurrentView(VIEW_MONTH_DAY);
    }

    /**
     * Listener called when the user selects a day in the day picker view.
     */
    private final OnDaySelectedListener mOnDaySelectedListener = new OnDaySelectedListener() {
        @Override
        public void onDaySelected(DayPickerView view, Calendar day) {
            mCurrentDate.setTimeInMillis(day.getTimeInMillis());
            onDateChanged(true, true);
        }
    };

    /**
     * Listener called when the user selects a year in the year picker view.
     */
    private final OnYearSelectedListener mOnYearSelectedListener = new OnYearSelectedListener() {
        @Override
        public void onYearChanged(YearPickerView view, int year) {
            // If the newly selected month / year does not contain the
            // currently selected day number, change the selected day number
            // to the last day of the selected month or year.
            // e.g. Switching from Mar to Apr when Mar 31 is selected -> Apr 30
            // e.g. Switching from 2012 to 2013 when Feb 29, 2012 is selected -> Feb 28, 2013
            final int day = mCurrentDate.get(Calendar.DAY_OF_MONTH);
            final int month = mCurrentDate.get(Calendar.MONTH);
            final int daysInMonth = getDaysInMonth(month, year);
            if (day > daysInMonth) {
                mCurrentDate.set(Calendar.DAY_OF_MONTH, daysInMonth);
            }

            mCurrentDate.set(Calendar.YEAR, year);
            onDateChanged(true, true);

            // Automatically switch to day picker.
            setCurrentView(VIEW_MONTH_DAY);
        }
    };

    /**
     * Listener called when the user clicks on a header item.
     */
    private final OnClickListener mOnHeaderClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            tryVibrate();

            int i = v.getId();
            if (i == R.id.date_picker_header_year) {
                setCurrentView(VIEW_YEAR);
            } else if (i == R.id.date_picker_header_date) {
                setCurrentView(VIEW_MONTH_DAY);
            }
        }
    };

    @Override
    protected void onLocaleChanged(Locale locale) {
        final TextView headerYear = mHeaderYear;
        if (headerYear == null) {
            // Abort, we haven't initialized yet. This method will get called
            // again later after everything has been set up.
            return;
        }

        // Update the date formatter.
        final String datePattern = DateFormatUtils.getBestDateTimePattern(locale, "EMMMd");
        mMonthDayFormat = new SimpleDateFormat(datePattern, locale);
        mYearFormat = new SimpleDateFormat("y", locale);

        // Update the header text.
        onCurrentDateChanged(false);
    }

    private void onCurrentDateChanged(boolean announce) {
        if (mHeaderYear == null) {
            // Abort, we haven't initialized yet. This method will get called
            // again later after everything has been set up.
            return;
        }

        final String year = mYearFormat.format(mCurrentDate.getTime());
        mHeaderYear.setText(year);

        final String monthDay = DateFormatUtils.format(mMonthDayFormat, mCurrentDate);
        mHeaderMonthDay.setText(monthDay);

        // TODO: This should use live regions.
        if (announce) {
            final long millis = mCurrentDate.getTimeInMillis();
            final int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR;
            final String fullDateText = DateUtils.formatDateTime(mContext, millis, flags);
            ViewCompatUtils.announceForAccessibility(mAnimator, fullDateText);
        }
    }

    private void setCurrentView(final int viewIndex) {
        switch (viewIndex) {
            case VIEW_MONTH_DAY:
                mDayPickerView.setDate(mCurrentDate.getTimeInMillis());

                if (mCurrentView != viewIndex) {
                    mHeaderMonthDay.setSelected(true);
                    mHeaderYear.setSelected(false);
                    mAnimator.setDisplayedChild(VIEW_MONTH_DAY);
                    mCurrentView = viewIndex;
                }

                ViewCompatUtils.announceForAccessibility(mAnimator, mSelectDay);
                break;
            case VIEW_YEAR:
                mYearPickerView.setDate(mCurrentDate.getTimeInMillis());

                if (mCurrentView != viewIndex) {
                    mHeaderMonthDay.setSelected(false);
                    mHeaderYear.setSelected(true);
                    mAnimator.setDisplayedChild(VIEW_YEAR);
                    mCurrentView = viewIndex;
                }

                ViewCompatUtils.announceForAccessibility(mAnimator, mSelectYear);
                break;
        }
    }

    @Override
    public void init(int year, int monthOfYear, int dayOfMonth,
                     AppCompatDatePicker.OnDateChangedListener callBack) {
        mCurrentDate.set(Calendar.YEAR, year);
        mCurrentDate.set(Calendar.MONTH, monthOfYear);
        mCurrentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        mDateChangedListener = callBack;

        onDateChanged(false, false);
    }

    @Override
    public void updateDate(int year, int month, int dayOfMonth) {
        mCurrentDate.set(Calendar.YEAR, year);
        mCurrentDate.set(Calendar.MONTH, month);
        mCurrentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        onDateChanged(false, true);
    }

    private void onDateChanged(boolean fromUser, boolean callbackToClient) {
        final int year = mCurrentDate.get(Calendar.YEAR);

        if (callbackToClient && mDateChangedListener != null) {
            final int monthOfYear = mCurrentDate.get(Calendar.MONTH);
            final int dayOfMonth = mCurrentDate.get(Calendar.DAY_OF_MONTH);
            mDateChangedListener.onDateChanged(mDelegator, year, monthOfYear, dayOfMonth);
        }

        mDayPickerView.setDate(mCurrentDate.getTimeInMillis());
        mYearPickerView.setYear(year);

        onCurrentDateChanged(fromUser);

        if (fromUser) {
            tryVibrate();
        }
    }

    @Override
    public int getYear() {
        return mCurrentDate.get(Calendar.YEAR);
    }

    @Override
    public int getMonth() {
        return mCurrentDate.get(Calendar.MONTH);
    }

    @Override
    public int getDayOfMonth() {
        return mCurrentDate.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void setMinDate(long minDate) {
        mTempDate.setTimeInMillis(minDate);
        if (mTempDate.get(Calendar.YEAR) == mMinDate.get(Calendar.YEAR)
                && mTempDate.get(Calendar.DAY_OF_YEAR) != mMinDate.get(Calendar.DAY_OF_YEAR)) {
            return;
        }
        if (mCurrentDate.before(mTempDate)) {
            mCurrentDate.setTimeInMillis(minDate);
            onDateChanged(false, true);
        }
        mMinDate.setTimeInMillis(minDate);
        mDayPickerView.setMinDate(minDate);
        mYearPickerView.setRange(mMinDate, mMaxDate);
    }

    @Override
    public Calendar getMinDate() {
        return mMinDate;
    }

    @Override
    public void setMaxDate(long maxDate) {
        mTempDate.setTimeInMillis(maxDate);
        if (mTempDate.get(Calendar.YEAR) == mMaxDate.get(Calendar.YEAR)
                && mTempDate.get(Calendar.DAY_OF_YEAR) != mMaxDate.get(Calendar.DAY_OF_YEAR)) {
            return;
        }
        if (mCurrentDate.after(mTempDate)) {
            mCurrentDate.setTimeInMillis(maxDate);
            onDateChanged(false, true);
        }
        mMaxDate.setTimeInMillis(maxDate);
        mDayPickerView.setMaxDate(maxDate);
        mYearPickerView.setRange(mMinDate, mMaxDate);
    }

    @Override
    public Calendar getMaxDate() {
        return mMaxDate;
    }

    @Override
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        mFirstDayOfWeek = firstDayOfWeek;

        mDayPickerView.setFirstDayOfWeek(firstDayOfWeek);
    }

    @Override
    public int getFirstDayOfWeek() {
        if (mFirstDayOfWeek != USE_LOCALE) {
            return mFirstDayOfWeek;
        }
        return mCurrentDate.getFirstDayOfWeek();
    }

    @Override
    public void setEnabled(boolean enabled) {
        mContainer.setEnabled(enabled);
        mDayPickerView.setEnabled(enabled);
        mYearPickerView.setEnabled(enabled);
        mHeaderYear.setEnabled(enabled);
        mHeaderMonthDay.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return mContainer.isEnabled();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        setCurrentLocale(newConfig.locale);
    }

    @Override
    public Parcelable onSaveInstanceState(Parcelable superState) {
        final int year = mCurrentDate.get(Calendar.YEAR);
        final int month = mCurrentDate.get(Calendar.MONTH);
        final int day = mCurrentDate.get(Calendar.DAY_OF_MONTH);

        int listPosition = -1;
        int listPositionOffset = -1;

        if (mCurrentView == VIEW_MONTH_DAY) {
            listPosition = mDayPickerView.getMostVisiblePosition();
        } else if (mCurrentView == VIEW_YEAR) {
            listPosition = mYearPickerView.getFirstVisiblePosition();
            listPositionOffset = mYearPickerView.getFirstPositionOffset();
        }

        return new SavedState(superState, year, month, day, mMinDate.getTimeInMillis(),
                mMaxDate.getTimeInMillis(), mCurrentView, listPosition, listPositionOffset);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        final SavedState ss = (SavedState) state;

        // TODO: Move instance state into DayPickerView, YearPickerView.
        mCurrentDate.set(ss.getSelectedYear(), ss.getSelectedMonth(), ss.getSelectedDay());
        mMinDate.setTimeInMillis(ss.getMinDate());
        mMaxDate.setTimeInMillis(ss.getMaxDate());

        onCurrentDateChanged(false);

        final int currentView = ss.getCurrentView();
        setCurrentView(currentView);

        final int listPosition = ss.getListPosition();
        if (listPosition != -1) {
            if (currentView == VIEW_MONTH_DAY) {
                mDayPickerView.setPosition(listPosition);
            } else if (currentView == VIEW_YEAR) {
                final int listPositionOffset = ss.getListPositionOffset();
                mYearPickerView.setSelectionFromTop(listPosition, listPositionOffset);
            }
        }
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        onPopulateAccessibilityEvent(event);
        return true;
    }

    @Override
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        event.getText().add(mCurrentDate.getTime().toString());
    }

    public CharSequence getAccessibilityClassName() {
        return AppCompatDatePicker.class.getName();
    }

    public static int getDaysInMonth(int month, int year) {
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.MARCH:
            case Calendar.MAY:
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.OCTOBER:
            case Calendar.DECEMBER:
                return 31;
            case Calendar.APRIL:
            case Calendar.JUNE:
            case Calendar.SEPTEMBER:
            case Calendar.NOVEMBER:
                return 30;
            case Calendar.FEBRUARY:
                return (year % 4 == 0) ? 29 : 28;
            default:
                throw new IllegalArgumentException("Invalid Month");
        }
    }

    private void tryVibrate() {
        mDelegator.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    }

    /**
     * Class for managing state storing/restoring.
     */
    private static class SavedState extends View.BaseSavedState {
        private final int mSelectedYear;
        private final int mSelectedMonth;
        private final int mSelectedDay;
        private final long mMinDate;
        private final long mMaxDate;
        private final int mCurrentView;
        private final int mListPosition;
        private final int mListPositionOffset;

        /**
         * Constructor called from {@link AppCompatDatePicker#onSaveInstanceState()}
         */
        private SavedState(Parcelable superState, int year, int month, int day,
                           long minDate, long maxDate, int currentView, int listPosition,
                           int listPositionOffset) {
            super(superState);
            mSelectedYear = year;
            mSelectedMonth = month;
            mSelectedDay = day;
            mMinDate = minDate;
            mMaxDate = maxDate;
            mCurrentView = currentView;
            mListPosition = listPosition;
            mListPositionOffset = listPositionOffset;
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            mSelectedYear = in.readInt();
            mSelectedMonth = in.readInt();
            mSelectedDay = in.readInt();
            mMinDate = in.readLong();
            mMaxDate = in.readLong();
            mCurrentView = in.readInt();
            mListPosition = in.readInt();
            mListPositionOffset = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mSelectedYear);
            dest.writeInt(mSelectedMonth);
            dest.writeInt(mSelectedDay);
            dest.writeLong(mMinDate);
            dest.writeLong(mMaxDate);
            dest.writeInt(mCurrentView);
            dest.writeInt(mListPosition);
            dest.writeInt(mListPositionOffset);
        }

        public int getSelectedDay() {
            return mSelectedDay;
        }

        public int getSelectedMonth() {
            return mSelectedMonth;
        }

        public int getSelectedYear() {
            return mSelectedYear;
        }

        public long getMinDate() {
            return mMinDate;
        }

        public long getMaxDate() {
            return mMaxDate;
        }

        public int getCurrentView() {
            return mCurrentView;
        }

        public int getListPosition() {
            return mListPosition;
        }

        public int getListPositionOffset() {
            return mListPositionOffset;
        }

        @SuppressWarnings("all")
        // suppress unused and hiding
        public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
