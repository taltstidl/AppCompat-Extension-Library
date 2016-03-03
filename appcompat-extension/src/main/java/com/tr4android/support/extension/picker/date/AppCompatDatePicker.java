/*
 * Copyright (C) 2007 The Android Open Source Project
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
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;

import com.tr4android.appcompat.extension.R;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Provides a widget for selecting a date.
 * <p>
 * See the <a href="{@docRoot}guide/topics/ui/controls/pickers.html">Pickers</a>
 * guide.
 * </p>
 * <p>
 * For a dialog using this view, see {@link android.app.DatePickerDialog}.
 * </p>
 *
 * @attr ref android.R.styleable#DatePicker_startYear
 * @attr ref android.R.styleable#DatePicker_endYear
 * @attr ref android.R.styleable#DatePicker_maxDate
 * @attr ref android.R.styleable#DatePicker_minDate
 * @attr ref android.R.styleable#DatePicker_spinnersShown
 * @attr ref android.R.styleable#DatePicker_calendarViewShown
 * @attr ref android.R.styleable#DatePicker_dayOfWeekBackground
 * @attr ref android.R.styleable#DatePicker_dayOfWeekTextAppearance
 * @attr ref android.R.styleable#DatePicker_headerBackground
 * @attr ref android.R.styleable#DatePicker_headerMonthTextAppearance
 * @attr ref android.R.styleable#DatePicker_headerDayOfMonthTextAppearance
 * @attr ref android.R.styleable#DatePicker_headerYearTextAppearance
 * @attr ref android.R.styleable#DatePicker_yearListItemTextAppearance
 * @attr ref android.R.styleable#DatePicker_yearListSelectorColor
 * @attr ref android.R.styleable#DatePicker_calendarTextColor
 */

public class AppCompatDatePicker extends FrameLayout {
    private static final String LOG_TAG = AppCompatDatePicker.class.getSimpleName();

    private final DatePickerDelegate mDelegate;

    /**
     * The callback used to indicate the user changed the date.
     */
    public interface OnDateChangedListener {

        /**
         * Called upon a date change.
         *
         * @param view The view associated with this listener.
         * @param year The year that was set.
         * @param monthOfYear The month that was set (0-11) for compatibility
         *            with {@link java.util.Calendar}.
         * @param dayOfMonth The day of the month that was set.
         */
        void onDateChanged(AppCompatDatePicker view, int year, int monthOfYear, int dayOfMonth);
    }

    public AppCompatDatePicker(Context context) {
        this(context, null);
    }

