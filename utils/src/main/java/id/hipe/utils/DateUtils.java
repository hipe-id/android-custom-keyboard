package id.hipe.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import timber.log.Timber;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by
 * Rochman Zaelani
 * im.rochmanz@gmail.com
 * on 03/04/18.
 */

public class DateUtils {
    public static final String FORMAT_DATE_1 = "EEE, d MMM yyyy";
    public static final String FORMAT_DATE_2 = "d MMM yyyy";
    public static final String FORMAT_DATE_3 = "dd/MM/yy";
    public static final String FORMAT_DATE_4 = "EEE, d MMM yyyy HH:mm";
    public static final String FORMAT_DATE_SERVER = "yyyy-MM-dd";
    public static final String FORMAT_DATE_SERVER_2 = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATE_SERVER_3 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final int CUT_OF_TIME = 15;

    public static String getCurrentDateTimeString() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE_SERVER_2);
            String currentTimeStamp = dateFormat.format(new Date()); // Find todays date
            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static String getRequestTimeDoku() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String currentTimeStamp = dateFormat.format(new Date()); // Find todays date
            return currentTimeStamp;
        } catch (Exception e) {
            Timber.e("getRequestTimeDoku(): %s", e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * get hour
     *
     * @param hours hour
     * @return string date time
     */
    public static String getHours(int hours) {
        DateTime dateTime = new DateTime();
        DateTime addedd = dateTime.plusHours(hours);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(addedd.toDate());
    }

    /**
     * get minutes
     *
     * @param minutes minutes after
     * @return minutes
     */
    public static String getMinutes(int minutes) {
        DateTime dateTime = new DateTime();
        DateTime addedd = dateTime.plusMinutes(minutes);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(addedd.toDate());
    }

    /**
     * @param date
     * @param format
     * @return
     */
    public static String formatDate(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static String formatStringDate(String stringDate, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(stringDate);
    }

    /**
     * convert string to date
     *
     * @param stringDate string date
     * @return Date
     */
    public static Date convertStringToDate(String stringDate, String format) {
        Timber.d("convertStringToDate() :  %s format %s", stringDate, format);
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            Date date = dateFormat.parse(stringDate);
            return date;
        } catch (ParseException e) {
            Timber.e("error : %s", e.getMessage());
        }
        return null;
    }

    public static String formatDateTime(DateTime dateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
        return formatter.print(dateTime);
    }

    public static DateTime convertDateStringToJodaTime(String date, String format) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
        return formatter.parseDateTime(date);
    }

    public static DateTime getTimeWithCutOffTime(DateTime date) {
        final int timeOfDay = date.getHourOfDay();
        if (timeOfDay >= 0 && timeOfDay < CUT_OF_TIME) {
            date = date.plusDays(1).withTimeAtStartOfDay();
            Timber.d("onCreate(): set date +1 become: " + date.getDayOfWeek());
        } else if (timeOfDay >= CUT_OF_TIME) {
            date = date.plusDays(2).withTimeAtStartOfDay();
            Timber.d("onCreate(): set date +2 become : " + date.getDayOfWeek());
        }
        return date;
    }

    public static int getDifferenceDays(Date d1, Date d2) {
        int daysdiff = 0;
        long diff = d2.getTime() - d1.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000) + 1;
        daysdiff = (int) diffDays;
        return daysdiff;
    }
}
