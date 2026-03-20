package com.example.myapplication.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {
    private static final String DATE_PATTERN = "EEE, MMM d yyyy 'at' h:mm a";

    private DateUtils() {
    }

    public static String epochToReadable(long epochMs) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());
        return format.format(new Date(epochMs));
    }

    public static long readableToEpoch(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());
        try {
            Date parsedDate = format.parse(dateString);
            return parsedDate != null ? parsedDate.getTime() : 0L;
        } catch (ParseException e) {
            return 0L;
        }
    }
}
