package com.tr4android.support.extension.picker.date;

import android.os.Build;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateFormatUtils {

    public static String getBestDateTimePattern(Locale locale, String skeleton) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return DateFormat.getBestDateTimePattern(locale, skeleton);
        } else {
            // Try to improve the skeleton on older devices
            if (skeleton.equals("EMMMd")) return "E, MMM d";
            if (skeleton.equals("MMMMy")) return "MMMM yyyy";
            return skeleton;
        }
    }

    public static String formatDayOfWeek(SimpleDateFormat formatter, Calendar calendar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return formatter.format(calendar.getTime());
        } else {
            // Use DateUtils on older devices (Saturday = 7)
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            return DateUtils.getDayOfWeekString((dayOfWeek == 0) ? 7 : dayOfWeek, DateUtils.LENGTH_SHORTEST);
        }
    }

    public static String format(SimpleDateFormat formatter, Calendar calendar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return formatter.format(calendar.getTime());
        } else {
            return DateFormat.format(formatter.toLocalizedPattern(), calendar).toString();
        }
    }
}
