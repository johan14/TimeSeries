package com.rayonit.times.util;

import com.rayonit.times.configuration.MongoConf;
import com.rayonit.times.hierarchic.levels.BaseLevel;
import com.rayonit.times.hierarchic.levels.HierarchicLevel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class DateUtil {

    private static final int SECOND = 1;
    private static final int MINUTE = 1;
    private static final int HOUR = 1;
    @Autowired
    MongoConf mongoConf;



    public static Date setTimeOfDateToTime(Date date, int time) {

        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(date);

        if (time > MINUTE)
            calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        date = calendar.getTime();
        return date;
    }

    public static Date setTimeHierarchic(Date date, HierarchicLevel level, boolean timezoneSetting) {
        Calendar calendar;
        if (timezoneSetting) {
            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            calendar = Calendar.getInstance(timeZone);
        } else calendar = Calendar.getInstance();
        calendar.setTime(date);

        switch (level) {
            case DAY_LEVEL: {

                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                break;
            }
            case HOUR_LEVEL: {
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                break;
            }
            case MINUTE_LEVEL: {
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                break;
            }
        }
        date = calendar.getTime();
        return date;
    }

    public static Date toUTC(Date date) {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(date);

        return calendar.getTime();

    }

    public static Date addSec(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, SECOND);
        return calendar.getTime();
    }

    public static Date addMin(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, MINUTE);
        return calendar.getTime();
    }

    public static Date addHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, HOUR);
        return calendar.getTime();
    }

    public static Integer findKey(Date date, BaseLevel level) {
        switch (level) {
            case HOUR:
                TimeZone timeZone = TimeZone.getTimeZone("UTC");
                Calendar cal = Calendar.getInstance(timeZone);
                cal.setTime(date);
                return cal.get(Calendar.HOUR_OF_DAY);
            case MINUTE:
                cal = Calendar.getInstance();
                cal.setTime(date);
                return cal.get(Calendar.MINUTE);
            case SECOND:
                cal = Calendar.getInstance();
                cal.setTime(date);
                return cal.get(Calendar.SECOND);

        }
        return 0;
    }

    public static Integer[] convertDateToArray(Date date) {

        Integer[] integers = new Integer[3];
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(date);

        integers[0] = calendar.get(Calendar.SECOND);
        integers[1] = calendar.get(Calendar.MINUTE);
        integers[2] = calendar.get(Calendar.HOUR_OF_DAY);

        return integers;
    }


}