    public AppCompatDatePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0); //TODO: look into R.attr.datePickerStyle
    }

    public AppCompatDatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DatePickerDialog,
                defStyleAttr, 0); //TODO: add default style res
        final int firstDayOfWeek = a.getInt(R.styleable.DatePickerDialog_firstDayOfWeek, 0);
        a.recycle();

        mDelegate = createCalendarUIDelegate(context, attrs, defStyleAttr, 0); //TODO: add default style res

        if (firstDayOfWeek != 0) {
            setFirstDayOfWeek(firstDayOfWeek);
        }
    }

    private DatePickerDelegate createCalendarUIDelegate(Context context, AttributeSet attrs,
                                                        int defStyleAttr, int defStyleRes) {
        return new AppCompatDatePickerDelegate(this, context, attrs, defStyleAttr,
                defStyleRes);
    }

    /**
     * Initialize the state. If the provided values designate an inconsistent
     * date the values are normalized before updating the spinners.
     *
     * @param year The initial year.
     * @param monthOfYear The initial month <strong>starting from zero</strong>.
     * @param dayOfMonth The initial day of the month.
     * @param onDateChangedListener How user is notified date is changed by
     *            user, can be null.
     */
    public void init(int year, int monthOfYear, int dayOfMonth,
                     OnDateChangedListener onDateChangedListener) {
        mDelegate.init(year, monthOfYear, dayOfMonth, onDateChangedListener);
    }

    /**
     * Update the current date.
     *
     * @param year The year.
     * @param month The month which is <strong>starting from zero</strong>.
     * @param dayOfMonth The day of the month.
     */
    public void updateDate(int year, int month, int dayOfMonth) {
        mDelegate.updateDate(year, month, dayOfMonth);
    }

    /**
     * @return The selected year.
     */
    public int getYear() {
        return mDelegate.getYear();
    }

    /**
     * @return The selected month.
     */
    public int getMonth() {
        return mDelegate.getMonth();
    }

    /**
     * @return The selected day of month.
     */
    public int getDayOfMonth() {
        return mDelegate.getDayOfMonth();
    }

    /**
     * Gets the minimal date supported by this {@link AppCompatDatePicker} in
     * milliseconds since January 1, 1970 00:00:00 in
     * {@link TimeZone#getDefault()} time zone.
     * <p>
     * Note: The default minimal date is 01/01/1900.
     * <p>
     *
     * @return The minimal supported date.
     */
    public long getMinDate() {
        return mDelegate.getMinDate().getTimeInMillis();
    }

    /**
     * Sets the minimal date supported by this {@link AppCompatDatePicker} in
     * milliseconds since January 1, 1970 00:00:00 in
     * {@link TimeZone#getDefault()} time zone.
     *
     * @param minDate The minimal supported date.
     */
    public void setMinDate(long minDate) {
        mDelegate.setMinDate(minDate);
    }

    /**
     * Gets the maximal date supported by this {@link AppCompatDatePicker} in
     * milliseconds since January 1, 1970 00:00:00 in
     * {@link TimeZone#getDefault()} time zone.
     * <p>
     * Note: The default maximal date is 12/31/2100.
     * <p>
     *
     * @return The maximal supported date.
     */
    public long getMaxDate() {
        return mDelegate.getMaxDate().getTimeInMillis();
    }

    /**
     * Sets the maximal date supported by this {@link AppCompatDatePicker} in
     * milliseconds since January 1, 1970 00:00:00 in
     * {@link TimeZone#getDefault()} time zone.
     *
     * @param maxDate The maximal supported date.
     */
    public void setMaxDate(long maxDate) {
        mDelegate.setMaxDate(maxDate);
    }

    /**
     * Sets the callback that indicates the current date is valid.
     *
     * @param callback the callback, may be null
     * @hide
     */
    public void setValidationCallback(@Nullable ValidationCallback callback) {
        mDelegate.setValidationCallback(callback);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mDelegate.isEnabled() == enabled) {
            return;
        }
        super.setEnabled(enabled);
        mDelegate.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return mDelegate.isEnabled();
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return mDelegate.dispatchPopulateAccessibilityEvent(event);
    }

    @Override
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(event);
        mDelegate.onPopulateAccessibilityEvent(event);
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return AppCompatDatePicker.class.getName();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDelegate.onConfigurationChanged(newConfig);
    }

    /**
     * Sets the first day of week.
     *
     * @param firstDayOfWeek The first day of the week conforming to the
     *            {@link DayPickerView} APIs.
     * @see Calendar#SUNDAY
     * @see Calendar#MONDAY
     * @see Calendar#TUESDAY
     * @see Calendar#WEDNESDAY
     * @see Calendar#THURSDAY
     * @see Calendar#FRIDAY
     * @see Calendar#SATURDAY
     *
     * @attr ref android.R.styleable#DatePicker_firstDayOfWeek
     */
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        if (firstDayOfWeek < Calendar.SUNDAY || firstDayOfWeek > Calendar.SATURDAY) {
            throw new IllegalArgumentException("firstDayOfWeek must be between 1 and 7");
        }
        mDelegate.setFirstDayOfWeek(firstDayOfWeek);
    }

    /**
     * Gets the first day of week.
     *
     * @return The first day of the week conforming to the {@link DayPickerView}
     *         APIs.
     * @see Calendar#SUNDAY
     * @see Calendar#MONDAY
     * @see Calendar#TUESDAY
     * @see Calendar#WEDNESDAY
     * @see Calendar#THURSDAY
     * @see Calendar#FRIDAY
     * @see Calendar#SATURDAY
     *
     * @attr ref android.R.styleable#DatePicker_firstDayOfWeek
     */
    public int getFirstDayOfWeek() {
        return mDelegate.getFirstDayOfWeek();
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return mDelegate.onSaveInstanceState(superState);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        BaseSavedState ss = (BaseSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mDelegate.onRestoreInstanceState(ss);
    }

    /**
     * A delegate interface that defined the public API of the DatePicker. Allows different
     * DatePicker implementations. This would need to be implemented by the DatePicker delegates
     * for the real behavior.
     *
     * @hide
     */
    interface DatePickerDelegate {
        void init(int year, int monthOfYear, int dayOfMonth,
                  OnDateChangedListener onDateChangedListener);

        void updateDate(int year, int month, int dayOfMonth);

        int getYear();
        int getMonth();
        int getDayOfMonth();

        void setFirstDayOfWeek(int firstDayOfWeek);
        int getFirstDayOfWeek();

        void setMinDate(long minDate);
        Calendar getMinDate();

        void setMaxDate(long maxDate);
        Calendar getMaxDate();

        void setEnabled(boolean enabled);
        boolean isEnabled();

        void setValidationCallback(ValidationCallback callback);

        void onConfigurationChanged(Configuration newConfig);

        Parcelable onSaveInstanceState(Parcelable superState);
        void onRestoreInstanceState(Parcelable state);

        boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event);
        void onPopulateAccessibilityEvent(AccessibilityEvent event);
    }

    /**
     * An abstract class which can be used as a start for DatePicker implementations
     */
    abstract static class AbstractDatePickerDelegate implements DatePickerDelegate {
        // The delegator
        protected AppCompatDatePicker mDelegator;

        // The context
        protected Context mContext;

        // The current locale
        protected Locale mCurrentLocale;

        // Callbacks
        protected OnDateChangedListener mOnDateChangedListener;
        protected ValidationCallback mValidationCallback;

        public AbstractDatePickerDelegate(AppCompatDatePicker delegator, Context context) {
            mDelegator = delegator;
            mContext = context;

            setCurrentLocale(Locale.getDefault());
        }

        protected void setCurrentLocale(Locale locale) {
            if (!locale.equals(mCurrentLocale)) {
                mCurrentLocale = locale;
                onLocaleChanged(locale);
            }
        }

        @Override
        public void setValidationCallback(ValidationCallback callback) {
            mValidationCallback = callback;
        }

        protected void onValidationChanged(boolean valid) {
            if (mValidationCallback != null) {
                mValidationCallback.onValidationChanged(valid);
            }
        }

        protected void onLocaleChanged(Locale locale) {
            // Stub.
        }
    }

    /**
     * A callback interface for updating input validity when the date picker
     * when included into a dialog.
     *
     * @hide
     */
    public static interface ValidationCallback {
        void onValidationChanged(boolean valid);
    }

    /**
     * Class for managing state storing/restoring.
     */
    private static class SavedState extends BaseSavedState {

        private final int mYear;

        private final int mMonth;

        private final int mDay;

        /**
         * Constructor called from {@link AppCompatDatePicker#onSaveInstanceState()}
         */
        private SavedState(Parcelable superState, int year, int month, int day) {
            super(superState);
            mYear = year;
            mMonth = month;
            mDay = day;
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            mYear = in.readInt();
            mMonth = in.readInt();
            mDay = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mYear);
            dest.writeInt(mMonth);
            dest.writeInt(mDay);
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
