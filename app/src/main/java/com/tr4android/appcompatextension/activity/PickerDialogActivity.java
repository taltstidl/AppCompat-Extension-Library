package com.tr4android.appcompatextension.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tr4android.appcompatextension.R;
import com.tr4android.support.extension.drawable.PlaceholderDrawable;
import com.tr4android.support.extension.picker.date.AppCompatDatePicker;
import com.tr4android.support.extension.picker.date.AppCompatDatePickerDialog;
import com.tr4android.support.extension.picker.time.AppCompatTimePicker;
import com.tr4android.support.extension.picker.time.AppCompatTimePickerDialog;
import com.tr4android.support.extension.utils.ThemeUtils;

import java.util.Calendar;

public class PickerDialogActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker_dialog);

        // Setup toolbar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // for conversion from dp (dependent pixels)
        float dp = getResources().getDisplayMetrics().density;

        // Setup DatePickerDialog CardView header
        final Drawable tintedDatePickerDrawable = DrawableCompat.wrap(
                ContextCompat.getDrawable(this, R.drawable.ic_event_black_24dp));
        DrawableCompat.setTint(tintedDatePickerDrawable, Color.WHITE);
        PlaceholderDrawable placeholderDatePicker = new PlaceholderDrawable.Builder(this)
                .setPlaceholderImage(tintedDatePickerDrawable)
                .setPlaceholderImageSize(Math.round(24 * dp)).build();
        ((ImageView) findViewById(R.id.header_image_date)).setImageDrawable(placeholderDatePicker);

        // Setup TimePickerDialog CardView header
        final Drawable tintedTimePickerDrawable = DrawableCompat.wrap(
                ContextCompat.getDrawable(this, R.drawable.ic_schedule_black_24dp));
        DrawableCompat.setTint(tintedTimePickerDrawable, Color.WHITE);
        PlaceholderDrawable placeholderTimePicker = new PlaceholderDrawable.Builder(this)
                .setPlaceholderImage(tintedTimePickerDrawable)
                .setPlaceholderImageSize(Math.round(24 * dp)).build();
        ((ImageView) findViewById(R.id.header_image_time)).setImageDrawable(placeholderTimePicker);

        // Setup external link listener
        final String link = ((TextView) findViewById(R.id.href_link)).getText().toString();
        findViewById(R.id.external_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(link));
                startActivity(i);
            }
        });

        // Setup dialog launch listeners
        findViewById(R.id.buttonDateLight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show new light DatePickerDialog
                DialogFragment datePicker = new DatePickerLightFragment();
                datePicker.show(getSupportFragmentManager(), "datePicker");
            }
        });
        findViewById(R.id.buttonDateDark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show new dark DatePickerDialog
                DialogFragment datePicker = new DatePickerDarkFragment();
                datePicker.show(getSupportFragmentManager(), "datePicker");
            }
        });
        findViewById(R.id.buttonTimeLight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show new light TimePickerDialog
                DialogFragment timePicker = new TimePickerLightFragment();
                timePicker.show(getSupportFragmentManager(), "timePicker");
            }
        });
        findViewById(R.id.buttonTimeDark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show new dark TimePickerDialog
                DialogFragment timePicker = new TimePickerDarkFragment();
                timePicker.show(getSupportFragmentManager(), "timePicker");
            }
        });
    }

    public static class DatePickerLightFragment extends DialogFragment
            implements AppCompatDatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of AppCompatDatePickerDialog and return it
            return new AppCompatDatePickerDialog(getActivity(),
                    this, year, month, day);
        }

        public void onDateSet(AppCompatDatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
        }
    }

    public static class TimePickerLightFragment extends DialogFragment
            implements AppCompatTimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new AppCompatTimePickerDialog(getActivity(),
                    this, hour, minute, false);
        }

        public void onTimeSet(AppCompatTimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
        }
    }

    public static class DatePickerDarkFragment extends DialogFragment
            implements AppCompatDatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of AppCompatDatePickerDialog and return it
            return new AppCompatDatePickerDialog(getActivity(),
                    R.style.Theme_AppCompat_DatePickerDialog, this, year, month, day);
        }

        public void onDateSet(AppCompatDatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
        }
    }

    public static class TimePickerDarkFragment extends DialogFragment
            implements AppCompatTimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new AppCompatTimePickerDialog(getActivity(),
                    R.style.Theme_AppCompat_TimePickerDialog, this, hour, minute, false);
        }

        public void onTimeSet(AppCompatTimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
        }
    }
}
