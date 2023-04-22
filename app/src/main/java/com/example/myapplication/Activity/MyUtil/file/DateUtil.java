package com.example.myapplication.Activity.MyUtil.file;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);
        return sdf.format(date);
    }

    public static Date parse(String strDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);
        return sdf.parse(strDate);
    }

    public static String AM_PM(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh", Locale.CHINA);
        return sdf.format(date).compareTo("12") < 0 ? "am" : "pm";
    }

    public static String Current_time() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }
}
