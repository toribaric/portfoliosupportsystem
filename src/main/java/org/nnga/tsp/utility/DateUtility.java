package org.nnga.tsp.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtility {

    public static String getSimpleDate(Date date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static Date getDateFromString(String stringDate, String format) throws IllegalArgumentException {
        DateFormat dateFormat = new SimpleDateFormat(format);
        try {
            return dateFormat.parse(stringDate);
        } catch( ParseException e ) {
            throw new IllegalArgumentException("'" + stringDate + "' is an invalid date format");
        }
    }

    public static long getDaysDifferenceBetweenDates(Date firstDate, Date secondDate) {
        Calendar firstCalendar = Calendar.getInstance();
        Calendar secondCalendar = Calendar.getInstance();
        firstCalendar.setTime(firstDate);
        secondCalendar.setTime(secondDate);
        long millisecondsFirstDate = firstCalendar.getTimeInMillis();
        long millisecondsSecondDate = secondCalendar.getTimeInMillis();
        long difference = millisecondsSecondDate - millisecondsFirstDate;
        return difference / (24 * 60 * 60 * 1000);
    }

}
